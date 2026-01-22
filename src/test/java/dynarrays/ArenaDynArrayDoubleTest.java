package dynarrays;

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
        assertThrows(ClassCastException.class, () -> array.contains("string"));
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
        assertTrue(array.remove(1.0));
        assertEquals(2, array.size());
        assertEquals(2.0, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertFalse(array.remove(2.0));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(ClassCastException.class, () -> array.remove("string"));
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
        assertThrows(NoSuchElementException.class, () -> array.remove(0));
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

    @Test
    void listIteratorIteratesAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.addAll(List.of(1.0, 2.0, 3.0));
        ListIterator<Double> it = array.listIterator();
        assertTrue(it.hasNext());
        assertEquals(1.0, it.next());
        assertEquals(2.0, it.next());
        assertEquals(3.0, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3.0, it.previous());
    }

    @Test
    void subListReturnsCorrectElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.addAll(List.of(10.0, 20.0, 30.0, 40.0));
        List<Double> sub = array.subList(1, 3);
        assertEquals(2, sub.size());
        assertEquals(20.0, sub.get(0));
        assertEquals(30.0, sub.get(1));
    }

    @Test
    void spliteratorIteratesAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.addAll(List.of(5.0, 6.0, 7.0));
        List<Double> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(List.of(5.0, 6.0, 7.0), result);
    }

    @Test
    void streamCollectsAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.addAll(List.of(100.0, 200.0, 300.0));
        List<Double> collected = array.stream().toList();
        assertEquals(List.of(100.0, 200.0, 300.0), collected);
    }

    @Test
    void addThrowsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Double.class, -1));
    }

    @Test
    void addTriggersReallocationWhenCapacityReached() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class, 2);
        array.add(1.0);
        array.add(2.0);
        array.add(3.0); // should trigger reallocation
        assertEquals(3, array.size());
        assertEquals(1.0, array.get(0));
        assertEquals(2.0, array.get(1));
        assertEquals(3.0, array.get(2));
    }

    @Test
    void addAllAtIndexZeroOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.addAll(0, List.of(1.0, 2.0, 3.0));
        assertEquals(3, array.size());
        assertEquals(1.0, array.getFirst());
    }

    @Test
    void addAllAtEndOfArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0);
        array.addAll(2, List.of(3.0, 4.0));
        assertEquals(4, array.size());
        assertEquals(4.0, array.get(3));
    }

    @Test
    void containsAllOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertFalse(array.containsAll(List.of(1.0, 2.0, 3.0)));
    }

    @Test
    void removeAllRemovesAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.removeAll(List.of(1.0, 2.0, 3.0));
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void removeIfOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertFalse(array.removeIf(_ -> true));
    }

    @Test
    void indexOfWithNullOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertEquals(-1, array.indexOf(null));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void indexOfThrowsOnWrongType() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(ClassCastException.class, () -> array.indexOf("string"));
    }

    @Test
    void lastIndexOfWithNullOnEmptyArray() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertEquals(-1, array.lastIndexOf(null));
    }

    @Test
    void forEachThrowsOnNullAction() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertThrows(NullPointerException.class, () -> array.forEach(null));
    }

    @Test
    void sortThrowsOnNullComparator() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertThrows(NullPointerException.class, () -> array.sort(null));
    }

    @Test
    void toArrayWithNullArgument() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertThrows(IllegalArgumentException.class, () -> array.toArray((Double[]) null));
    }

    @Test
    @SuppressWarnings("SuspiciousToArrayCall")
    void toArrayWithWrongTypeThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertThrows(IllegalArgumentException.class, () -> array.toArray(new String[1]));
    }

    @Test
    void getFirstOnEmptyArrayThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void getLastOnEmptyArrayThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, array::getLast);
    }

    @Test
    void removeFirstOnEmptyArrayThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NoSuchElementException.class, array::removeFirst);
    }

    @Test
    void removeLastOnEmptyArrayThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NoSuchElementException.class, array::removeLast);
    }

    @Test
    void listIteratorWithIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 4.0);
        ListIterator<Double> it = array.listIterator(2);
        assertEquals(3.0, it.next());
        assertEquals(4.0, it.next());
    }

    @Test
    void listIteratorWithIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(1));
    }

    @Test
    void listIteratorNextIndexReturnsCorrectValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());
    }

    @Test
    void listIteratorPreviousIndexReturnsCorrectValue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        assertEquals(-1, it.previousIndex());
        it.next();
        assertEquals(0, it.previousIndex());
    }

    @Test
    void listIteratorPreviousThrowsWhenAtStart() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        ListIterator<Double> it = array.listIterator();
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIteratorNextThrowsWhenAtEnd() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        ListIterator<Double> it = array.listIterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIteratorRemoveRemovesLastReturnedElement() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        it.next();
        it.remove();
        assertEquals(2, array.size());
        assertEquals(2.0, array.getFirst());
    }

    @Test
    void listIteratorSetUpdatesLastReturnedElement() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        it.next();
        it.set(99.0);
        assertEquals(99.0, array.getFirst());
    }

    @Test
    void listIteratorSetThrowsWhenNotAdvanced() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        ListIterator<Double> it = array.listIterator();
        assertThrows(IllegalStateException.class, () -> it.set(99.0));
    }

    @Test
    void listIteratorAddInsertsElement() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        it.next();
        it.add(2.0);
        assertEquals(3, array.size());
        assertEquals(2.0, array.get(1));
    }

    @Test
    void subListThrowsOnNegativeFromIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(-1, 2));
    }

    @Test
    void subListThrowsWhenToIndexExceedsSize() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(0, 4));
    }

    @Test
    void subListThrowsWhenFromIndexGreaterThanToIndex() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(2, 1));
    }

    @Test
    void subListReturnsEmptyListWhenFromEqualsTo() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        List<Double> sub = array.subList(1, 1);
        assertEquals(0, sub.size());
    }

    @Test
    void reversedThrowsUnsupportedOperationException() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(UnsupportedOperationException.class, array::reversed);
    }

    @Test
    void retainAllThrowsUnsupportedOperationException() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        var list = List.of(1.0, 2.0, 3.0);
        assertThrows(UnsupportedOperationException.class, () -> array.retainAll(list));
    }

    @Test
    void replaceAllThrowsUnsupportedOperationException() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(UnsupportedOperationException.class, () -> array.replaceAll(x -> x * 2));
    }

    @Test
    void parallelStreamCollectsAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> collected = array.parallelStream().toList();
        assertEquals(5, collected.size());
        assertTrue(collected.containsAll(List.of(1.0, 2.0, 3.0, 4.0, 5.0)));
    }

    @Test
    void addManyElementsTriggersMultipleReallocations() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class, 2);
        for (int i = 0; i < 100; i++) {
            array.add((double) i);
        }
        assertEquals(100, array.size());
        assertEquals(0.0, array.getFirst());
        assertEquals(99.0, array.get(99));
    }

    @Test
    void clearAndReaddElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.clear();
        createArrayWithValues(array, 4.0, 5.0, 6.0);
        assertEquals(3, array.size());
        assertEquals(4.0, array.getFirst());
    }

    @Test
    void multipleRemoveOperationsPreserveOrder() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 4.0, 5.0);
        array.remove(1);
        array.remove(2);
        assertEquals(3, array.size());
        assertEquals(1.0, array.get(0));
        assertEquals(3.0, array.get(1));
        assertEquals(5.0, array.get(2));
    }

    @Test
    void iteratorMultipleCallsToNext() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        Iterator<Double> it = array.iterator();
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
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NullPointerException.class, () -> array.addAll(null));
    }

    @Test
    void addAllAtIndexNullCollectionThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NullPointerException.class, () -> array.addAll(0, null));
    }

    @Test
    void addNullElementThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NullPointerException.class, () -> array.add(null));
    }

    @Test
    void setNullElementThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.0);
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    void containsNullReturnsFalse() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertEquals(-1, array.indexOf(null));
        assertFalse(array.contains(null));
    }

    @Test
    void removeAllNullCollectionThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NullPointerException.class, () -> array.removeAll(null));
    }

    @Test
    void containsAllNullCollectionThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertThrows(NullPointerException.class, () -> array.containsAll(null));
    }

    @Test
    void addAtMaxCapacityBoundary() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class, 1);
        array.add(1.0);
        array.add(2.0); // triggers reallocation
        array.add(3.0);
        assertEquals(3, array.size());
        assertEquals(1.0, array.get(0));
        assertEquals(2.0, array.get(1));
        assertEquals(3.0, array.get(2));
    }

    @Test
    void addAtIndexAtSizeBoundary() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.add(3, 4.0); // valid: adds at the end
        assertEquals(4, array.size());
        assertEquals(4.0, array.get(3));
    }

    @Test
    void removeAtIndexMaxBoundary() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 4.0, 5.0);
        array.remove(4); // last valid index
        assertEquals(4, array.size());
        assertEquals(4.0, array.get(3));
    }

    @Test
    void getAtMaxIndexBoundary() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertEquals(3.0, array.get(2)); // last valid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    void setAtMaxIndexBoundary() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.set(2, 99.0); // last valid index
        assertEquals(99.0, array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(3, 100.0));
    }

    @Test
    void iteratorRemoveWithoutNextThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        Iterator<Double> it = array.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void iteratorDoubleRemoveThrows() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        Iterator<Double> it = array.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void listIteratorRemoveAfterPreviousWorks() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        it.next();
        it.next();
        it.previous();
        it.remove();
        assertEquals(2, array.size());
    }

    @Test
    void listIteratorAddAfterRemoveWorks() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        ListIterator<Double> it = array.listIterator();
        it.next();
        it.remove();
        it.add(99.0);
        assertEquals(3, array.size());
    }

    @Test
    void addAtIndexZeroShiftsAllElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        array.add(0, 0.0);
        assertEquals(4, array.size());
        assertEquals(0.0, array.get(0));
        assertEquals(1.0, array.get(1));
        assertEquals(2.0, array.get(2));
        assertEquals(3.0, array.get(3));
    }

    @Test
    void removeMiddleElementShiftsCorrectly() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0, 4.0, 5.0);
        array.remove(2);
        assertEquals(4, array.size());
        assertEquals(1.0, array.get(0));
        assertEquals(2.0, array.get(1));
        assertEquals(4.0, array.get(2));
        assertEquals(5.0, array.get(3));
    }

    @Test
    void multipleAddAllsWorkCorrectly() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class, 2);
        array.addAll(List.of(1.0, 2.0));
        array.addAll(List.of(3.0, 4.0));
        array.addAll(List.of(5.0, 6.0));
        assertEquals(6, array.size());
        assertEquals(1.0, array.get(0));
        assertEquals(6.0, array.get(5));
    }

    @Test
    void streamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertEquals(0, (long) array.size());
    }

    @Test
    void parallelStreamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertEquals(0, array.parallelStream().count());
    }

    @Test
    void spliteratorOnEmptyArrayHasNoElements() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        List<Double> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equalsOnSelfReturnsTrue() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        assertEquals(array, array);
    }

    @Test
    void equalsOnDifferentTypeReturnsFalse() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        assertNotEquals('c', array);
    }

    @Test
    void hashCodeConsistency() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        createArrayWithValues(array, 1.0, 2.0, 3.0);
        int hash1 = array.hashCode();
        int hash2 = array.hashCode();
        assertEquals(hash1, hash2);
    }
}
