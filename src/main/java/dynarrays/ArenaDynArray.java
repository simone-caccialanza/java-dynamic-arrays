package dynarrays;

import jdk.internal.util.ArraysSupport;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class ArenaDynArray<T> implements List<T> {

    //TODO implement string
    //TODO implement void
    //TODO check parent class documentation and throw the requested exceptions
    //TODO can I implement sort()?

    private static final short DEFAULT_START_CAPACITY = 8;

    private enum TypeConstant {
        INTEGER_PRIMITIVE(int.class, ValueLayout.JAVA_INT, 0),
        INTEGER_WRAPPER(Integer.class, ValueLayout.JAVA_INT, 0),

        LONG_PRIMITIVE(long.class, ValueLayout.JAVA_LONG, 0L),
        LONG_WRAPPER(Long.class, ValueLayout.JAVA_LONG, 0L),

        FLOAT_PRIMITIVE(float.class, ValueLayout.JAVA_FLOAT, 0f),
        FLOAT_WRAPPER(Float.class, ValueLayout.JAVA_FLOAT, 0f),

        DOUBLE_PRIMITIVE(double.class, ValueLayout.JAVA_DOUBLE, 0d),
        DOUBLE_WRAPPER(Double.class, ValueLayout.JAVA_DOUBLE, 0d),

        BOOLEAN_PRIMITIVE(boolean.class, ValueLayout.JAVA_BOOLEAN, false),
        BOOLEAN_WRAPPER(Boolean.class, ValueLayout.JAVA_BOOLEAN, false),

        CHAR_PRIMITIVE(char.class, ValueLayout.JAVA_CHAR, '\0'),
        CHAR_WRAPPER(Character.class, ValueLayout.JAVA_CHAR, '\0');

        final Class<?> type;
        final ValueLayout layout;
        private final Object zero;

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
                throw new UnsupportedDynArrayTypeException(clazz);
            }
            return tc;
        }

        @SuppressWarnings("unchecked")
        <T> T zero() {
            return (T) zero;
        }
    }

    private final Arena arena;
    private final Class<T> clazz;
    private final ValueLayout layout;
    private final IntFunction<T> reader;
    private final BiConsumer<T, Integer> setter;
    private final T zero;
    private MemorySegment nativeValues;
    private long capacity;
    private int size = 0;

    public ArenaDynArray(Class<T> clazz) {
        this(clazz, DEFAULT_START_CAPACITY);
    }

    public ArenaDynArray(Class<T> clazz, long startCapacity) {
        this(clazz, startCapacity, MemoryManagerType.SHARED);
    }

    public ArenaDynArray(Class<T> clazz, long startCapacity, MemoryManagerType memoryManager) {
        //TODO refactor constructor to be more clear and less complex
        this.clazz = clazz;
        this.layout = TypeConstant.getBy(clazz).layout;
        this.zero = TypeConstant.getBy(clazz).zero();

        if (startCapacity < 0) {
            throw new IllegalArgumentException("Start length must be non negative");
        }
        this.capacity = startCapacity;

        switch (memoryManager) {
            case GLOBAL -> arena = Arena.global();
            case CONFINED -> arena = Arena.ofConfined();
            case SHARED -> arena = Arena.ofShared();
            default -> throw new IllegalArgumentException("Unsupported memory manager type " + memoryManager);
        }
        if (arena == null) {
            throw new IllegalStateException("Could not allocate Arena");
        }

        MemoryLayout memoryLayout =
                MemoryLayout.sequenceLayout(startCapacity, layout);

        this.reader = getValueReader();
        this.setter = getValueSetter();


        this.nativeValues = arena.allocate(memoryLayout.byteSize(), memoryLayout.byteAlignment());
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
        if (o == null) {
            return indexOf(null) != -1;
        }
        if (!(this.clazz.isAssignableFrom(o.getClass()))) {
            throw new IllegalArgumentException("Parameter of contains(Object) is not of type " + this.clazz);
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
        assertSupportedOperation();
        return new SimpleIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if (size == 0) return;
        assertSupportedOperation();

        for (int i = 0; i < size; i++) {
            action.accept(get(i));
        }

    }

    private void assertSupportedOperation() {
        if (TypeConstant.getBy(clazz).type != clazz)
            throw new UnsupportedDynArrayTypeException(clazz);
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
        //TODO check size overflow
        size++;
        checkSizeAndRealloc();
        set(size - 1, element);
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
        Objects.requireNonNull(c);
        boolean modified = false;
        for (Object o : c) {
            while (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        if (size == 0) return false;

        int i = 0;
        while (i < size) {
            final T value = get(i);

            if (filter.test(value)) {
                shiftLeftValuesAtIndex(i);

                final int lastIndex = size - 1;
                set(lastIndex, zero);

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
        throw new UnsupportedOperationException("ArenaDynArray.retainAll(Collection<?>) is not implemented yet");
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        throw new UnsupportedOperationException("ArenaDynArray.replaceAll(UnaryOperator<T>) is not implemented yet");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> c) {
        assertSupportedOperation();
        Objects.requireNonNull(c);
        if (size == 0) return;
        if (clazz == boolean.class || clazz == Boolean.class) {
            Comparator<Boolean> booleanComparator = (Comparator<Boolean>) c;
            booleanSort(booleanComparator);
        } else {
            quickSort(0, size - 1, c);
        }

    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public T get(int index) {
        checkIndexOutOfBounds(index);
        return reader.apply(index);
    }

    @Override
    public T set(int index, T element) {
        checkIndexOutOfBounds(index);
        checkSizeAndRealloc();
        T oldValue = get(index);
        setter.accept(element, index);
        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        checkIndexOutOfBoundsForAdd(index);
        size++;
        checkSizeAndRealloc();
        shiftRightValuesAtIndex(index);
        set(index, element);
    }

    @Override
    public T remove(int index) {
        checkIndexOutOfBounds(index);
        T oldValue = clazz.cast(get(index));
        shiftLeftValuesAtIndex(index);
        set(size - 1, zero);
        size--;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
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
        return new ListIterator<>() {
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
                ArenaDynArray.this.add(cursor++, t);
            }
        };
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkIndexOutOfBounds(index);

        return new ListIterator<>() {
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
        checkIndexOutOfBounds(fromIndex);
        checkIndexOutOfBounds(toIndex);
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex" + fromIndex + "is greater than toIndex" + toIndex);
        }
        ArenaDynArray<T> subList = new ArenaDynArray<>(clazz, (short) (toIndex - fromIndex));

        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(get(i));
        }

        return subList;
    }

    @Override
    public Spliterator<T> spliterator() {
        return List.super.spliterator();
    }

    @Override
    public void addFirst(T t) {
        this.add(0, t);
    }

    @Override
    public void addLast(T t) {
        this.add(size, t);
    }

    @Override
    public T getFirst() {
        return this.get(0);
    }

    @Override
    public T getLast() {
        return this.get(size - 1);
    }

    @Override
    public T removeFirst() {
        return this.remove(0);
    }

    @Override
    public T removeLast() {
        return this.remove(size - 1);
    }

    @Override
    public List<T> reversed() {
        throw new UnsupportedOperationException("ArenaDynArray.reversed(Collection<?>) is not implemented yet");
    }

    @Override
    public Stream<T> stream() {
        return List.super.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return List.super.parallelStream();
    }

    private void checkSizeAndRealloc() {
        if (size >= capacity) {
            var newCapacity = capacity == 0 ? 1 : capacity * 2;
            MemorySegment newNativeValues = arena.allocate(layout.byteSize() * newCapacity, layout.byteAlignment());
            MemorySegment.copy(nativeValues, 0, newNativeValues, 0, nativeValues.byteSize());
            nativeValues = newNativeValues;
            capacity = newCapacity;
            checkSizeAndRealloc();
        }
    }

    private void shiftLeftValuesAtIndex(int i) {
        if (size - i - 1 > 0) {
            MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
        }
    }

    private void shiftRightValuesAtIndex(int i) {
        if (size - i - 1 > 0) {
            MemorySegment.copy(nativeValues, i * layout.byteSize(), nativeValues, (i + 1) * layout.byteSize(), (size - i - 1) * layout.byteSize());
        }
    }

    private void checkIndexOutOfBounds(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for size " + size);
    }

    private void checkIndexOutOfBoundsForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for size " + size);
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
        assertSupportedOperation();
        if (clazz == int.class || clazz == Integer.class) return this::getIntAtIndex;
        if (clazz == long.class || clazz == Long.class) return this::getLongAtIndex;
        if (clazz == float.class || clazz == Float.class) return this::getFloatAtIndex;
        if (clazz == double.class || clazz == Double.class) return this::getDoubleAtIndex;
        if (clazz == boolean.class || clazz == Boolean.class) return this::getBooleanAtIndex;
        if (clazz == char.class || clazz == Character.class) return this::getCharAtIndex;
        throw new UnsupportedDynArrayTypeException(clazz);
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
        assertSupportedOperation();
        if (clazz == int.class || clazz == Integer.class) return this::setIntAtIndex;
        if (clazz == long.class || clazz == Long.class) return this::setLongAtIndex;
        if (clazz == float.class || clazz == Float.class) return this::setFloatAtIndex;
        if (clazz == double.class || clazz == Double.class) return this::setDoubleAtIndex;
        if (clazz == boolean.class || clazz == Boolean.class) return this::setBooleanAtIndex;
        if (clazz == char.class || clazz == Character.class) return this::setCharAtIndex;
        throw new UnsupportedDynArrayTypeException(clazz);
    }


    public enum MemoryManagerType {
        SHARED,
        CONFINED,
        GLOBAL
    }

    private int partition(int low, int high, Comparator<? super T> c) {

        T pivot = get(high);
        int i = low - 1;

        for (int j = low; j <= high - 1; j++) {
            if (c.compare(get(j), pivot) < 0) {
                i++;
                swap(i, j);
            }
        }

        swap(i + 1, high);
        return i + 1;
    }

    private void swap(int i, int j) {
        T temp = this.get(i);
        this.set(i, this.get(j));
        this.set(j, temp);
    }

    private void quickSort(int low, int high, Comparator<? super T> c) {
        if (low < high) {

            int pi = partition(low, high, c);

            quickSort(low, pi - 1, c);
            quickSort(pi + 1, high, c);
        }
    }

    private void booleanSort(Comparator<Boolean> c) {
        final boolean leftValue = c.compare(false, true) > 0;
        final boolean rightValue = !leftValue;

        int i = 0;
        int j = size - 1;
        while (i < j) {
            while (i < j && nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i) == leftValue) {
                i++;
            }
            while (i < j && nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, j) == rightValue) {
                j--;
            }

            if (i < j) {
                nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, i, leftValue);
                nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, j, rightValue);
                i++;
                j--;
            }
        }
    }

    protected class SimpleIterator implements Iterator<T> {

        protected int index = 0;

        // indice dell'ultimo elemento restituito da next(); -1 => nessuno / remove non permesso
        protected int lastReturned = -1;

        @Override
        public final boolean hasNext() {
            return index < size;
        }

        @Override
        public final T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = index;
            return nextElement(index++);
        }

        protected T nextElement(int index) {
            return get(index);
        }

        @Override
        public void remove() {
            if (lastReturned < 0) {
                throw new IllegalStateException();
            }
            ArenaDynArray.this.remove(lastReturned);
            index = lastReturned;
            lastReturned = -1;
        }
    }

}
