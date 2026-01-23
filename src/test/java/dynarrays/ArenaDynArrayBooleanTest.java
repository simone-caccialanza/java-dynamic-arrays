package dynarrays;

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
        assertThrows(ClassCastException.class, () -> array.contains("string"));
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
        assertTrue(array.remove(Boolean.TRUE));
        assertEquals(2, array.size());
        assertEquals(false, array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertFalse(array.remove(Boolean.FALSE));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(ClassCastException.class, () -> array.remove("string"));
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
        assertThrows(NoSuchElementException.class, () -> array.remove(0));
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

    @Test
    void listIteratorIteratesAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.addAll(List.of(true, false, true));
        ListIterator<Boolean> it = array.listIterator();
        assertTrue(it.hasNext());
        assertEquals(true, it.next());
        assertEquals(false, it.next());
        assertEquals(true, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(true, it.previous());
    }

    @Test
    void subListReturnsCorrectElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.addAll(List.of(true, false, true, false));
        List<Boolean> sub = array.subList(1, 3);
        assertEquals(2, sub.size());
        assertEquals(false, sub.get(0));
        assertEquals(true, sub.get(1));
    }

    @Test
    void spliteratorIteratesAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.addAll(List.of(true, false, true));
        List<Boolean> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(List.of(true, false, true), result);
    }

    @Test
    void streamCollectsAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.addAll(List.of(false, true, false));
        List<Boolean> collected = array.stream().toList();
        assertEquals(List.of(false, true, false), collected);
    }

    @Test
    void addThrowsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Boolean.class, -1));
    }

    @Test
    void addTriggersReallocationWhenCapacityReached() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class, 2);
        array.add(true);
        array.add(false);
        array.add(true); // should trigger reallocation
        assertEquals(3, array.size());
        assertEquals(true, array.get(0));
        assertEquals(false, array.get(1));
        assertEquals(true, array.get(2));
    }

    @Test
    void addAllAtIndexZeroOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.addAll(0, List.of(true, false, true));
        assertEquals(3, array.size());
        assertEquals(true, array.getFirst());
    }

    @Test
    void addAllAtEndOfArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false);
        array.addAll(2, List.of(true, false));
        assertEquals(4, array.size());
        assertEquals(false, array.get(3));
    }

    @Test
    void containsAllOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertFalse(array.containsAll(List.of(true, false)));
    }

    @Test
    void removeAllRemovesAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.removeAll(List.of(true, false));
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void removeIfOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertFalse(array.removeIf(_ -> true));
    }

    @Test
    void indexOfWithNullOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertEquals(-1, array.indexOf(null));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void indexOfThrowsOnWrongType() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(ClassCastException.class, () -> array.indexOf("string"));
    }

    @Test
    void lastIndexOfWithNullOnEmptyArray() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertEquals(-1, array.lastIndexOf(null));
    }

    @Test
    void forEachThrowsOnNullAction() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertThrows(NullPointerException.class, () -> array.forEach(null));
    }

    @Test
    void sortThrowsOnNullComparator() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertThrows(NullPointerException.class, () -> array.sort(null));
    }

    @Test
    void toArrayWithNullArgument() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertThrows(IllegalArgumentException.class, () -> array.toArray((Boolean[]) null));
    }

    @Test
    @SuppressWarnings("SuspiciousToArrayCall")
    void toArrayWithWrongTypeThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertThrows(IllegalArgumentException.class, () -> array.toArray(new String[1]));
    }

    @Test
    void getFirstOnEmptyArrayThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void getLastOnEmptyArrayThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, array::getLast);
    }

    @Test
    void removeFirstOnEmptyArrayThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NoSuchElementException.class, array::removeFirst);
    }

    @Test
    void removeLastOnEmptyArrayThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NoSuchElementException.class, array::removeLast);
    }

    @Test
    void listIteratorWithIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false);
        ListIterator<Boolean> it = array.listIterator(2);
        assertEquals(true, it.next());
        assertEquals(false, it.next());
    }

    @Test
    void listIteratorWithIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(1));
    }

    @Test
    void listIteratorNextIndexReturnsCorrectValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        ListIterator<Boolean> it = array.listIterator();
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());
    }

    @Test
    void listIteratorPreviousIndexReturnsCorrectValue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        ListIterator<Boolean> it = array.listIterator();
        assertEquals(-1, it.previousIndex());
        it.next();
        assertEquals(0, it.previousIndex());
    }

    @Test
    void listIteratorPreviousThrowsWhenAtStart() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        ListIterator<Boolean> it = array.listIterator();
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIteratorNextThrowsWhenAtEnd() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        ListIterator<Boolean> it = array.listIterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIteratorRemoveRemovesLastReturnedElement() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        ListIterator<Boolean> it = array.listIterator();
        it.next();
        it.remove();
        assertEquals(2, array.size());
        assertEquals(false, array.getFirst());
    }

    @Test
    void listIteratorSetUpdatesLastReturnedElement() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        ListIterator<Boolean> it = array.listIterator();
        it.next();
        it.set(false);
        assertEquals(false, array.getFirst());
    }

    @Test
    void listIteratorSetThrowsWhenNotAdvanced() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        ListIterator<Boolean> it = array.listIterator();
        assertThrows(IllegalStateException.class, () -> it.set(false));
    }

    @Test
    void listIteratorAddInsertsElement() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, true);
        ListIterator<Boolean> it = array.listIterator();
        it.next();
        it.add(false);
        assertEquals(3, array.size());
        assertEquals(false, array.get(1));
    }

    @Test
    void subListThrowsOnNegativeFromIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(-1, 2));
    }

    @Test
    void subListThrowsWhenToIndexExceedsSize() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(0, 4));
    }

    @Test
    void subListThrowsWhenFromIndexGreaterThanToIndex() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(2, 1));
    }

    @Test
    void subListReturnsEmptyListWhenFromEqualsTo() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        List<Boolean> sub = array.subList(1, 1);
        assertEquals(0, sub.size());
    }

    @Test
    void reversedReturnsReversedList() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false, true);
        List<Boolean> reversed = array.reversed();
        assertEquals(5, reversed.size());
        assertEquals(true, reversed.get(0));
        assertEquals(false, reversed.get(1));
        assertEquals(true, reversed.get(2));
        assertEquals(false, reversed.get(3));
        assertEquals(true, reversed.get(4));
    }

    @Test
    void retainAllKeepsOnlySpecifiedElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false, true);
        boolean modified = array.retainAll(List.of(true));
        assertTrue(modified);
        assertEquals(3, array.size());
        assertEquals(true, array.get(0));
        assertEquals(true, array.get(1));
        assertEquals(true, array.get(2));
    }

    @Test
    void replaceAllReplacesAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.replaceAll(x -> !x);
        assertEquals(3, array.size());
        assertEquals(false, array.get(0));
        assertEquals(true, array.get(1));
        assertEquals(false, array.get(2));
    }

    @Test
    void parallelStreamCollectsAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false, true);
        List<Boolean> collected = array.parallelStream().toList();
        assertEquals(5, collected.size());
        assertTrue(collected.containsAll(List.of(true, false)));
    }

    @Test
    void addManyElementsTriggersMultipleReallocations() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class, 2);
        for (int i = 0; i < 100; i++) {
            array.add(i % 2 == 0);
        }
        assertEquals(100, array.size());
        assertEquals(true, array.getFirst());
        assertEquals(false, array.get(99));
    }

    @Test
    void clearAndReaddElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.clear();
        createArrayWithValues(array, false, true, false);
        assertEquals(3, array.size());
        assertEquals(false, array.getFirst());
    }

    @Test
    void multipleRemoveOperationsPreserveOrder() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false, true);
        array.remove(1);
        array.remove(2);
        assertEquals(3, array.size());
        assertEquals(true, array.get(0));
        assertEquals(true, array.get(1));
        assertEquals(true, array.get(2));
    }

    @Test
    void iteratorMultipleCallsToNext() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        Iterator<Boolean> it = array.iterator();
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
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NullPointerException.class, () -> array.addAll(null));
    }

    @Test
    void addAllAtIndexNullCollectionThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NullPointerException.class, () -> array.addAll(0, null));
    }

    @Test
    void addNullElementThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NullPointerException.class, () -> array.add(null));
    }

    @Test
    void setNullElementThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    void containsNullReturnsFalse() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertEquals(-1, array.indexOf(null));
        assertFalse(array.contains(null));
    }

    @Test
    void removeAllNullCollectionThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NullPointerException.class, () -> array.removeAll(null));
    }

    @Test
    void containsAllNullCollectionThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertThrows(NullPointerException.class, () -> array.containsAll(null));
    }

    @Test
    void addAtMaxCapacityBoundary() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class, 1);
        array.add(true);
        array.add(false); // triggers reallocation
        array.add(true);
        assertEquals(3, array.size());
        assertEquals(true, array.get(0));
        assertEquals(false, array.get(1));
        assertEquals(true, array.get(2));
    }

    @Test
    void addAtIndexAtSizeBoundary() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.add(3, false); // valid: adds at the end
        assertEquals(4, array.size());
        assertEquals(false, array.get(3));
    }

    @Test
    void removeAtIndexMaxBoundary() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false, true);
        array.remove(4); // last valid index
        assertEquals(4, array.size());
        assertEquals(false, array.get(3));
    }

    @Test
    void getAtMaxIndexBoundary() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertEquals(true, array.get(2)); // last valid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    void setAtMaxIndexBoundary() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.set(2, false); // last valid index
        assertEquals(false, array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(3, true));
    }

    @Test
    void iteratorRemoveWithoutNextThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        Iterator<Boolean> it = array.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void iteratorDoubleRemoveThrows() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        Iterator<Boolean> it = array.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void listIteratorRemoveAfterPreviousWorks() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        ListIterator<Boolean> it = array.listIterator();
        it.next();
        it.next();
        it.previous();
        it.remove();
        assertEquals(2, array.size());
    }

    @Test
    void listIteratorAddAfterRemoveWorks() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        ListIterator<Boolean> it = array.listIterator();
        it.next();
        it.remove();
        it.add(true);
        assertEquals(3, array.size());
    }

    @Test
    void addAtIndexZeroShiftsAllElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        array.add(0, false);
        assertEquals(4, array.size());
        assertEquals(false, array.get(0));
        assertEquals(true, array.get(1));
        assertEquals(false, array.get(2));
        assertEquals(true, array.get(3));
    }

    @Test
    void removeMiddleElementShiftsCorrectly() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true, false, true);
        array.remove(2);
        assertEquals(4, array.size());
        assertEquals(true, array.get(0));
        assertEquals(false, array.get(1));
        assertEquals(false, array.get(2));
        assertEquals(true, array.get(3));
    }

    @Test
    void multipleAddAllsWorkCorrectly() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class, 2);
        array.addAll(List.of(true, false));
        array.addAll(List.of(true, false));
        array.addAll(List.of(true, false));
        assertEquals(6, array.size());
        assertEquals(true, array.get(0));
        assertEquals(false, array.get(5));
    }

    @Test
    void streamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertEquals(0, (long) array.size());
    }

    @Test
    void parallelStreamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertEquals(0, array.parallelStream().count());
    }

    @Test
    void spliteratorOnEmptyArrayHasNoElements() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        List<Boolean> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equalsOnSelfReturnsTrue() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        assertEquals(array, array);
    }

    @Test
    void equalsOnDifferentTypeReturnsFalse() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        assertNotEquals(1, array);
    }

    @Test
    void hashCodeConsistency() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        createArrayWithValues(array, true, false, true);
        int hash1 = array.hashCode();
        int hash2 = array.hashCode();
        assertEquals(hash1, hash2);
    }
}
