package dynarrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ArenaDynArrayTest {

    void createIntArrayWithValues(ArenaDynArray<Integer> array, int... values) {
        for (int v : values) array.add(v);
    }

    @Test
    void sizeIsZeroOnNewArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertEquals(0, array.size());
    }

    @Test
    void isEmptyTrueOnNewArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertTrue(array.isEmpty());
    }

    @Test
    void isEmptyFalseAfterAdd() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertFalse(array.isEmpty());
    }

    @Test
    void addIncreasesSizeAndStoresValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(42);
        assertEquals(1, array.size());
        assertEquals(42, array.getFirst());
    }

    @Test
    void addThrowsOnUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Object.class));
    }

    @Test
    void addThrowsOnZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Integer.class, (short) 0));
    }

    @Test
    void addAllAddsAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.addAll(List.of(1, 2, 3));
        assertEquals(3, array.size());
        assertEquals(2, array.get(1));
    }

    @Test
    void addAllAtIndexInsertsElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.addAll(1, List.of(9, 8));
        assertEquals(5, array.size());
        assertEquals(9, array.get(1));
        assertEquals(8, array.get(2));
    }

    @Test
    void addAllAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, List.of(1)));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, List.of(1)));
    }

    @Test
    void containsReturnsTrueForPresentValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(5);
        assertTrue(array.contains(5));
    }

    @Test
    void containsReturnsFalseForAbsentValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(5);
        assertFalse(array.contains(6));
    }

    @Test
    void containsThrowsOnWrongType() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IllegalArgumentException.class, () -> array.contains("string"));
    }

    @Test
    void getReturnsCorrectValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(7);
        assertEquals(7, array.getFirst());
    }

    @Test
    void getThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        Integer old = array.set(0, 99);
        assertEquals(1, old);
        assertEquals(99, array.get(0));
    }

    @Test
    void setThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, 1));
    }

    @Test
    void addAtIndexInsertsValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        array.add(0, 2);
        assertEquals(2, array.get(0));
        assertEquals(1, array.get(1));
    }

    @Test
    void addAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 1));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 1);
        assertTrue(array.remove(Integer.valueOf(1)));
        assertEquals(2, array.size());
        assertEquals(2, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertFalse(array.remove(Integer.valueOf(2)));
    }

    @Test
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IllegalArgumentException.class, () -> array.remove("string"));
    }

    @Test
    void removeAtIndexRemovesCorrectValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        Integer removed = array.remove(1);
        assertEquals(2, removed);
        assertEquals(2, array.size());
        assertEquals(3, array.get(1));
    }

    @Test
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    void containsAllReturnsTrueIfAllPresent() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertTrue(array.containsAll(List.of(1, 2)));
    }

    @Test
    void containsAllReturnsFalseIfAnyAbsent() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertFalse(array.containsAll(List.of(1, 4)));
    }

    @Test
    void removeAllRemovesAllPresentValues() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 2);
        array.removeAll(List.of(2, 3));
        assertEquals(1, array.size());
        assertEquals(1, array.getFirst());
    }

    @Test
    void removeIfRemovesMatchingValues() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4);
        array.removeIf(i -> i % 2 == 0);
        assertEquals(2, array.size());
        assertEquals(1, array.get(0));
        assertEquals(3, array.get(1));
    }

    @Test
    void removeIfReturnsFalseIfNoMatch() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 3, 5);
        assertFalse(array.removeIf(i -> i % 2 == 0));
    }

    @Test
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IllegalArgumentException.class, () -> array.removeIf(null));
    }

    @Test
    void clearEmptiesArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void indexOfReturnsCorrectIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 2);
        assertEquals(1, array.indexOf(2));
    }

    @Test
    void indexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertEquals(-1, array.indexOf(4));
    }

    @Test
    void lastIndexOfReturnsCorrectIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 2);
        assertEquals(3, array.lastIndexOf(2));
    }

    @Test
    void lastIndexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertEquals(-1, array.lastIndexOf(4));
    }

    @Test
    void lastIndexOfThrowsOnWrongType() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IllegalArgumentException.class, () -> array.lastIndexOf("string"));
    }

    @Test
    void iteratorHasNextFalseOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertFalse(array.iterator().hasNext());
    }

    @Test
    void iteratorNextThrowsOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NoSuchElementException.class, () -> array.iterator().next());
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertTrue(array.iterator().hasNext());
    }

    @Test
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        List<Integer> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void addStoresMultipleIntegerValues() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        array.add(2);
        array.add(3);
        assertEquals(3, array.size());
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
        assertEquals(3, array.get(2));
    }

    @Test
    void addAllWithEmptyListDoesNotChangeArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(5);
        array.addAll(Collections.emptyList());
        assertEquals(1, array.size());
        assertEquals(5, array.get(0));
    }

    @Test
    void addAllWithSingleElementAddsElement() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(42);
        assertEquals(1, array.size());
        assertEquals(42, array.get(0));
    }

    @Test
    void removeAllRemovesAllMatchingIntegers() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        array.add(2);
        array.add(2);
        array.add(3);
        array.removeAll(List.of(2));
        assertEquals(2, array.size());
        assertEquals(1, array.get(0));
        assertEquals(3, array.get(1));
    }

    @Test
    void removeIfRemovesAllIfAllMatchPredicate() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(2);
        array.add(4);
        array.removeIf(i -> i % 2 == 0);
        assertEquals(0, array.size());
    }

    @Test
    void setUpdatesValueAtGivenIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(10);
        array.set(0, 99);
        assertEquals(99, array.get(0));
    }

    @Test
    void clearOnEmptyArrayKeepsSizeZero() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.clear();
        assertEquals(0, array.size());
    }

    @Test
    void addAtIndexZeroOnEmptyArrayAddsElement() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(0, 5);
        assertEquals(1, array.size());
        assertEquals(5, array.get(0));
    }

    @Test
    void indexOfReturnsFirstIndexForDuplicateIntegers() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(7);
        array.add(7);
        assertEquals(0, array.indexOf(7));
    }

    @Test
    void lastIndexOfReturnsLastIndexForDuplicateIntegers() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(7);
        array.add(7);
        assertEquals(1, array.lastIndexOf(7));
    }

    @Test
    void iteratorIteratesAllIntegerElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        array.add(2);
        Iterator<Integer> it = array.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertTrue(it.hasNext());
        assertEquals(2, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void toArrayReturnsObjectArrayWithValues() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        array.add(2);
        array.add(3);
        Object[] result = array.toArray();
        assertArrayEquals(new Object[]{1, 2, 3}, result);
    }

    @Test
    void toArrayTArrayReturnsFilledArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(10);
        array.add(20);
        Integer[] input = new Integer[2];
        Integer[] result = array.toArray(input);
        assertArrayEquals(new Integer[]{10, 20}, result);
    }

    @Test
    void toArrayTArrayExpandsAndFillsArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(5);
        array.add(6);
        array.add(7);
        Integer[] input = new Integer[2];
        Integer[] result = array.toArray(input);
        assertArrayEquals(new Integer[]{5, 6, 7}, result);
    }

    @Test
    void toArrayTArraySetsNullIfArrayIsLarger() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(42);
        Integer[] input = new Integer[3];
        Integer[] result = array.toArray(input);
        assertEquals(42, result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
    }

    @Test
    void toArrayIntFunctionReturnsFilledArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(100);
        array.add(200);
        Integer[] result = array.toArray(Integer[]::new);
        assertArrayEquals(new Integer[]{100, 200}, result);
    }

    @Test
    void toArrayEmptyArrayReturnsEmpty() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertArrayEquals(new Object[0], array.toArray());
        assertArrayEquals(new Integer[0], array.toArray(Integer[]::new));
        assertArrayEquals(new Integer[0], array.toArray(new Integer[0]));
    }

    @Test
    void sortSortsIntegersInAscendingOrder() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 5, 2, 9, 1, 3);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Integer[]{1, 2, 3, 5, 9}, array.toArray(new Integer[0]));
    }

    @Test
    void sortSortsIntegersInDescendingOrder() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 5, 2, 9, 1, 3);
        array.sort(Comparator.reverseOrder());
        assertArrayEquals(new Integer[]{9, 5, 3, 2, 1}, array.toArray(new Integer[0]));
    }

    @Test
    void sortOnEmptyArrayDoesNothing() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.sort(Comparator.naturalOrder());
        assertEquals(0, array.size());
    }

    @Test
    void listIteratorIteratesAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.addAll(List.of(1, 2, 3));
        ListIterator<Integer> it = array.listIterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3, it.previous());
    }

    @Test
    void subListReturnsCorrectElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.addAll(List.of(10, 20, 30, 40));
        List<Integer> sub = array.subList(1, 3);
        assertEquals(2, sub.size());
        assertEquals(20, sub.get(0));
        assertEquals(30, sub.get(1));
    }

    @Test
    void spliteratorIteratesAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.addAll(List.of(5, 6, 7));
        List<Integer> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(List.of(5, 6, 7), result);
    }

    @Test
    void streamCollectsAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.addAll(List.of(100, 200, 300));
        List<Integer> collected = array.stream().toList();
        assertEquals(List.of(100, 200, 300), collected);
    }

    @Test
    @Disabled("testConfinedArenaThreadViolation -> Renable when reviewing implementation")
    void testConfinedArenaThreadViolation() {
        ArenaDynArray<Integer> arr = new ArenaDynArray<>(Integer.class, 8, ArenaDynArray.MemoryManagerType.CONFINED);
        Thread t = new Thread(() -> {
            assertThrows(IllegalStateException.class, () -> arr.add(1));
        });
        t.start();
        try { t.join(); } catch (InterruptedException ignored) {}
    }

    @Test
    void testSharedArenaThreadAccess() {
        ArenaDynArray<Integer> arr = new ArenaDynArray<>(Integer.class, 8, ArenaDynArray.MemoryManagerType.SHARED);
        Thread t = new Thread(() -> {
            assertDoesNotThrow(() -> arr.add(2));
        });
        t.start();
        try { t.join(); } catch (InterruptedException ignored) {}
    }

    @Test
    void testGlobalArenaThreadAccess() {
        ArenaDynArray<Integer> arr = new ArenaDynArray<>(Integer.class, 8, ArenaDynArray.MemoryManagerType.GLOBAL);
        Thread t = new Thread(() -> {
            assertDoesNotThrow(() -> arr.add(3));
        });
        t.start();
        try { t.join(); } catch (InterruptedException ignored) {}
    }
}
