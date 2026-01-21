package dynarrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ArenaDynArrayDoubleTest {

    void createArrayWithValues(ArenaDynArray<Double> array, double... values) {
        for (double v : values) array.add(v);
    }

    @Test
    void sizeIsZeroOnNewArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertEquals(0, array.size());
    }

    @Test
    void isEmptyTrueOnNewArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertTrue(array.isEmpty());
    }

    @Test
    void isEmptyFalseAfterAdd() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertFalse(array.isEmpty());
    }

    @Test
    void addIncreasesSizeAndStoresValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(42.5);
        assertEquals(1, array.size());
        assertEquals(42.5, array.getFirst());
    }

    @Test
    void addThrowsOnUnsupportedType() {
        assertThrows(UnsupportedDynArrayTypeException.class, () -> new ArenaDynArray<>(Object.class));
    }

    @Test
    void addAllAddsAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.addAll(List.of(1.0, 2.0, 3.0));
        assertEquals(3, array.size());
        assertEquals(2.0, array.get(1));
    }

    @Test
    void addAllAtIndexInsertsElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.addAll(1, List.of(9.0, 8.0));
        assertEquals(5, array.size());
        assertEquals(9.0, array.get(1));
        assertEquals(8.0, array.get(2));
    }

    @Test
    void addAllAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        var list = List.of(1.0, 2.0, 3.0);
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, list));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, list));
    }

    @Test
    void containsReturnsTrueForPresentValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(5.5);
        assertTrue(array.contains(5.5));
    }

    @Test
    void containsReturnsFalseForAbsentValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(5.5);
        assertFalse(array.contains(6.6));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void containsThrowsOnWrongType() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IllegalArgumentException.class, () -> array.contains("string"));
    }

    @Test
    void getReturnsCorrectValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(7.7);
        assertEquals(7.7, array.getFirst());
    }

    @Test
    void getThrowsOnInvalidIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        Double old = array.set(0, 99.9);
        assertEquals(1.0, old);
        assertEquals(99.9, array.getFirst());
    }

    @Test
    void setThrowsOnInvalidIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, 1.0));
    }

    @Test
    void addAtIndexInsertsValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        array.add(0, 2.0);
        assertEquals(2.0, array.get(0));
        assertEquals(1.0, array.get(1));
    }

    @Test
    void addAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 1.0));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 1.0);
        assertTrue(array.remove(Double.valueOf(1.0)));
        assertEquals(2, array.size());
        assertEquals(2.0, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertFalse(array.remove(Double.valueOf(2.0)));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IllegalArgumentException.class, () -> array.remove("string"));
    }

    @Test
    void removeAtIndexRemovesCorrectValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        Double removed = array.remove(1);
        assertEquals(2.0, removed);
        assertEquals(2, array.size());
        assertEquals(3.0, array.get(1));
    }

    @Test
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    void containsAllReturnsTrueIfAllPresent() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertTrue(array.containsAll(List.of(1.0, 2.0)));
    }

    @Test
    void containsAllReturnsFalseIfAnyAbsent() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertFalse(array.containsAll(List.of(1.0, 4.0)));
    }

    @Test
    void removeAllRemovesAllPresentValues() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 2.0);
        array.removeAll(List.of(2.0, 3.0));
        assertEquals(1, array.size());
        assertEquals(1.0, array.getFirst());
    }

    @Test
    void removeIfRemovesMatchingValues() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 4.0);
        array.removeIf(i -> i > 2.5);
        assertEquals(2, array.size());
        assertEquals(1.0, array.get(0));
        assertEquals(2.0, array.get(1));
    }

    @Test
    void removeIfReturnsFalseIfNoMatch() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 3.0, 5.0);
        assertFalse(array.removeIf(i -> i > 10.0));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NullPointerException.class, () -> array.removeIf(null));
    }

    @Test
    @SuppressWarnings("ConstantValue")
    void clearEmptiesArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void indexOfReturnsCorrectIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 2.0);
        assertEquals(1, array.indexOf(2.0));
    }

    @Test
    void indexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertEquals(-1, array.indexOf(4.0));
    }

    @Test
    void lastIndexOfReturnsCorrectIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 2.0);
        assertEquals(3, array.lastIndexOf(2.0));
    }

    @Test
    void lastIndexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertEquals(-1, array.lastIndexOf(4.0));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void lastIndexOfThrowsOnWrongType() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IllegalArgumentException.class, () -> array.lastIndexOf("string"));
    }

    @Test
    void iteratorHasNextFalseOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertFalse(array.iterator().hasNext());
    }

    @Test
    void iteratorNextThrowsOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        var iterator = array.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertTrue(array.iterator().hasNext());
    }

    @Test
    @SuppressWarnings("UseBulkOperation")
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        List<Double> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of(1.0, 2.0, 3.0), result);
    }

    @Test
    void toArrayReturnsObjectArrayWithValues() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        array.add(2.0);
        array.add(3.0);
        Object[] result = array.toArray();
        assertArrayEquals(new Object[]{1.0, 2.0, 3.0}, result);
    }

    @Test
    void toArrayTArrayReturnsFilledArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(10.0);
        array.add(20.0);
        Double[] input = new Double[2];
        Double[] result = array.toArray(input);
        assertArrayEquals(new Double[]{10.0, 20.0}, result);
    }

    @Test
    void toArrayTArrayExpandsAndFillsArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(5.0);
        array.add(6.0);
        array.add(7.0);
        Double[] input = new Double[2];
        Double[] result = array.toArray(input);
        assertArrayEquals(new Double[]{5.0, 6.0, 7.0}, result);
    }

    @Test
    void toArrayTArraySetsNullIfArrayIsLarger() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(42.0);
        Double[] input = new Double[3];
        Double[] result = array.toArray(input);
        assertEquals(42.0, result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
    }

    @Test
    void toArrayIntFunctionReturnsFilledArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(100.0);
        array.add(200.0);
        Double[] result = array.toArray(Double[]::new);
        assertArrayEquals(new Double[]{100.0, 200.0}, result);
    }

    @Test
    void toArrayEmptyArrayReturnsEmpty() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertArrayEquals(new Object[0], array.toArray());
        assertArrayEquals(new Double[0], array.toArray(Double[]::new));
        assertArrayEquals(new Double[0], array.toArray(new Double[0]));
    }

    @Test
    void sortSortsInAscendingOrder() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 5.5, 2.2, 9.9, 1.1, 3.3);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Double[]{1.1, 2.2, 3.3, 5.5, 9.9}, array.toArray(new Double[0]));
    }

    @Test
    void sortSortsInDescendingOrder() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 5.5, 2.2, 9.9, 1.1, 3.3);
        array.sort(Comparator.reverseOrder());
        assertArrayEquals(new Double[]{9.9, 5.5, 3.3, 2.2, 1.1}, array.toArray(new Double[0]));
    }

    @Test
    void sortOnEmptyArrayDoesNothing() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.sort(Comparator.naturalOrder());
        assertEquals(0, array.size());
    }

    @Test
    void constructorWithZeroCapacityWorks() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class, 0);
        assertEquals(0, array.size());
        array.add(1.0);
        assertEquals(1, array.size());
    }
}
