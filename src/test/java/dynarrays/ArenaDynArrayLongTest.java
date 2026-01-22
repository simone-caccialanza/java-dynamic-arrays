package dynarrays;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ArenaDynArrayLongTest {

    void createArrayWithValues(ArenaDynArray<Long> array, long... values) {
        for (long v : values) array.add(v);
    }

    @Test
    void sizeIsZeroOnNewArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertEquals(0, array.size());
    }

    @Test
    void isEmptyTrueOnNewArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertTrue(array.isEmpty());
    }

    @Test
    void isEmptyFalseAfterAdd() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertFalse(array.isEmpty());
    }

    @Test
    void addIncreasesSizeAndStoresValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(42L);
        assertEquals(1, array.size());
        assertEquals(42L, array.getFirst());
    }

    @Test
    void addThrowsOnUnsupportedType() {
        assertThrows(UnsupportedDynArrayTypeException.class, () -> new ArenaDynArray<>(Object.class));
    }

    @Test
    void addAllAddsAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.addAll(List.of(1L, 2L, 3L));
        assertEquals(3, array.size());
        assertEquals(2L, array.get(1));
    }

    @Test
    void addAllAtIndexInsertsElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.addAll(1, List.of(9L, 8L));
        assertEquals(5, array.size());
        assertEquals(9L, array.get(1));
        assertEquals(8L, array.get(2));
    }

    @Test
    void addAllAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        var list = List.of(1L, 2L, 3L);
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, list));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, list));
    }

    @Test
    void containsReturnsTrueForPresentValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(5L);
        assertTrue(array.contains(5L));
    }

    @Test
    void containsReturnsFalseForAbsentValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(5L);
        assertFalse(array.contains(6L));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void containsThrowsOnWrongType() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(ClassCastException.class, () -> array.contains("string"));
    }

    @Test
    void getReturnsCorrectValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(7L);
        assertEquals(7L, array.getFirst());
    }

    @Test
    void getThrowsOnInvalidIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        Long old = array.set(0, 99L);
        assertEquals(1L, old);
        assertEquals(99L, array.getFirst());
    }

    @Test
    void setThrowsOnInvalidIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, 1L));
    }

    @Test
    void addAtIndexInsertsValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        array.add(0, 2L);
        assertEquals(2L, array.get(0));
        assertEquals(1L, array.get(1));
    }

    @Test
    void addAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 1L));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 1L));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 1L);
        assertTrue(array.remove(1L));
        assertEquals(2, array.size());
        assertEquals(2L, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertFalse(array.remove(2L));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(ClassCastException.class, () -> array.remove("string"));
    }

    @Test
    void removeAtIndexRemovesCorrectValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        Long removed = array.remove(1);
        assertEquals(2L, removed);
        assertEquals(2, array.size());
        assertEquals(3L, array.get(1));
    }

    @Test
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NoSuchElementException.class, () -> array.remove(0));
    }

    @Test
    void containsAllReturnsTrueIfAllPresent() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertTrue(array.containsAll(List.of(1L, 2L)));
    }

    @Test
    void containsAllReturnsFalseIfAnyAbsent() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertFalse(array.containsAll(List.of(1L, 4L)));
    }

    @Test
    void removeAllRemovesAllPresentValues() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 2L);
        array.removeAll(List.of(2L, 3L));
        assertEquals(1, array.size());
        assertEquals(1L, array.getFirst());
    }

    @Test
    void removeIfRemovesMatchingValues() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 4L);
        array.removeIf(i -> i % 2 == 0);
        assertEquals(2, array.size());
        assertEquals(1L, array.get(0));
        assertEquals(3L, array.get(1));
    }

    @Test
    void removeIfReturnsFalseIfNoMatch() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 3L, 5L);
        assertFalse(array.removeIf(i -> i % 2 == 0));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NullPointerException.class, () -> array.removeIf(null));
    }

    @Test
    @SuppressWarnings("ConstantValue")
    void clearEmptiesArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void indexOfReturnsCorrectIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 2L);
        assertEquals(1, array.indexOf(2L));
    }

    @Test
    void indexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertEquals(-1, array.indexOf(4L));
    }

    @Test
    void lastIndexOfReturnsCorrectIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 2L);
        assertEquals(3, array.lastIndexOf(2L));
    }

    @Test
    void lastIndexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertEquals(-1, array.lastIndexOf(4L));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void lastIndexOfThrowsOnWrongType() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IllegalArgumentException.class, () -> array.lastIndexOf("string"));
    }

    @Test
    void iteratorHasNextFalseOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertFalse(array.iterator().hasNext());
    }

    @Test
    void iteratorNextThrowsOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        var iterator = array.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertTrue(array.iterator().hasNext());
    }

    @Test
    @SuppressWarnings("UseBulkOperation")
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        List<Long> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of(1L, 2L, 3L), result);
    }

    @Test
    void toArrayReturnsObjectArrayWithValues() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        array.add(2L);
        array.add(3L);
        Object[] result = array.toArray();
        assertArrayEquals(new Object[]{1L, 2L, 3L}, result);
    }

    @Test
    void toArrayTArrayReturnsFilledArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(10L);
        array.add(20L);
        Long[] input = new Long[2];
        Long[] result = array.toArray(input);
        assertArrayEquals(new Long[]{10L, 20L}, result);
    }

    @Test
    void toArrayTArrayExpandsAndFillsArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(5L);
        array.add(6L);
        array.add(7L);
        Long[] input = new Long[2];
        Long[] result = array.toArray(input);
        assertArrayEquals(new Long[]{5L, 6L, 7L}, result);
    }

    @Test
    void toArrayTArraySetsNullIfArrayIsLarger() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(42L);
        Long[] input = new Long[3];
        Long[] result = array.toArray(input);
        assertEquals(42L, result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
    }

    @Test
    void toArrayIntFunctionReturnsFilledArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(100L);
        array.add(200L);
        Long[] result = array.toArray(Long[]::new);
        assertArrayEquals(new Long[]{100L, 200L}, result);
    }

    @Test
    void toArrayEmptyArrayReturnsEmpty() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertArrayEquals(new Object[0], array.toArray());
        assertArrayEquals(new Long[0], array.toArray(Long[]::new));
        assertArrayEquals(new Long[0], array.toArray(new Long[0]));
    }

    @Test
    void sortSortsInAscendingOrder() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 5L, 2L, 9L, 1L, 3L);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Long[]{1L, 2L, 3L, 5L, 9L}, array.toArray(new Long[0]));
    }

    @Test
    void sortSortsInDescendingOrder() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 5L, 2L, 9L, 1L, 3L);
        array.sort(Comparator.reverseOrder());
        assertArrayEquals(new Long[]{9L, 5L, 3L, 2L, 1L}, array.toArray(new Long[0]));
    }

    @Test
    void sortOnEmptyArrayDoesNothing() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.sort(Comparator.naturalOrder());
        assertEquals(0, array.size());
    }

    @Test
    void constructorWithZeroCapacityWorks() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class, 0);
        assertEquals(0, array.size());
        array.add(1L);
        assertEquals(1, array.size());
    }
}
