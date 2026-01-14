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
    private final Consumer<T> setter;
    private MemorySegment nativeValues;
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
            if (t.equals(reader.apply(i))) {
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
            action.accept(reader.apply(i));
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
            result[i] = reader.apply(i);
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
    public boolean add(T t) {
        checkSizeAndRealloc();
        if (clazz == int.class || clazz == Integer.class) {
            nativeValues.setAtIndex((ValueLayout.OfInt) layout, size, (int) t);
            size++;
            return true;
        } else if (clazz == long.class || clazz == Long.class) {
            nativeValues.setAtIndex((ValueLayout.OfLong) layout, size, (long) t);
            size++;
            return true;
        } else if (clazz == float.class || clazz == Float.class) {
            nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size, (float) t);
            size++;
            return true;
        } else if (clazz == double.class || clazz == Double.class) {
            nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size, (double) t);
            size++;
            return true;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size, (boolean) t);
            size++;
            return true;
        } else if (clazz == char.class || clazz == Character.class) {
            nativeValues.setAtIndex((ValueLayout.OfChar) layout, size, (char) t);
            size++;
            return true;
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (!(this.clazz.isAssignableFrom(o.getClass()))) {
            throw new IllegalArgumentException("Parameter of contains(Object) is not of type " + this.clazz);
        }
        T t = clazz.cast(o);
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getIntAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfInt) layout, size - 1, 0);
                    size--;
                    return true;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getLongAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfLong) layout, size - 1, 0L);
                    size--;
                    return true;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getFloatAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size - 1, 0f);
                    size--;
                    return true;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getDoubleAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size - 1, 0d);
                    size--;
                    return true;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getBooleanAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size - 1, false);
                    size--;
                    return true;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getCharAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfChar) layout, size - 1, '\0');
                    size--;
                    return true;
                }
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
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
        if (filter == null) throw new NullPointerException();
        if (size == 0) return false;
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(getIntAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfInt) layout, size - 1, 0);
                    size--;
                    i--; //TODO can avoid this saving all indexes of removal and strip the array just once after the loop
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(getLongAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfLong) layout, size - 1, 0L);
                    size--;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(getFloatAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size - 1, 0f);
                    size--;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(getDoubleAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size - 1, 0d);
                    size--;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(getBooleanAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size - 1, false);
                    size--;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(getCharAtIndex(i))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfChar) layout, size - 1, '\0');
                    size--;
                }
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
        return false;
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
        if (clazz == int.class || clazz == Integer.class) {
            return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, index));
        } else if (clazz == long.class || clazz == Long.class) {
            return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, index));
        } else if (clazz == float.class || clazz == Float.class) {
            return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, index));
        } else if (clazz == double.class || clazz == Double.class) {
            return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, index));
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, index));
        } else if (clazz == char.class || clazz == Character.class) {
            return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, index));
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
    }

    @Override
    public T set(int index, T element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        checkSizeAndRealloc();
        if (clazz == int.class || clazz == Integer.class) {
            T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, index));
            nativeValues.setAtIndex((ValueLayout.OfInt) layout, index, (int) element);
            return oldValue;
        } else if (clazz == long.class || clazz == Long.class) {
            T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, index));
            nativeValues.setAtIndex((ValueLayout.OfLong) layout, index, (long) element);
            return oldValue;
        } else if (clazz == float.class || clazz == Float.class) {
            T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, index));
            nativeValues.setAtIndex((ValueLayout.OfFloat) layout, index, (float) element);
            return oldValue;
        } else if (clazz == double.class || clazz == Double.class) {
            T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, index));
            nativeValues.setAtIndex((ValueLayout.OfDouble) layout, index, (double) element);
            return oldValue;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, index));
            nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, index, (boolean) element);
            return oldValue;
        } else if (clazz == char.class || clazz == Character.class) {
            T oldValue = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, index));
            nativeValues.setAtIndex((ValueLayout.OfChar) layout, index, (char) element);
            return oldValue;
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        checkSizeAndRealloc();
        if (clazz == int.class || clazz == Integer.class) {
            MemorySegment.copy(nativeValues, index * layout.byteSize(), nativeValues, (index + 1) * layout.byteSize(), (size - index) * layout.byteSize());
            nativeValues.setAtIndex((ValueLayout.OfInt) layout, index, (int) element);
            size++;
        } else if (clazz == long.class || clazz == Long.class) {
            MemorySegment.copy(nativeValues, index * layout.byteSize(), nativeValues, (index + 1) * layout.byteSize(), (size - index) * layout.byteSize());
            nativeValues.setAtIndex((ValueLayout.OfLong) layout, index, (long) element);
            size++;
        } else if (clazz == float.class || clazz == Float.class) {
            MemorySegment.copy(nativeValues, index * layout.byteSize(), nativeValues, (index + 1) * layout.byteSize(), (size - index) * layout.byteSize());
            nativeValues.setAtIndex((ValueLayout.OfFloat) layout, index, (float) element);
            size++;
        } else if (clazz == double.class || clazz == Double.class) {
            MemorySegment.copy(nativeValues, index * layout.byteSize(), nativeValues, (index + 1) * layout.byteSize(), (size - index) * layout.byteSize());
            nativeValues.setAtIndex((ValueLayout.OfDouble) layout, index, (double) element);
            size++;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            MemorySegment.copy(nativeValues, index * layout.byteSize(), nativeValues, (index + 1) * layout.byteSize(), (size - index) * layout.byteSize());
            nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, index, (boolean) element);
            size++;
        } else if (clazz == char.class || clazz == Character.class) {
            MemorySegment.copy(nativeValues, index * layout.byteSize(), nativeValues, (index + 1) * layout.byteSize(), (size - index) * layout.byteSize());
            nativeValues.setAtIndex((ValueLayout.OfChar) layout, index, (char) element);
            size++;
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
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
        if (!(this.clazz.isAssignableFrom(o.getClass()))) {
            throw new IllegalArgumentException("Parameter of indexOf(Object) is not of type " + this.clazz);
        }
        T t = clazz.cast(o);
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getIntAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getLongAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getFloatAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getDoubleAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getBooleanAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(getCharAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(this.clazz.isAssignableFrom(o.getClass()))) {
            throw new IllegalArgumentException("Parameter of lastIndexOf(Object) is not of type " + this.clazz);
        }
        T t = clazz.cast(o);
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(getIntAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(getLongAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(getFloatAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(getDoubleAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(getBooleanAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(getCharAtIndex(i))) {
                    return i;
                }
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
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

    private void setIntAtIndex(T n) {
        nativeValues.setAtIndex((ValueLayout.OfInt) layout, size, (int) n);
    }

    private void setLongAtIndex(T l) {
        nativeValues.setAtIndex((ValueLayout.OfLong) layout, size, (long) l);
    }

    private void setFloatAtIndex(T f) {
        nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size, (float) f);
    }

    private void setDoubleAtIndex(T d) {
        nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size, (double) d);
    }

    private void setBooleanAtIndex(T b) {
        nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size, (boolean) b);
    }

    private void setCharAtIndex(T c) {
        nativeValues.setAtIndex((ValueLayout.OfChar) layout, size, (char) c);
    }

    private Consumer<T> getValueSetter() {
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
            return reader.apply(index);
        }
    }

}
