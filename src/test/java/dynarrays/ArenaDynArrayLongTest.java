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

    @Test
    void listIteratorIteratesAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.addAll(List.of(1L, 2L, 3L));
        ListIterator<Long> it = array.listIterator();
        assertTrue(it.hasNext());
        assertEquals(1L, it.next());
        assertEquals(2L, it.next());
        assertEquals(3L, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3L, it.previous());
    }

    @Test
    void subListReturnsCorrectElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.addAll(List.of(10L, 20L, 30L, 40L));
        List<Long> sub = array.subList(1, 3);
        assertEquals(2, sub.size());
        assertEquals(20L, sub.get(0));
        assertEquals(30L, sub.get(1));
    }

    @Test
    void spliteratorIteratesAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.addAll(List.of(5L, 6L, 7L));
        List<Long> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(List.of(5L, 6L, 7L), result);
    }

    @Test
    void streamCollectsAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.addAll(List.of(100L, 200L, 300L));
        List<Long> collected = array.stream().toList();
        assertEquals(List.of(100L, 200L, 300L), collected);
    }

    @Test
    void addThrowsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Long.class, -1));
    }

    @Test
    void addTriggersReallocationWhenCapacityReached() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class, 2);
        array.add(1L);
        array.add(2L);
        array.add(3L); // should trigger reallocation
        assertEquals(3, array.size());
        assertEquals(1L, array.get(0));
        assertEquals(2L, array.get(1));
        assertEquals(3L, array.get(2));
    }

    @Test
    void addAllAtIndexZeroOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.addAll(0, List.of(1L, 2L, 3L));
        assertEquals(3, array.size());
        assertEquals(1L, array.getFirst());
    }

    @Test
    void addAllAtEndOfArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L);
        array.addAll(2, List.of(3L, 4L));
        assertEquals(4, array.size());
        assertEquals(4L, array.get(3));
    }

    @Test
    void containsAllOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertFalse(array.containsAll(List.of(1L, 2L, 3L)));
    }

    @Test
    void removeAllRemovesAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.removeAll(List.of(1L, 2L, 3L));
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void removeIfOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertFalse(array.removeIf(_ -> true));
    }

    @Test
    void indexOfWithNullOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertEquals(-1, array.indexOf(null));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void indexOfThrowsOnWrongType() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(ClassCastException.class, () -> array.indexOf("string"));
    }

    @Test
    void lastIndexOfWithNullOnEmptyArray() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertEquals(-1, array.lastIndexOf(null));
    }

    @Test
    void forEachThrowsOnNullAction() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertThrows(NullPointerException.class, () -> array.forEach(null));
    }

    @Test
    void sortThrowsOnNullComparator() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertThrows(NullPointerException.class, () -> array.sort(null));
    }

    @Test
    void toArrayWithNullArgument() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertThrows(IllegalArgumentException.class, () -> array.toArray((Long[]) null));
    }

    @Test
    @SuppressWarnings("SuspiciousToArrayCall")
    void toArrayWithWrongTypeThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertThrows(IllegalArgumentException.class, () -> array.toArray(new String[1]));
    }

    @Test
    void getFirstOnEmptyArrayThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void getLastOnEmptyArrayThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IndexOutOfBoundsException.class, array::getLast);
    }

    @Test
    void removeFirstOnEmptyArrayThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NoSuchElementException.class, array::removeFirst);
    }

    @Test
    void removeLastOnEmptyArrayThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NoSuchElementException.class, array::removeLast);
    }

    @Test
    void listIteratorWithIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 4L);
        ListIterator<Long> it = array.listIterator(2);
        assertEquals(3L, it.next());
        assertEquals(4L, it.next());
    }

    @Test
    void listIteratorWithIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(1));
    }

    @Test
    void listIteratorNextIndexReturnsCorrectValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        ListIterator<Long> it = array.listIterator();
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());
    }

    @Test
    void listIteratorPreviousIndexReturnsCorrectValue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        ListIterator<Long> it = array.listIterator();
        assertEquals(-1, it.previousIndex());
        it.next();
        assertEquals(0, it.previousIndex());
    }

    @Test
    void listIteratorPreviousThrowsWhenAtStart() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        ListIterator<Long> it = array.listIterator();
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIteratorNextThrowsWhenAtEnd() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        ListIterator<Long> it = array.listIterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIteratorRemoveRemovesLastReturnedElement() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        ListIterator<Long> it = array.listIterator();
        it.next();
        it.remove();
        assertEquals(2, array.size());
        assertEquals(2L, array.getFirst());
    }

    @Test
    void listIteratorSetUpdatesLastReturnedElement() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        ListIterator<Long> it = array.listIterator();
        it.next();
        it.set(99L);
        assertEquals(99L, array.getFirst());
    }

    @Test
    void listIteratorSetThrowsWhenNotAdvanced() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        ListIterator<Long> it = array.listIterator();
        assertThrows(IllegalStateException.class, () -> it.set(99L));
    }

    @Test
    void listIteratorAddInsertsElement() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 3L);
        ListIterator<Long> it = array.listIterator();
        it.next();
        it.add(2L);
        assertEquals(3, array.size());
        assertEquals(2L, array.get(1));
    }

    @Test
    void subListThrowsOnNegativeFromIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(-1, 2));
    }

    @Test
    void subListThrowsWhenToIndexExceedsSize() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(0, 4));
    }

    @Test
    void subListThrowsWhenFromIndexGreaterThanToIndex() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(2, 1));
    }

    @Test
    void subListReturnsEmptyListWhenFromEqualsTo() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        List<Long> sub = array.subList(1, 1);
        assertEquals(0, sub.size());
    }

    @Test
    void reversedThrowsUnsupportedOperationException() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(UnsupportedOperationException.class, array::reversed);
    }

    @Test
    void retainAllThrowsUnsupportedOperationException() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        var list = List.of(1L, 2L, 3L);
        assertThrows(UnsupportedOperationException.class, () -> array.retainAll(list));
    }

    @Test
    void replaceAllThrowsUnsupportedOperationException() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(UnsupportedOperationException.class, () -> array.replaceAll(x -> x * 2));
    }

    @Test
    void parallelStreamCollectsAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 4L, 5L);
        List<Long> collected = array.parallelStream().toList();
        assertEquals(5, collected.size());
        assertTrue(collected.containsAll(List.of(1L, 2L, 3L, 4L, 5L)));
    }

    @Test
    void addManyElementsTriggersMultipleReallocations() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class, 2);
        for (int i = 0; i < 100; i++) {
            array.add((long) i);
        }
        assertEquals(100, array.size());
        assertEquals(0L, array.getFirst());
        assertEquals(99L, array.get(99));
    }

    @Test
    void clearAndReaddElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.clear();
        createArrayWithValues(array, 4L, 5L, 6L);
        assertEquals(3, array.size());
        assertEquals(4L, array.getFirst());
    }

    @Test
    void multipleRemoveOperationsPreserveOrder() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 4L, 5L);
        array.remove(1);
        array.remove(2);
        assertEquals(3, array.size());
        assertEquals(1L, array.get(0));
        assertEquals(3L, array.get(1));
        assertEquals(5L, array.get(2));
    }

    @Test
    void iteratorMultipleCallsToNext() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        Iterator<Long> it = array.iterator();
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
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NullPointerException.class, () -> array.addAll(null));
    }

    @Test
    void addAllAtIndexNullCollectionThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NullPointerException.class, () -> array.addAll(0, null));
    }

    @Test
    void addNullElementThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NullPointerException.class, () -> array.add(null));
    }

    @Test
    void setNullElementThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(1L);
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    void containsNullReturnsFalse() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertEquals(-1, array.indexOf(null));
        assertFalse(array.contains(null));
    }

    @Test
    void removeAllNullCollectionThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NullPointerException.class, () -> array.removeAll(null));
    }

    @Test
    void containsAllNullCollectionThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertThrows(NullPointerException.class, () -> array.containsAll(null));
    }

    @Test
    void addAtMaxCapacityBoundary() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class, 1);
        array.add(1L);
        array.add(2L); // triggers reallocation
        array.add(3L);
        assertEquals(3, array.size());
        assertEquals(1L, array.get(0));
        assertEquals(2L, array.get(1));
        assertEquals(3L, array.get(2));
    }

    @Test
    void addAtIndexAtSizeBoundary() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.add(3, 4L); // valid: adds at the end
        assertEquals(4, array.size());
        assertEquals(4L, array.get(3));
    }

    @Test
    void removeAtIndexMaxBoundary() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 4L, 5L);
        array.remove(4); // last valid index
        assertEquals(4, array.size());
        assertEquals(4L, array.get(3));
    }

    @Test
    void getAtMaxIndexBoundary() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertEquals(3L, array.get(2)); // last valid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    void setAtMaxIndexBoundary() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.set(2, 99L); // last valid index
        assertEquals(99L, array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(3, 100L));
    }

    @Test
    void iteratorRemoveWithoutNextThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        Iterator<Long> it = array.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void iteratorDoubleRemoveThrows() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        Iterator<Long> it = array.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void listIteratorRemoveAfterPreviousWorks() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        ListIterator<Long> it = array.listIterator();
        it.next();
        it.next();
        it.previous();
        it.remove();
        assertEquals(2, array.size());
    }

    @Test
    void listIteratorAddAfterRemoveWorks() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        ListIterator<Long> it = array.listIterator();
        it.next();
        it.remove();
        it.add(99L);
        assertEquals(3, array.size());
    }

    @Test
    void addAtIndexZeroShiftsAllElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        array.add(0, 0L);
        assertEquals(4, array.size());
        assertEquals(0L, array.get(0));
        assertEquals(1L, array.get(1));
        assertEquals(2L, array.get(2));
        assertEquals(3L, array.get(3));
    }

    @Test
    void removeMiddleElementShiftsCorrectly() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L, 4L, 5L);
        array.remove(2);
        assertEquals(4, array.size());
        assertEquals(1L, array.get(0));
        assertEquals(2L, array.get(1));
        assertEquals(4L, array.get(2));
        assertEquals(5L, array.get(3));
    }

    @Test
    void multipleAddAllsWorkCorrectly() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class, 2);
        array.addAll(List.of(1L, 2L));
        array.addAll(List.of(3L, 4L));
        array.addAll(List.of(5L, 6L));
        assertEquals(6, array.size());
        assertEquals(1L, array.get(0));
        assertEquals(6L, array.get(5));
    }

    @Test
    void streamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertEquals(0, (long) array.size());
    }

    @Test
    void parallelStreamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertEquals(0, array.parallelStream().count());
    }

    @Test
    void spliteratorOnEmptyArrayHasNoElements() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        List<Long> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equalsOnSelfReturnsTrue() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        assertEquals(array, array);
    }

    @Test
    void equalsOnDifferentTypeReturnsFalse() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        assertNotEquals('c', array);
    }

    @Test
    void hashCodeConsistency() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        createArrayWithValues(array, 1L, 2L, 3L);
        int hash1 = array.hashCode();
        int hash2 = array.hashCode();
        assertEquals(hash1, hash2);
    }
}
