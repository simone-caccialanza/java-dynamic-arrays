package dynarrays;

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
        assertThrows(ClassCastException.class, () -> array.contains("string"));
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
        assertTrue(array.remove(1.0f));
        assertEquals(2, array.size());
        assertEquals(2.0f, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertFalse(array.remove(2.0f));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(ClassCastException.class, () -> array.remove("string"));
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
        assertThrows(NoSuchElementException.class, () -> array.remove(0));
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

    @Test
    void listIteratorIteratesAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.addAll(List.of(1.0f, 2.0f, 3.0f));
        ListIterator<Float> it = array.listIterator();
        assertTrue(it.hasNext());
        assertEquals(1.0f, it.next());
        assertEquals(2.0f, it.next());
        assertEquals(3.0f, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3.0f, it.previous());
    }

    @Test
    void subListReturnsCorrectElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.addAll(List.of(10.0f, 20.0f, 30.0f, 40.0f));
        List<Float> sub = array.subList(1, 3);
        assertEquals(2, sub.size());
        assertEquals(20.0f, sub.get(0));
        assertEquals(30.0f, sub.get(1));
    }

    @Test
    void spliteratorIteratesAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.addAll(List.of(5.0f, 6.0f, 7.0f));
        List<Float> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(List.of(5.0f, 6.0f, 7.0f), result);
    }

    @Test
    void streamCollectsAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.addAll(List.of(100.0f, 200.0f, 300.0f));
        List<Float> collected = array.stream().toList();
        assertEquals(List.of(100.0f, 200.0f, 300.0f), collected);
    }

    @Test
    void addThrowsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Float.class, -1));
    }

    @Test
    void addTriggersReallocationWhenCapacityReached() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class, 2);
        array.add(1.0f);
        array.add(2.0f);
        array.add(3.0f); // should trigger reallocation
        assertEquals(3, array.size());
        assertEquals(1.0f, array.get(0));
        assertEquals(2.0f, array.get(1));
        assertEquals(3.0f, array.get(2));
    }

    @Test
    void addAllAtIndexZeroOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.addAll(0, List.of(1.0f, 2.0f, 3.0f));
        assertEquals(3, array.size());
        assertEquals(1.0f, array.getFirst());
    }

    @Test
    void addAllAtEndOfArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f);
        array.addAll(2, List.of(3.0f, 4.0f));
        assertEquals(4, array.size());
        assertEquals(4.0f, array.get(3));
    }

    @Test
    void containsAllOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertFalse(array.containsAll(List.of(1.0f, 2.0f, 3.0f)));
    }

    @Test
    void removeAllRemovesAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.removeAll(List.of(1.0f, 2.0f, 3.0f));
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void removeIfOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertFalse(array.removeIf(_ -> true));
    }

    @Test
    void indexOfWithNullOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertEquals(-1, array.indexOf(null));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void indexOfThrowsOnWrongType() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(ClassCastException.class, () -> array.indexOf("string"));
    }

    @Test
    void lastIndexOfWithNullOnEmptyArray() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertEquals(-1, array.lastIndexOf(null));
    }

    @Test
    void forEachThrowsOnNullAction() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertThrows(NullPointerException.class, () -> array.forEach(null));
    }

    @Test
    void sortThrowsOnNullComparator() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertThrows(NullPointerException.class, () -> array.sort(null));
    }

    @Test
    void toArrayWithNullArgument() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertThrows(IllegalArgumentException.class, () -> array.toArray((Float[]) null));
    }

    @Test
    @SuppressWarnings("SuspiciousToArrayCall")
    void toArrayWithWrongTypeThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertThrows(IllegalArgumentException.class, () -> array.toArray(new String[1]));
    }

    @Test
    void getFirstOnEmptyArrayThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void getLastOnEmptyArrayThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, array::getLast);
    }

    @Test
    void removeFirstOnEmptyArrayThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NoSuchElementException.class, array::removeFirst);
    }

    @Test
    void removeLastOnEmptyArrayThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NoSuchElementException.class, array::removeLast);
    }

    @Test
    void listIteratorWithIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f);
        ListIterator<Float> it = array.listIterator(2);
        assertEquals(3.0f, it.next());
        assertEquals(4.0f, it.next());
    }

    @Test
    void listIteratorWithIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(1));
    }

    @Test
    void listIteratorNextIndexReturnsCorrectValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());
    }

    @Test
    void listIteratorPreviousIndexReturnsCorrectValue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        assertEquals(-1, it.previousIndex());
        it.next();
        assertEquals(0, it.previousIndex());
    }

    @Test
    void listIteratorPreviousThrowsWhenAtStart() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        ListIterator<Float> it = array.listIterator();
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIteratorNextThrowsWhenAtEnd() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        ListIterator<Float> it = array.listIterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIteratorRemoveRemovesLastReturnedElement() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        it.next();
        it.remove();
        assertEquals(2, array.size());
        assertEquals(2.0f, array.getFirst());
    }

    @Test
    void listIteratorSetUpdatesLastReturnedElement() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        it.next();
        it.set(99.0f);
        assertEquals(99.0f, array.getFirst());
    }

    @Test
    void listIteratorSetThrowsWhenNotAdvanced() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        ListIterator<Float> it = array.listIterator();
        assertThrows(IllegalStateException.class, () -> it.set(99.0f));
    }

    @Test
    void listIteratorAddInsertsElement() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        it.next();
        it.add(2.0f);
        assertEquals(3, array.size());
        assertEquals(2.0f, array.get(1));
    }

    @Test
    void subListThrowsOnNegativeFromIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(-1, 2));
    }

    @Test
    void subListThrowsWhenToIndexExceedsSize() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(0, 4));
    }

    @Test
    void subListThrowsWhenFromIndexGreaterThanToIndex() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(2, 1));
    }

    @Test
    void subListReturnsEmptyListWhenFromEqualsTo() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        List<Float> sub = array.subList(1, 1);
        assertEquals(0, sub.size());
    }

    @Test
    void reversedReturnsReversedList() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        List<Float> reversed = array.reversed();
        assertEquals(5, reversed.size());
        assertEquals(5.0f, reversed.get(0));
        assertEquals(4.0f, reversed.get(1));
        assertEquals(3.0f, reversed.get(2));
        assertEquals(2.0f, reversed.get(3));
        assertEquals(1.0f, reversed.get(4));
    }

    @Test
    void retainAllKeepsOnlySpecifiedElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        boolean modified = array.retainAll(List.of(2.0f, 4.0f));
        assertTrue(modified);
        assertEquals(2, array.size());
        assertEquals(2.0f, array.get(0));
        assertEquals(4.0f, array.get(1));
    }

    @Test
    void replaceAllReplacesAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.replaceAll(x -> x * 2);
        assertEquals(3, array.size());
        assertEquals(2.0f, array.get(0));
        assertEquals(4.0f, array.get(1));
        assertEquals(6.0f, array.get(2));
    }

    @Test
    void parallelStreamCollectsAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        List<Float> collected = array.parallelStream().toList();
        assertEquals(5, collected.size());
        assertTrue(collected.containsAll(List.of(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)));
    }

    @Test
    void addManyElementsTriggersMultipleReallocations() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class, 2);
        for (int i = 0; i < 100; i++) {
            array.add((float) i);
        }
        assertEquals(100, array.size());
        assertEquals(0.0f, array.getFirst());
        assertEquals(99.0f, array.get(99));
    }

    @Test
    void clearAndReaddElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.clear();
        createArrayWithValues(array, 4.0f, 5.0f, 6.0f);
        assertEquals(3, array.size());
        assertEquals(4.0f, array.getFirst());
    }

    @Test
    void multipleRemoveOperationsPreserveOrder() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        array.remove(1);
        array.remove(2);
        assertEquals(3, array.size());
        assertEquals(1.0f, array.get(0));
        assertEquals(3.0f, array.get(1));
        assertEquals(5.0f, array.get(2));
    }

    @Test
    void iteratorMultipleCallsToNext() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        Iterator<Float> it = array.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(3, count);
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void addAllNullCollectionThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NullPointerException.class, () -> array.addAll(null));
    }

    @Test
    void addAllAtIndexNullCollectionThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NullPointerException.class, () -> array.addAll(0, null));
    }

    @Test
    void addNullElementThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NullPointerException.class, () -> array.add(null));
    }

    @Test
    void setNullElementThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.0f);
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    void containsNullReturnsFalse() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertEquals(-1, array.indexOf(null));
        assertFalse(array.contains(null));
    }

    @Test
    void removeAllNullCollectionThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NullPointerException.class, () -> array.removeAll(null));
    }

    @Test
    void containsAllNullCollectionThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertThrows(NullPointerException.class, () -> array.containsAll(null));
    }

    @Test
    void addAtMaxCapacityBoundary() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class, 1);
        array.add(1.0f);
        array.add(2.0f); // triggers reallocation
        array.add(3.0f);
        assertEquals(3, array.size());
        assertEquals(1.0f, array.get(0));
        assertEquals(2.0f, array.get(1));
        assertEquals(3.0f, array.get(2));
    }

    @Test
    void addAtIndexAtSizeBoundary() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.add(3, 4.0f); // valid: adds at the end
        assertEquals(4, array.size());
        assertEquals(4.0f, array.get(3));
    }

    @Test
    void removeAtIndexMaxBoundary() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        array.remove(4); // last valid index
        assertEquals(4, array.size());
        assertEquals(4.0f, array.get(3));
    }

    @Test
    void getAtMaxIndexBoundary() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertEquals(3.0f, array.get(2)); // last valid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    void setAtMaxIndexBoundary() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.set(2, 99.0f); // last valid index
        assertEquals(99.0f, array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(3, 100.0f));
    }

    @Test
    void iteratorRemoveWithoutNextThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        Iterator<Float> it = array.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void iteratorDoubleRemoveThrows() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        Iterator<Float> it = array.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void listIteratorRemoveAfterPreviousWorks() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        it.next();
        it.next();
        it.previous();
        it.remove();
        assertEquals(2, array.size());
    }

    @Test
    void listIteratorAddAfterRemoveWorks() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        ListIterator<Float> it = array.listIterator();
        it.next();
        it.remove();
        it.add(99.0f);
        assertEquals(3, array.size());
    }

    @Test
    void addAtIndexZeroShiftsAllElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        array.add(0, 0.0f);
        assertEquals(4, array.size());
        assertEquals(0.0f, array.get(0));
        assertEquals(1.0f, array.get(1));
        assertEquals(2.0f, array.get(2));
        assertEquals(3.0f, array.get(3));
    }

    @Test
    void removeMiddleElementShiftsCorrectly() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        array.remove(2);
        assertEquals(4, array.size());
        assertEquals(1.0f, array.get(0));
        assertEquals(2.0f, array.get(1));
        assertEquals(4.0f, array.get(2));
        assertEquals(5.0f, array.get(3));
    }

    @Test
    void multipleAddAllsWorkCorrectly() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class, 2);
        array.addAll(List.of(1.0f, 2.0f));
        array.addAll(List.of(3.0f, 4.0f));
        array.addAll(List.of(5.0f, 6.0f));
        assertEquals(6, array.size());
        assertEquals(1.0f, array.get(0));
        assertEquals(6.0f, array.get(5));
    }

    @Test
    void streamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertEquals(0, (long) array.size());
    }

    @Test
    void parallelStreamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertEquals(0, array.parallelStream().count());
    }

    @Test
    void spliteratorOnEmptyArrayHasNoElements() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        List<Float> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equalsOnSelfReturnsTrue() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        assertEquals(array, array);
    }

    @Test
    void equalsOnDifferentTypeReturnsFalse() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        assertNotEquals('c', array);
    }

    @Test
    void hashCodeConsistency() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        createArrayWithValues(array, 1.0f, 2.0f, 3.0f);
        int hash1 = array.hashCode();
        int hash2 = array.hashCode();
        assertEquals(hash1, hash2);
    }
}
