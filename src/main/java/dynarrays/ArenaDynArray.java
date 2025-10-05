package dynarrays;

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
    private final int capacity;
    private final Class<T> clazz;
    private final ValueLayout layout;
    private MemorySegment nativeValues;
    private int size = 0;

    public ArenaDynArray(Class<T> clazz) {
        this(clazz, DEFAULT_START_CAPACITY);
    }

    //TODO
    // - implement memory type considering different types of memory privileges (Arena.ofSomething())
    // - implement a factory
    public ArenaDynArray(Class<T> clazz, short startCapacity) {
        if (!ALLOWED_LAYOUTS_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Only primitive and wrapper types are allowed");
        }
        this.clazz = clazz;
        this.layout = ALLOWED_LAYOUTS_MAP.get(clazz);

        if (startCapacity <= 0) {
            throw new IllegalArgumentException("Start length must be positive");
        }
        this.capacity = startCapacity;

        arena = Arena.ofConfined();
        if (arena == null) {
            throw new RuntimeException("Could not allocate Arena");
        }

        MemoryLayout memoryLayout =
                MemoryLayout.sequenceLayout(startCapacity, layout);

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
        T t = clazz.cast(o);
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)))) {
                    return true;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)))) {
                    return true;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)))) {
                    return true;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)))) {
                    return true;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)))) {
                    return true;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)))) {
                    return true;
                }
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> it = null;
        if (clazz == int.class || clazz == Integer.class) {
            it = new Iterator<>() {
                private int index = 0;
                @Override
                public boolean hasNext() {
                    try {
                        if (index >= size) throw new IndexOutOfBoundsException();
                        nativeValues.getAtIndex((ValueLayout.OfInt) layout, index);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        if (index >= size) throw new IndexOutOfBoundsException();
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, index++));
                    } catch (IndexOutOfBoundsException e) {
                        throw new NoSuchElementException(e);
                    }
                }
            };
        } else if (clazz == long.class || clazz == Long.class) {
            it = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    try {
                        nativeValues.getAtIndex((ValueLayout.OfLong) layout, 0);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, 0));
                    } catch (IndexOutOfBoundsException e) {
                        throw new NoSuchElementException(e);
                    }
                }
            };
        } else if (clazz == float.class || clazz == Float.class) {
            it = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    try {
                        nativeValues.getAtIndex((ValueLayout.OfFloat) layout, 0);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, 0));
                    } catch (IndexOutOfBoundsException e) {
                        throw new NoSuchElementException(e);
                    }
                }
            };
        } else if (clazz == double.class || clazz == Double.class) {
            it = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    try {
                        nativeValues.getAtIndex((ValueLayout.OfDouble) layout, 0);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, 0));
                    } catch (IndexOutOfBoundsException e) {
                        throw new NoSuchElementException(e);
                    }
                }
            };
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            it = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    try {
                        nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, 0);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, 0));
                    } catch (IndexOutOfBoundsException e) {
                        throw new NoSuchElementException(e);
                    }
                }
            };
        } else if (clazz == char.class || clazz == Character.class) {
            it = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    try {
                        nativeValues.getAtIndex((ValueLayout.OfChar) layout, 0);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, 0));
                    } catch (IndexOutOfBoundsException e) {
                        throw new NoSuchElementException(e);
                    }
                }
            };
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        }

        if (it == null) throw new UnsupportedOperationException("Unsupported type " + clazz);
        return it;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
        if (size == 0) return;
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                action.accept(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                action.accept(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                action.accept(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                action.accept(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                action.accept(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)));
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                action.accept(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)));
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }

    }

    @Override
    public Object[] toArray() {
        if (size == 0) return new Object[0];
        Object[] result = new Object[size];
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                result[i] = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                result[i] = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                result[i] = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                result[i] = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                result[i] = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i));
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                result[i] = clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i));
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
        return result;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a == null) throw new IllegalArgumentException("Array must not be null");
        if (!(clazz.isAssignableFrom(a.getClass().getComponentType())))
            throw new IllegalArgumentException("Array must be of type " + clazz.getName());
        if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i));
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i));
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        if (generator == null) throw new NullPointerException();
        T1[] a = generator.apply(size);
        if (!(clazz.isAssignableFrom(a.getClass().getComponentType())))
            throw new IllegalArgumentException("Array must be of type " + clazz.getName());
        if (clazz == int.class || clazz == Integer.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i));
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                a[i] = (T1) clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i));
            }
        } else if (clazz == String.class) {
            throw new UnsupportedOperationException("String is not yet supported");
        } else {
            throw new UnsupportedOperationException("Unsupported type " + clazz);
        }
        return a;
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
            return true;
        } else if (clazz == float.class || clazz == Float.class) {
            nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size, (float) t);
            return true;
        } else if (clazz == double.class || clazz == Double.class) {
            nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size, (double) t);
            return true;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size, (boolean) t);
            return true;
        } else if (clazz == char.class || clazz == Character.class) {
            nativeValues.setAtIndex((ValueLayout.OfChar) layout, size, (char) t);
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
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfInt) layout, size - 1, 0);
                    size--;
                    return true;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfLong) layout, size - 1, 0L);
                    size--;
                    return true;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size - 1, 0f);
                    size--;
                    return true;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size - 1, 0d);
                    size--;
                    return true;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size - 1, false);
                    size--;
                    return true;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)))) {
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
                if (filter.test(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfInt) layout, size - 1, 0);
                    size--;
                    i--; //TODO can avoid this saving all indexes of removal and strip the array just once after the loop
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfLong) layout, size - 1, 0L);
                    size--;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfFloat) layout, size - 1, 0f);
                    size--;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfDouble) layout, size - 1, 0d);
                    size--;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)))) {
                    MemorySegment.copy(nativeValues, (i + 1) * layout.byteSize(), nativeValues, i * layout.byteSize(), (size - i - 1) * layout.byteSize());
                    nativeValues.setAtIndex((ValueLayout.OfBoolean) layout, size - 1, false);
                    size--;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (filter.test(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)))) {
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
        } else if (clazz == boolean.class || clazz == Boolean.class){
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
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)))) {
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
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)))) {
                    return i;
                }
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = size - 1; i >= 0; i--) {
                if (t.equals(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)))) {
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
                subList.add(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, i)));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfLong) layout, i)));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfFloat) layout, i)));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfDouble) layout, i)));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfBoolean) layout, i)));
            }
        } else if (clazz == char.class || clazz == Character.class) {
            for (int i = fromIndex; i < toIndex; i++) {
                subList.add(clazz.cast(nativeValues.getAtIndex((ValueLayout.OfChar) layout, i)));
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
                public long estimateSize() { return size - index; }
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
                public long estimateSize() { return size - index; }
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
                public long estimateSize() { return size - index; }
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
                    action.accept((double) value);
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
                            action.accept((double) v);
                            return true;
                        }
                    };
                    index = mid;
                    return left;
                }
                @Override
                public long estimateSize() { return size - index; }
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
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void checkSizeAndRealloc() {
        if (size == capacity) {
            MemorySegment newNativeValues = arena.allocate(layout.byteSize(), layout.byteAlignment());
            MemorySegment.copy(nativeValues, 0, newNativeValues, 0, nativeValues.byteSize());
            nativeValues = newNativeValues;
        }
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


}
