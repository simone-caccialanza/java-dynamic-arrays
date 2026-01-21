package dynarrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ArenaDynArrayBooleanTest {

    void createArrayWithValues(ArenaDynArray<Boolean> array, boolean... values) {
        for (boolean v : values) array.add(v);
    }

    @Test
    void sizeIsZeroOnNewArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertEquals(0, array.size());
    }

    @Test
    void isEmptyTrueOnNewArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertTrue(array.isEmpty());
    }

    @Test
    void isEmptyFalseAfterAdd() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertFalse(array.isEmpty());
    }

    @Test
    void addIncreasesSizeAndStoresValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertEquals(1, array.size());
        assertEquals(true, array.getFirst());
    }

    @Test
    void addThrowsOnUnsupportedType() {
        assertThrows(UnsupportedDynArrayTypeException.class, () -> new ArenaDynArray<>(Object.class));
    }

    @Test
    void addAllAddsAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.addAll(List.of(true, false, true));
        assertEquals(3, array.size());
        assertEquals(false, array.get(1));
    }

    @Test
    void addAllAtIndexInsertsElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.addAll(1, List.of(false, false));
        assertEquals(5, array.size());
        assertEquals(false, array.get(1));
        assertEquals(false, array.get(2));
    }

    @Test
    void addAllAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        var list = List.of(true, false, true);
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, list));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, list));
    }

    @Test
    void containsReturnsTrueForPresentValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertTrue(array.contains(true));
    }

    @Test
    void containsReturnsFalseForAbsentValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertFalse(array.contains(false));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void containsThrowsOnWrongType() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IllegalArgumentException.class, () -> array.contains("string"));
    }

    @Test
    void getReturnsCorrectValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(false);
        assertEquals(false, array.getFirst());
    }

    @Test
    void getThrowsOnInvalidIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        Boolean old = array.set(0, false);
        assertEquals(true, old);
        assertEquals(false, array.getFirst());
    }

    @Test
    void setThrowsOnInvalidIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, true));
    }

    @Test
    void addAtIndexInsertsValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        array.add(0, false);
        assertEquals(false, array.get(0));
        assertEquals(true, array.get(1));
    }

    @Test
    void addAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, true));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, true));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertTrue(array.remove(Boolean.valueOf(true)));
        assertEquals(2, array.size());
        assertEquals(false, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertFalse(array.remove(Boolean.valueOf(false)));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IllegalArgumentException.class, () -> array.remove("string"));
    }

    @Test
    void removeAtIndexRemovesCorrectValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        Boolean removed = array.remove(1);
        assertEquals(false, removed);
        assertEquals(2, array.size());
        assertEquals(true, array.get(1));
    }

    @Test
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    void containsAllReturnsTrueIfAllPresent() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertTrue(array.containsAll(List.of(true, false)));
    }

    @Test
    void containsAllReturnsFalseIfAnyAbsent() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, true, true);
        assertFalse(array.containsAll(List.of(true, false)));
    }

    @Test
    void removeAllRemovesAllPresentValues() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false);
        array.removeAll(List.of(false));
        assertEquals(2, array.size());
        assertEquals(true, array.getFirst());
    }

    @Test
    void removeIfRemovesMatchingValues() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false);
        array.removeIf(b -> b);
        assertEquals(2, array.size());
        assertEquals(false, array.get(0));
        assertEquals(false, array.get(1));
    }

    @Test
    void removeIfReturnsFalseIfNoMatch() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, true, true);
        assertFalse(array.removeIf(b -> !b));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NullPointerException.class, () -> array.removeIf(null));
    }

    @Test
    @SuppressWarnings("ConstantValue")
    void clearEmptiesArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void indexOfReturnsCorrectIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false);
        assertEquals(1, array.indexOf(false));
    }

    @Test
    void indexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, true, true);
        assertEquals(-1, array.indexOf(false));
    }

    @Test
    void lastIndexOfReturnsCorrectIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false);
        assertEquals(3, array.lastIndexOf(false));
    }

    @Test
    void lastIndexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, true, true);
        assertEquals(-1, array.lastIndexOf(false));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void lastIndexOfThrowsOnWrongType() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IllegalArgumentException.class, () -> array.lastIndexOf("string"));
    }

    @Test
    void iteratorHasNextFalseOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertFalse(array.iterator().hasNext());
    }

    @Test
    void iteratorNextThrowsOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        var iterator = array.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertTrue(array.iterator().hasNext());
    }

    @Test
    @SuppressWarnings("UseBulkOperation")
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        List<Boolean> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of(true, false, true), result);
    }

    @Test
    void toArrayReturnsObjectArrayWithValues() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        array.add(false);
        array.add(true);
        Object[] result = array.toArray();
        assertArrayEquals(new Object[]{true, false, true}, result);
    }

    @Test
    void toArrayTArrayReturnsFilledArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        array.add(false);
        Boolean[] input = new Boolean[2];
        Boolean[] result = array.toArray(input);
        assertArrayEquals(new Boolean[]{true, false}, result);
    }

    @Test
    void toArrayTArrayExpandsAndFillsArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        array.add(false);
        array.add(true);
        Boolean[] input = new Boolean[2];
        Boolean[] result = array.toArray(input);
        assertArrayEquals(new Boolean[]{true, false, true}, result);
    }

    @Test
    void toArrayTArraySetsNullIfArrayIsLarger() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        Boolean[] input = new Boolean[3];
        Boolean[] result = array.toArray(input);
        assertEquals(true, result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
    }

    @Test
    void toArrayIntFunctionReturnsFilledArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(false);
        array.add(true);
        Boolean[] result = array.toArray(Boolean[]::new);
        assertArrayEquals(new Boolean[]{false, true}, result);
    }

    @Test
    void toArrayEmptyArrayReturnsEmpty() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertArrayEquals(new Object[0], array.toArray());
        assertArrayEquals(new Boolean[0], array.toArray(Boolean[]::new));
        assertArrayEquals(new Boolean[0], array.toArray(new Boolean[0]));
    }

    @Test
    void sortSortsBooleanValues() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false);
        array.sort(Comparator.naturalOrder());
        // false < true -> [false, false, true, true]
        assertEquals(false, array.get(0));
        assertEquals(false, array.get(1));
        assertEquals(true, array.get(2));
        assertEquals(true, array.get(3));
    }

    @Test
    void sortSortsBooleanValuesInDescending() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, false, true, false, true);
        array.sort(Comparator.reverseOrder());
        // true > false -> [true, true, false, false]
        assertEquals(true, array.get(0));
        assertEquals(true, array.get(1));
        assertEquals(false, array.get(2));
        assertEquals(false, array.get(3));
    }

    @Test
    void sortOnEmptyArrayDoesNothing() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.sort(Comparator.naturalOrder());
        assertEquals(0, array.size());
    }

    @Test
    void constructorWithZeroCapacityWorks() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class, 0);
        assertEquals(0, array.size());
        array.add(true);
        assertEquals(1, array.size());
    }
}
