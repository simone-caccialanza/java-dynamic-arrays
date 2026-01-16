package dynarrays;

import jdk.internal.util.ArraysSupport;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ArenaDynArray<T> implements List<T> {

    private static final short DEFAULT_START_CAPACITY = 8;
    private static final Map<Class<?>, ValueLayout> ALLOWED_LAYOUTS_MAP = new HashMap<>();

    private enum TypeConstant {
        INTEGER_PRIMITIVE(int.class, ValueLayout.JAVA_INT, 0),
        INTEGER_WRAPPER(Integer.class, ValueLayout.JAVA_INT, 0),

        LONG_PRIMITIVE(long.class, ValueLayout.JAVA_LONG, 0L),
        LONG_WRAPPER(Long.class, ValueLayout.JAVA_LONG, 0L),

        FLOAT_PRIMITIVE(float.class, ValueLayout.JAVA_FLOAT, 0f),
        FLOAT_WRAPPER(Float.class, ValueLayout.JAVA_FLOAT, 0f),

        DOUBLE_PRIMITIVE(double.class, ValueLayout.JAVA_DOUBLE, 0d),
        DOUBLE_WRAPPER(Double.class, ValueLayout.JAVA_DOUBLE, 0d),

        BOOLEAN_PRIMITIVE(boolean.class, ValueLayout.JAVA_BYTE, false),
        BOOLEAN_WRAPPER(Boolean.class, ValueLayout.JAVA_BYTE, false),

        CHAR_PRIMITIVE(char.class, ValueLayout.JAVA_CHAR, '\0'),
        CHAR_WRAPPER(Character.class, ValueLayout.JAVA_CHAR, '\0');

        final Class<?> type;
        final ValueLayout layout;
        final Object zero;

        TypeConstant(Class<?> type, ValueLayout layout, Object zero) {
            this.type = type;
            this.layout = layout;
            this.zero = zero;
        }

        private static final java.util.Map<Class<?>, TypeConstant> BY_TYPE =
                java.util.Arrays.stream(values())
                        .collect(java.util.stream.Collectors.toMap(tc -> tc.type, tc -> tc));

        static TypeConstant getBy(Class<?> clazz) {
            TypeConstant tc = BY_TYPE.get(clazz);
            if (tc == null) {
                throw new UnsupportedOperationException("Unsupported type " + clazz);
            }
            return tc;
        }

        @SuppressWarnings("unchecked")
        <T> T zero() {
            return (T) zero;
        }
    }

    static {
        ALLOWED_LAYOUTS_MAP.put(int.class, ValueLayout.JAVA_INT);
        ALLOWED_LAYOUTS_MAP.put(Integer.class, ValueLayout.JAVA_INT);

        ALLOWED_LAYOUTS_MAP.put(long.class, ValueLayout.JAVA_LONG);
        ALLOWED_LAYOUTS_MAP.put(Long.class, ValueLayout.JAVA_LONG);

        ALLOWED_LAYOUTS_MAP.put(float.class, ValueLayout.JAVA_FLOAT);
        ALLOWED_LAYOUTS_MAP.put(Float.class, ValueLayout.JAVA_FLOAT);

        ALLOWED_LAYOUTS_MAP.put(double.class, ValueLayout.JAVA_DOUBLE);
        ALLOWED_LAYOUTS_MAP.put(Double.class, ValueLayout.JAVA_DOUBLE);

        ALLOWED_LAYOUTS_MAP.put(boolean.class, ValueLayout.JAVA_BYTE);
        ALLOWED_LAYOUTS_MAP.put(Boolean.class, ValueLayout.JAVA_BYTE);

        ALLOWED_LAYOUTS_MAP.put(char.class, ValueLayout.JAVA_CHAR);
        ALLOWED_LAYOUTS_MAP.put(Character.class, ValueLayout.JAVA_CHAR);

//        ALLOWED_LAYOUTS_MAP.put(void.class, MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_BYTE));
//        ALLOWED_LAYOUTS_MAP.put(Void.class, MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_BYTE));

        ALLOWED_LAYOUTS_MAP.put(String.class, ValueLayout.ADDRESS);
    }

    private final Arena arena;
    private final long capacity;
    private final Class<T> clazz;
    private final ValueLayout layout;
    private final IntFunction<T> reader;
    private final BiConsumer<T, Integer> setter;
    private MemorySegment nativeValues;
    private final TypeConstant typeConstant;
    private int size = 0;

    public ArenaDynArray(Class<T> clazz) {
        this(clazz, DEFAULT_START_CAPACITY);
    }

    public ArenaDynArray(Class<T> clazz, long startCapacity) {
        this(clazz, startCapacity, MemoryManagerType.SHARED);
    }

    public ArenaDynArray(Class<T> clazz, long startCapacity, MemoryManagerType memoryManager) {
        if (!ALLOWED_LAYOUTS_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Only primitive and wrapper types are allowed");
        }
        this.clazz = clazz;
        this.layout = ALLOWED_LAYOUTS_MAP.get(clazz);

        if (startCapacity <= 0) {
            throw new IllegalArgumentException("Start length must be positive");
        }
        this.capacity = startCapacity;

        switch (memoryManager) {
            case GLOBAL -> arena = Arena.global();
            case CONFINED -> arena = Arena.ofConfined();
            case SHARED -> arena = Arena.ofShared();
            default -> throw new IllegalArgumentException("Unsupported memory manager type " + memoryManager);
        }
        if (arena == null) {
            throw new RuntimeException("Could not allocate Arena");
        }

        MemoryLayout memoryLayout =
                MemoryLayout.sequenceLayout(startCapacity, layout);

        reader = getValueReader();
        setter = getValueSetter();

        typeConstant = TypeConstant.getBy(clazz);

        nativeValues = arena.allocate(memoryLayout.byteSize(), memoryLayout.byteAlignment());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (!(this.clazz.isAssignableFrom(o.getClass()))) {
            throw new IllegalArgumentException("Parameter of contains(Object) is not of type " + this.clazz);
        }
        if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }
        T t = clazz.cast(o);
        for (int i = 0; i < size; i++) {
            if (t.equals(get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }
        return new SimpleIterator<>();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
        if (size == 0) return;
        assertSupportedOperation();

        for (int i = 0; i < size; i++) {
            action.accept(get(i));
        }

    }

    private void assertSupportedOperation() {
        if (!ALLOWED_LAYOUTS_MAP.containsKey(clazz))
            throw new UnsupportedOperationException("Unsupported type " + clazz);
    }

    @Override
    public Object[] toArray() {
        assertSupportedOperation();
        if (size == 0) return new Object[0];
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            result[i] = get(i);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(T1[] a) {
        if (a == null) throw new IllegalArgumentException("Array must not be null");
        if (!(clazz.isAssignableFrom(a.getClass().getComponentType())))
            throw new IllegalArgumentException("Array must be of type " + clazz.getName());
        T1[] r = a.length >= size ? a :
                (T1[]) java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);
        Iterator<T> it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                if (a == r) {
                    r[i] = null; // null-terminate
                } else if (a.length < i) {
                    return Arrays.copyOf(r, i);
                } else {
                    System.arraycopy(r, 0, a, 0, i);
                    if (a.length > i) {
                        a[i] = null;
                    }
                }
                return a;
            }
            r[i] = (T1) it.next();
        }
        // more elements than expected
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int len = r.length;
        int i = len;
        while (it.hasNext()) {
            if (i == len) {
                len = ArraysSupport.newLength(len,
                        1,             /* minimum growth */
                        (len >> 1) + 1 /* preferred growth */);
                r = Arrays.copyOf(r, len);
            }
            r[i++] = (T) it.next();
        }
        // trim if overallocated
        return (i == len) ? r : Arrays.copyOf(r, i);
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return toArray(generator.apply(0));
    }

    @Override
    public boolean add(T element) {
        add(size, element);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!(this.clazz.isAssignableFrom(o.getClass()))) {
            throw new IllegalArgumentException("Parameter of contains(Object) is not of type " + this.clazz);
        }
        T t = clazz.cast(o);
        for (int i = 0; i < size; i++) {
            if (t.equals(get(i))) {
                shiftLeftValuesAtIndex(i);
                size--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            add(t);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        boolean modified = false;
        int insertIndex = index;
        for (T t : c) {
            add(insertIndex++, t);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            while (remove(o)) {
            }
        }
        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        if (filter == null) throw new IllegalArgumentException();
        boolean removed = false;
        if (size == 0) return false;

        int i = 0;
        while (i < size) {
            final T value = get(i);

            if (filter.test(value)) {
                shiftLeftValuesAtIndex(i);

                final int lastIndex = size - 1;
                setter.accept(typeConstant.zero(), lastIndex);

                size--;
                removed = true;
                // do not increment i:
                // new element at index i must be checked
            } else {
                i++;
            }
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void sort(Comparator<? super T> c) {
        //TODO refactor here
        if (c == null) throw new NullPointerException();
        if (size == 0) return;
        if (clazz == int.class || clazz == Integer.class) {
            IntSort.quickSort(nativeValues, 0, size - 1, (Comparator<? super Integer>) c);
        } else if (clazz == long.class || clazz == Long.class) {
            LongSort.quickSort(nativeValues, 0, size - 1, (Comparator<? super Long>) c);
        } else if (clazz == float.class || clazz == Float.class) {
            FloatSort.quickSort(nativeValues, 0, size - 1, (Comparator<? super Float>) c);
        } else if (clazz == double.class || clazz == Double.class) {
            DoubleSort.quickSort(nativeValues, 0, size - 1, (Comparator<? super Double>) c);
        } else if (clazz == char.class || clazz == Character.class) {
            CharSort.quickSort(nativeValues, 0, size - 1, (Comparator<? super Character>) c);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            BooleanSort.sort(nativeValues, size);
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return reader.apply(index);
    }

    @Override
    public T set(int index, T element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        checkSizeAndRealloc();
        T oldValue = get(index);
        setter.accept(element, index);
        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        checkSizeAndRealloc();
        shiftRightValuesAtIndex(index);
        setter.accept(element, index);
        size++;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, index));
        MemorySegment.copy(nativeValues, (index + 1) * layout.byteSize(), nativeValues, index * layout.byteSize(), (size - index - 1) * layout.byteSize());
        nativeValues.setAtIndex((ValueLayout.OfInt) layout, size - 1, 0);
        size--;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        if(o == null){
            for (int i = 0; i < size; i++) {
                if (get(i) == null) {
                    return i;
                }
            }
        } else {
            T t = clazz.cast(o);
            if (!(this.clazz.isAssignableFrom(o.getClass()))) {
                throw new IllegalArgumentException("Parameter of indexOf(Object) is not of type " + this.clazz);
            }
            for (int i = 0; i < size; i++) {
                if (t.equals(get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (get(i) == null) {
                    return i;
                }
            }
        } else {
            if (!(this.clazz.isAssignableFrom(o.getClass()))) {
                throw new IllegalArgumentException("Parameter of lastIndexOf(Object) is not of type " + this.clazz);
            }
            T t = clazz.cast(o);
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(get(i))) {
                    return i;
                }
            }

        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListIterator<T>() {
            private int cursor = 0;
            private int lastCursor = 0;

            @Override
            public boolean hasNext() {
                return cursor != size;
            }

            @Override
            public T next() {
                if (cursor >= size) {
                    throw new NoSuchElementException();
                }
                lastCursor = cursor;
                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public T previous() {
                if (cursor <= 0) {
                    throw new NoSuchElementException();
                }
                lastCursor = cursor;
                return get(--cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                ArenaDynArray.this.remove(lastCursor);
            }

            @Override
            public void set(T t) {
                if (cursor <= 0) {
                    throw new IllegalStateException();
                }
                ArenaDynArray.this.set(lastCursor, t);
            }

            @Override
            public void add(T t) {
                ArenaDynArray.this.add(t);
            }
        };
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        return new ListIterator<T>() {
            private int cursor = index;
            private int lastCursor = index;

            @Override
            public boolean hasNext() {
                return cursor != size;
            }

            @Override
            public T next() {
                if (cursor >= size) {
                    throw new NoSuchElementException();
                }
                lastCursor = cursor;
                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public T previous() {
                if (cursor <= 0) {
                    throw new NoSuchElementException();
                }
                lastCursor = cursor;
                return get(--cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                ArenaDynArray.this.remove(lastCursor);
            }

            @Override
            public void set(T t) {
                if (cursor <= 0) {
                    throw new IllegalStateException();
                }
                ArenaDynArray.this.set(lastCursor, t);
            }

            @Override
            public void add(T t) {
                ArenaDynArray.this.add(cursor++, t);
            }
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        ArenaDynArray<T> subList = new ArenaDynArray<>(clazz, (short) (toIndex - fromIndex));
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(getIntAtIndex(i));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(getLongAtIndex(i));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(getFloatAtIndex(i));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(getDoubleAtIndex(i));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(getBooleanAtIndex(i));
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(getCharAtIndex(i));
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }
        return subList;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Spliterator<T> spliterator() {
        if (clazz == int.class || clazz == Integer.class) {
            Spliterator.OfInt ofInt = new Spliterators.AbstractIntSpliterator(
                    size,
                    Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
            ) {
                int index = 0;

                @Override
                public boolean tryAdvance(IntConsumer action) {
                    if (index >= size) return false;
                    int value = nativeValues.getAtIndex((ValueLayout.OfInt) layout, index++);
                    action.accept(value);
                    return true;
                }

                @Override
                public Spliterator.OfInt trySplit() {
                    int remaining = size - index;
                    if (remaining < 2) return null;
                    int mid = index + remaining / 2;
                    Spliterator.OfInt left = new Spliterators.AbstractIntSpliterator(
                            mid - index,
                            Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
                    ) {
                        int local = index;

                        @Override
                        public boolean tryAdvance(IntConsumer action) {
                            if (local >= mid) return false;
                            int v = nativeValues.getAtIndex((ValueLayout.OfInt) layout, local++);
                            action.accept(v);
                            return true;
                        }
                    };
                    index = mid;
                    return left;
                }

                @Override
                public long estimateSize() {
                    return size - index;
                }
            };
            return (Spliterator) ofInt;
        }

        if (clazz == long.class || clazz == Long.class) {
            Spliterator.OfLong ofLong = new Spliterators.AbstractLongSpliterator(
                    size,
                    Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
            ) {
                int index = 0;

                @Override
                public boolean tryAdvance(LongConsumer action) {
                    if (index >= size) return false;
                    long value = nativeValues.getAtIndex((ValueLayout.OfLong) layout, index++);
                    action.accept(value);
                    return true;
                }

                @Override
                public Spliterator.OfLong trySplit() {
                    int remaining = size - index;
                    if (remaining < 2) return null;
                    int mid = index + remaining / 2;
                    Spliterator.OfLong left = new Spliterators.AbstractLongSpliterator(
                            mid - index,
                            Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
                    ) {
                        int local = index;

                        @Override
                        public boolean tryAdvance(LongConsumer action) {
                            if (local >= mid) return false;
                            long v = nativeValues.getAtIndex((ValueLayout.OfLong) layout, local++);
                            action.accept(v);
                            return true;
                        }
                    };
                    index = mid;
                    return left;
                }

                @Override
                public long estimateSize() {
                    return size - index;
                }
            };
            return (Spliterator) ofLong;
        }

        if (clazz == double.class || clazz == Double.class) {
            Spliterator.OfDouble ofDouble = new Spliterators.AbstractDoubleSpliterator(
                    size,
                    Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
            ) {
                int index = 0;

                @Override
                public boolean tryAdvance(DoubleConsumer action) {
                    if (index >= size) return false;
                    double value = nativeValues.getAtIndex((ValueLayout.OfDouble) layout, index++);
                    action.accept(value);
                    return true;
                }

                @Override
                public Spliterator.OfDouble trySplit() {
                    int remaining = size - index;
                    if (remaining < 2) return null;
                    int mid = index + remaining / 2;
                    Spliterator.OfDouble left = new Spliterators.AbstractDoubleSpliterator(
                            mid - index,
                            Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
                    ) {
                        int local = index;

                        @Override
                        public boolean tryAdvance(DoubleConsumer action) {
                            if (local >= mid) return false;
                            double v = nativeValues.getAtIndex((ValueLayout.OfDouble) layout, local++);
                            action.accept(v);
                            return true;
                        }
                    };
                    index = mid;
                    return left;
                }

                @Override
                public long estimateSize() {
                    return size - index;
                }
            };
            return (Spliterator) ofDouble;
        }

        if (clazz == float.class || clazz == Float.class) {
            // use OfDouble by converting float->double inside stream OR implement a dedicated OfDouble/OfObject approach
            Spliterator.OfDouble ofFloatAsDouble = new Spliterators.AbstractDoubleSpliterator(
                    size,
                    Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
            ) {
                int index = 0;

                @Override
                public boolean tryAdvance(DoubleConsumer action) {
                    if (index >= size) return false;
                    float value = nativeValues.getAtIndex((ValueLayout.OfFloat) layout, index++);
                    action.accept(value);
                    return true;
                }

                @Override
                public Spliterator.OfDouble trySplit() {
                    int remaining = size - index;
                    if (remaining < 2) return null;
                    int mid = index + remaining / 2;
                    Spliterator.OfDouble left = new Spliterators.AbstractDoubleSpliterator(
                            mid - index,
                            Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
                    ) {
                        int local = index;

                        @Override
                        public boolean tryAdvance(DoubleConsumer action) {
                            if (local >= mid) return false;
                            float v = nativeValues.getAtIndex((ValueLayout.OfFloat) layout, local++);
                            action.accept(v);
                            return true;
                        }
                    };
                    index = mid;
                    return left;
                }

                @Override
                public long estimateSize() {
                    return size - index;
                }
            };
            return (Spliterator) ofFloatAsDouble;
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            Spliterator<Boolean> booleans = new Spliterators.AbstractSpliterator<Boolean>(
                    size,
                    Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
            ) {
                int index = 0;

                @Override
                public boolean tryAdvance(Consumer<? super Boolean> action) {
                    if (index >= size) return false;
                    boolean value = nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, index++);
                    action.accept(Boolean.valueOf(value));
                    return true;
                }
            };
            return (Spliterator) booleans;
        }

        if (clazz == char.class || clazz == Character.class) {
            Spliterator<Character> chars = new Spliterators.AbstractSpliterator<Character>(
                    size,
                    Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE
            ) {
                int index = 0;

                @Override
                public boolean tryAdvance(Consumer<? super Character> action) {
                    if (index >= size) return false;
                    char value = nativeValues.getAtIndex((ValueLayout.OfChar) layout, index++);
                    action.accept(Character.valueOf(value)); // boxing unavoidable for generics
                    return true;
                }
            };
            return (Spliterator) chars;
        }

        return new Spliterators.AbstractSpliterator<T>(
                size,
                Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED
        ) {
            private int index = 0;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if (index >= size) {
                    return false;
                }
                action.accept(ArenaDynArray.this.get(index++));
                return true;
            }
        };
    }

    @Override
    public void addFirst(T t) {
        List.super.addFirst(t);
    }

    @Override
    public void addLast(T t) {
        List.super.addLast(t);
    }

    @Override
    public T getFirst() {
        return List.super.getFirst();
    }

    @Override
    public T getLast() {
        return List.super.getLast();
    }

    @Override
    public T removeFirst() {
        return List.super.removeFirst();
    }

    @Override
    public T removeLast() {
        return List.super.removeLast();
    }

    @Override
    public List<T> reversed() {
        throw new UnsupportedOperationException("Can't reverse " + this.getClass().getSimpleName());
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    private void checkSizeAndRealloc() {
        if (size == capacity) {
            MemorySegment newNativeValues = arena.allocate(layout.byteSize(), layout.byteAlignment());
            MemorySegment.copy(nativeValues, 0, newNativeValues, 0, nativeValues.byteSize());
            nativeValues = newNativeValues;
        }
    }

    private void shiftLeftValuesAtIndex(int i) {
        MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
    }

    private void shiftRightValuesAtIndex(int i) {
        MemorySegment.copy(nativeValues, i * layout.byteSize(), nativeValues, (i + 1) * layout.byteSize(), (size - i) * layout.byteSize());
    }

    private T getIntAtIndex(int i) {
        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i));
    }

    private T getLongAtIndex(int i) {
        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i));
    }

    private T getFloatAtIndex(int i) {
        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i));
    }

    private T getDoubleAtIndex(int i) {
        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i));
    }

    private T getCharAtIndex(int i) {
        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i));
    }

    private T getBooleanAtIndex(int i) {
        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i));
    }

    private IntFunction<T> getValueReader() {
        if (clazz == int.class || clazz == Integer.class) return this::getIntAtIndex;
        if (clazz == long.class || clazz == Long.class) return this::getLongAtIndex;
        if (clazz == float.class || clazz == Float.class) return this::getFloatAtIndex;
        if (clazz == double.class || clazz == Double.class) return this::getDoubleAtIndex;
        if (clazz == boolean.class || clazz == Boolean.class) return this::getBooleanAtIndex;
        if (clazz == char.class || clazz == Character.class) return this::getCharAtIndex;

        if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }
        throw new UnsupportedOperationException("Unsupported type " + clazz);
    }

    private void setIntAtIndex(T n, Integer i) {
        nativeValues.setAtIndex((ValueLayout.OfInt) layout, i, (int) n);
    }

    private void setLongAtIndex(T l, Integer i) {
        nativeValues.setAtIndex((ValueLayout.OfLong) layout, i, (long) l);
    }

    private void setFloatAtIndex(T f, Integer i) {
        nativeValues.setAtIndex((ValueLayout.OfFloat) layout, i, (float) f);
    }

    private void setDoubleAtIndex(T d, Integer i) {
        nativeValues.setAtIndex((ValueLayout.OfDouble) layout, i, (double) d);
    }

    private void setBooleanAtIndex(T b, Integer i) {
        nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, i, (boolean) b);
    }

    private void setCharAtIndex(T c, Integer i) {
        nativeValues.setAtIndex((ValueLayout.OfChar) layout, i, (char) c);
    }

    private BiConsumer<T, Integer> getValueSetter() {
        if (clazz == int.class || clazz == Integer.class) return this::setIntAtIndex;
        if (clazz == long.class || clazz == Long.class) return this::setLongAtIndex;
        if (clazz == float.class || clazz == Float.class) return this::setFloatAtIndex;
        if (clazz == double.class || clazz == Double.class) return this::setDoubleAtIndex;
        if (clazz == boolean.class || clazz == Boolean.class) return this::setBooleanAtIndex;
        if (clazz == char.class || clazz == Character.class) return this::setCharAtIndex;

        if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }
        throw new UnsupportedOperationException("Unsupported type " + clazz);
    }


    public enum MemoryManagerType {
        SHARED,
        CONFINED,
        GLOBAL
    }

    private class IntSort {
        private static final ValueLayout layout = ValueLayout.JAVA_INT;

        private static int partition(MemorySegment arr, int low, int high, Comparator<? super Integer> c) {

            int pivot = arr.getAtIndex((ValueLayout.OfInt) layout, high);
            int i = low - 1;

            for (int j = low; j <= high - 1; j++) {
                if (c.compare(arr.getAtIndex((ValueLayout.OfInt) layout, j), pivot) < 0) {
                    i++;
                    swap(arr, i, j);
                }
            }

            swap(arr, i + 1, high);
            return i + 1;
        }

        private static void swap(MemorySegment arr, int i, int j) {
            int temp = arr.getAtIndex((ValueLayout.OfInt) layout, i);
            arr.setAtIndex((ValueLayout.OfInt) layout, i, (arr.getAtIndex((ValueLayout.OfInt) layout, j)));
            arr.setAtIndex((ValueLayout.OfInt) layout, j, temp);
        }

        public static void quickSort(MemorySegment arr, int low, int high, Comparator<? super Integer> c) {
            if (low < high) {

                int pi = partition(arr, low, high, c);

                quickSort(arr, low, pi - 1, c);
                quickSort(arr, pi + 1, high, c);
            }
        }
    }

    private class LongSort {
        private static final ValueLayout layout = ValueLayout.JAVA_LONG;

        private static int partition(MemorySegment arr, int low, int high, Comparator<? super Long> c) {
            long pivot = arr.getAtIndex((ValueLayout.OfLong) layout, high);
            int i = low - 1;
            for (int j = low; j <= high - 1; j++) {
                if (c.compare(arr.getAtIndex((ValueLayout.OfLong) layout, j), pivot) < 0) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            return i + 1;
        }

        private static void swap(MemorySegment arr, int i, int j) {
            long temp = arr.getAtIndex((ValueLayout.OfLong) layout, i);
            arr.setAtIndex((ValueLayout.OfLong) layout, i, arr.getAtIndex((ValueLayout.OfLong) layout, j));
            arr.setAtIndex((ValueLayout.OfLong) layout, j, temp);
        }

        public static void quickSort(MemorySegment arr, int low, int high, Comparator<? super Long> c) {
            if (low < high) {
                int pi = partition(arr, low, high, c);
                quickSort(arr, low, pi - 1, c);
                quickSort(arr, pi + 1, high, c);
            }
        }
    }

    private class FloatSort {
        private static final ValueLayout layout = ValueLayout.JAVA_FLOAT;

        private static int partition(MemorySegment arr, int low, int high, Comparator<? super Float> c) {
            float pivot = arr.getAtIndex((ValueLayout.OfFloat) layout, high);
            int i = low - 1;
            for (int j = low; j <= high - 1; j++) {
                if (c.compare(arr.getAtIndex((ValueLayout.OfFloat) layout, j), pivot) < 0) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            return i + 1;
        }

        private static void swap(MemorySegment arr, int i, int j) {
            float temp = arr.getAtIndex((ValueLayout.OfFloat) layout, i);
            arr.setAtIndex((ValueLayout.OfFloat) layout, i, arr.getAtIndex((ValueLayout.OfFloat) layout, j));
            arr.setAtIndex((ValueLayout.OfFloat) layout, j, temp);
        }

        public static void quickSort(MemorySegment arr, int low, int high, Comparator<? super Float> c) {
            if (low < high) {
                int pi = partition(arr, low, high, c);
                quickSort(arr, low, pi - 1, c);
                quickSort(arr, pi + 1, high, c);
            }
        }
    }

    private class DoubleSort {
        private static final ValueLayout layout = ValueLayout.JAVA_DOUBLE;

        private static int partition(MemorySegment arr, int low, int high, Comparator<? super Double> c) {
            double pivot = arr.getAtIndex((ValueLayout.OfDouble) layout, high);
            int i = low - 1;
            for (int j = low; j <= high - 1; j++) {
                if (c.compare(arr.getAtIndex((ValueLayout.OfDouble) layout, j), pivot) < 0) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            return i + 1;
        }

        private static void swap(MemorySegment arr, int i, int j) {
            double temp = arr.getAtIndex((ValueLayout.OfDouble) layout, i);
            arr.setAtIndex((ValueLayout.OfDouble) layout, i, arr.getAtIndex((ValueLayout.OfDouble) layout, j));
            arr.setAtIndex((ValueLayout.OfDouble) layout, j, temp);
        }

        public static void quickSort(MemorySegment arr, int low, int high, Comparator<? super Double> c) {
            if (low < high) {
                int pi = partition(arr, low, high, c);
                quickSort(arr, low, pi - 1, c);
                quickSort(arr, pi + 1, high, c);
            }
        }
    }

    private class CharSort {
        private static final ValueLayout layout = ValueLayout.JAVA_CHAR;

        private static int partition(MemorySegment arr, int low, int high, Comparator<? super Character> c) {
            char pivot = arr.getAtIndex((ValueLayout.OfChar) layout, high);
            int i = low - 1;
            for (int j = low; j <= high - 1; j++) {
                if (c.compare(arr.getAtIndex((ValueLayout.OfChar) layout, j), pivot) < 0) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            return i + 1;
        }

        private static void swap(MemorySegment arr, int i, int j) {
            char temp = arr.getAtIndex((ValueLayout.OfChar) layout, i);
            arr.setAtIndex((ValueLayout.OfChar) layout, i, arr.getAtIndex((ValueLayout.OfChar) layout, j));
            arr.setAtIndex((ValueLayout.OfChar) layout, j, temp);
        }

        public static void quickSort(MemorySegment arr, int low, int high, Comparator<? super Character> c) {
            if (low < high) {
                int pi = partition(arr, low, high, c);
                quickSort(arr, low, pi - 1, c);
                quickSort(arr, pi + 1, high, c);
            }
        }
    }

    private class BooleanSort {
        private static final ValueLayout layout = ValueLayout.JAVA_BOOLEAN;

        public static void sort(MemorySegment arr, int size) {
            for (int i = 0, j = size - 1; i != j; ) {
                if (arr.getAtIndex((ValueLayout.OfBoolean) layout, i)) {
                    if (!arr.getAtIndex((ValueLayout.OfBoolean) layout, j)) {
                        arr.setAtIndex((ValueLayout.OfBoolean) layout, i, false);
                        arr.setAtIndex((ValueLayout.OfBoolean) layout, j, true);
                    }
                    j--;
                    continue;
                }
                i++;
            }
        }

    }

    protected class SimpleIterator<R extends T> implements Iterator<T> { //TODO fixme

        protected int index = 0;

        @Override
        public final boolean hasNext() {
            return index < size;
        }

        @Override
        public final T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return nextElement(index++);
        }

        protected T nextElement(int index) {
            return get(index);
        }
    }

}
