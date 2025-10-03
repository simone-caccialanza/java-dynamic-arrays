package dynarrays;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

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
    // - implement max length
    // - implement memory type
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
                @Override
                public boolean hasNext() {
                    try {
                        nativeValues.getAtIndex((ValueLayout.OfInt) layout, 0);
                        return true;
                    } catch (IndexOutOfBoundsException _) {
                        return false;
                    }
                }

                @Override
                public T next() {
                    try {
                        return clazz.cast(nativeValues.getAtIndex((ValueLayout.OfInt) layout, 0));
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        throw new UnsupportedOperationException("Not yet implemented");
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
            remove(o);
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException("Not yet implemented");
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Stream<T> stream() {
        throw new UnsupportedOperationException("Not yet implemented");
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
}
