package dynarrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ArenaDynArrayFloatTest {

    void createArrayWithValues(ArenaDynArray<Float> array, float... values) {
        for (float v : values) array.add(v);
    }

    @Test
    void sizeIsZeroOnNewArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertEquals(0, array.size());
    }

    @Test
    void isEmptyTrueOnNewArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertTrue(array.isEmpty());
    }

    @Test
    void isEmptyFalseAfterAdd() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertFalse(array.isEmpty());
    }

    @Test
    void addIncreasesSizeAndStoresValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(42.5f);
        assertEquals(1, array.size());
        assertEquals(42.5f, array.getFirst());
    }

    @Test
    void addThrowsOnUnsupportedType() {
        assertThrows(UnsupportedDynArrayTypeException.class, () -> new ArenaDynArray<>(Object.class));
    }

    @Test
    void addAllAddsAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.addAll(List.of(1.0f, 2.0f, 3.0f));
        assertEquals(3, array.size());
        assertEquals(2.0f, array.get(1));
    }

    @Test
    void addAllAtIndexInsertsElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.addAll(1, List.of(9.0f, 8.0f));
        assertEquals(5, array.size());
        assertEquals(9.0f, array.get(1));
        assertEquals(8.0f, array.get(2));
    }

    @Test
    void addAllAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        var list = List.of(1.0f, 2.0f, 3.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, list));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, list));
    }

    @Test
    void containsReturnsTrueForPresentValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(5.5f);
        assertTrue(array.contains(5.5f));
    }

    @Test
    void containsReturnsFalseForAbsentValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(5.5f);
        assertFalse(array.contains(6.6f));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void containsThrowsOnWrongType() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IllegalArgumentException.class, () -> array.contains("string"));
    }

    @Test
    void getReturnsCorrectValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(7.7f);
        assertEquals(7.7f, array.getFirst());
    }

    @Test
    void getThrowsOnInvalidIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        Float old = array.set(0, 99.9f);
        assertEquals(1.0f, old);
        assertEquals(99.9f, array.getFirst());
    }

    @Test
    void setThrowsOnInvalidIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, 1.0f));
    }

    @Test
    void addAtIndexInsertsValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        array.add(0, 2.0f);
        assertEquals(2.0f, array.get(0));
        assertEquals(1.0f, array.get(1));
    }

    @Test
    void addAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 1.0f));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 1.0f));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 1.0f);
        assertTrue(array.remove(Float.valueOf(1.0f)));
        assertEquals(2, array.size());
        assertEquals(2.0f, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertFalse(array.remove(Float.valueOf(2.0f)));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IllegalArgumentException.class, () -> array.remove("string"));
    }

    @Test
    void removeAtIndexRemovesCorrectValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        Float removed = array.remove(1);
        assertEquals(2.0f, removed);
        assertEquals(2, array.size());
        assertEquals(3.0f, array.get(1));
    }

    @Test
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    void containsAllReturnsTrueIfAllPresent() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertTrue(array.containsAll(List.of(1.0f, 2.0f)));
    }

    @Test
    void containsAllReturnsFalseIfAnyAbsent() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertFalse(array.containsAll(List.of(1.0f, 4.0f)));
    }

    @Test
    void removeAllRemovesAllPresentValues() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 2.0f);
        array.removeAll(List.of(2.0f, 3.0f));
        assertEquals(1, array.size());
        assertEquals(1.0f, array.getFirst());
    }

    @Test
    void removeIfRemovesMatchingValues() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f);
        array.removeIf(i -> i > 2.5f);
        assertEquals(2, array.size());
        assertEquals(1.0f, array.get(0));
        assertEquals(2.0f, array.get(1));
    }

    @Test
    void removeIfReturnsFalseIfNoMatch() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 3.0f, 5.0f);
        assertFalse(array.removeIf(i -> i > 10.0f));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NullPointerException.class, () -> array.removeIf(null));
    }

    @Test
    @SuppressWarnings("ConstantValue")
    void clearEmptiesArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void indexOfReturnsCorrectIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 2.0f);
        assertEquals(1, array.indexOf(2.0f));
    }

    @Test
    void indexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertEquals(-1, array.indexOf(4.0f));
    }

    @Test
    void lastIndexOfReturnsCorrectIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 2.0f);
        assertEquals(3, array.lastIndexOf(2.0f));
    }

    @Test
    void lastIndexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertEquals(-1, array.lastIndexOf(4.0f));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void lastIndexOfThrowsOnWrongType() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IllegalArgumentException.class, () -> array.lastIndexOf("string"));
    }

    @Test
    void iteratorHasNextFalseOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertFalse(array.iterator().hasNext());
    }

    @Test
    void iteratorNextThrowsOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        var iterator = array.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertTrue(array.iterator().hasNext());
    }

    @Test
    @SuppressWarnings("UseBulkOperation")
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        List<Float> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of(1.0f, 2.0f, 3.0f), result);
    }

    @Test
    void toArrayReturnsObjectArrayWithValues() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        array.add(2.0f);
        array.add(3.0f);
        Object[] result = array.toArray();
        assertArrayEquals(new Object[]{1.0f, 2.0f, 3.0f}, result);
    }

    @Test
    void toArrayTArrayReturnsFilledArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(10.0f);
        array.add(20.0f);
        Float[] input = new Float[2];
        Float[] result = array.toArray(input);
        assertArrayEquals(new Float[]{10.0f, 20.0f}, result);
    }

    @Test
    void toArrayTArrayExpandsAndFillsArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(5.0f);
        array.add(6.0f);
        array.add(7.0f);
        Float[] input = new Float[2];
        Float[] result = array.toArray(input);
        assertArrayEquals(new Float[]{5.0f, 6.0f, 7.0f}, result);
    }

    @Test
    void toArrayTArraySetsNullIfArrayIsLarger() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(42.0f);
        Float[] input = new Float[3];
        Float[] result = array.toArray(input);
        assertEquals(42.0f, result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
    }

    @Test
    void toArrayIntFunctionReturnsFilledArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(100.0f);
        array.add(200.0f);
        Float[] result = array.toArray(Float[]::new);
        assertArrayEquals(new Float[]{100.0f, 200.0f}, result);
    }

    @Test
    void toArrayEmptyArrayReturnsEmpty() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertArrayEquals(new Object[0], array.toArray());
        assertArrayEquals(new Float[0], array.toArray(Float[]::new));
        assertArrayEquals(new Float[0], array.toArray(new Float[0]));
    }

    @Test
    void sortSortsInAscendingOrder() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 5.5f, 2.2f, 9.9f, 1.1f, 3.3f);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Float[]{1.1f, 2.2f, 3.3f, 5.5f, 9.9f}, array.toArray(new Float[0]));
    }

    @Test
    void sortSortsInDescendingOrder() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 5.5f, 2.2f, 9.9f, 1.1f, 3.3f);
        array.sort(Comparator.reverseOrder());
        assertArrayEquals(new Float[]{9.9f, 5.5f, 3.3f, 2.2f, 1.1f}, array.toArray(new Float[0]));
    }

    @Test
    void sortOnEmptyArrayDoesNothing() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.sort(Comparator.naturalOrder());
        assertEquals(0, array.size());
    }

    @Test
    void constructorWithZeroCapacityWorks() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class, 0);
        assertEquals(0, array.size());
        array.add(1.0f);
        assertEquals(1, array.size());
    }
}
