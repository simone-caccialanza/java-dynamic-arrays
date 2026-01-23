package dynarrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ArenaDynArrayIntegerTest {

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
        assertThrows(UnsupportedDynArrayTypeException.class, () -> new ArenaDynArray<>(Object.class));
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
        var list = List.of(1, 2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, list));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, list));
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
    @SuppressWarnings("SuspiciousMethodCalls")
    void containsThrowsOnWrongType() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(ClassCastException.class, () -> array.contains("string"));
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
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        Integer old = array.set(0, 99);
        assertEquals(1, old);
        assertEquals(99, array.getFirst());
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
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(ClassCastException.class, () -> array.remove("string"));
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
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NoSuchElementException.class, () -> array.remove(0));
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
    @SuppressWarnings("DataFlowIssue")
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NullPointerException.class, () -> array.removeIf(null));
    }

    @Test
    @SuppressWarnings("ConstantValue")
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
    @SuppressWarnings("SuspiciousMethodCalls")
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
        var iterator = array.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertTrue(array.iterator().hasNext());
    }

    @Test
    @SuppressWarnings("UseBulkOperation")
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        List<Integer> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of(1, 2, 3), result);
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
    @Disabled("testConfinedArenaThreadViolation -> Re-enable when reviewing implementation")
    void testConfinedArenaThreadViolation() {
        ArenaDynArray<Integer> arr = new ArenaDynArray<>(Integer.class, 8, ArenaDynArray.MemoryManagerType.CONFINED);
        Thread t = new Thread(() -> assertThrows(IllegalStateException.class, () -> arr.add(1)));
        t.start();
        try {
            t.join();
        } catch (InterruptedException _) {
            assert false;
        }
    }

    @Test
    void testSharedArenaThreadAccess() {
        ArenaDynArray<Integer> arr = new ArenaDynArray<>(Integer.class, 8, ArenaDynArray.MemoryManagerType.SHARED);
        Thread t = new Thread(() -> assertDoesNotThrow(() -> arr.add(2)));
        t.start();
        try {
            t.join();
        } catch (InterruptedException _) {
            assert false;
        }
    }

    @Test
    void testGlobalArenaThreadAccess() {
        ArenaDynArray<Integer> arr = new ArenaDynArray<>(Integer.class, 8, ArenaDynArray.MemoryManagerType.GLOBAL);
        Thread t = new Thread(() -> assertDoesNotThrow(() -> arr.add(3)));
        t.start();
        try {
            t.join();
        } catch (InterruptedException _) {
            assert false;
        }
    }

    // ==================== CORNER CASES ====================

    @Test
    void addThrowsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Integer.class, -1));
    }

    @Test
    void addTriggersReallocationWhenCapacityReached() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class, 2);
        array.add(1);
        array.add(2);
        array.add(3); // should trigger reallocation
        assertEquals(3, array.size());
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
        assertEquals(3, array.get(2));
    }

    @Test
    void addAllAtIndexZeroOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.addAll(0, List.of(1, 2, 3));
        assertEquals(3, array.size());
        assertEquals(1, array.getFirst());
    }

    @Test
    void addAllAtEndOfArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2);
        array.addAll(2, List.of(3, 4));
        assertEquals(4, array.size());
        assertEquals(4, array.get(3));
    }

    @Test
    void containsAllOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertFalse(array.containsAll(List.of(1, 2, 3)));
    }

    @Test
    void removeAllRemovesAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.removeAll(List.of(1, 2, 3));
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void removeIfOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertFalse(array.removeIf(_ -> true));
    }

    @Test
    void indexOfWithNullOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertEquals(-1, array.indexOf(null));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void indexOfThrowsOnWrongType() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(ClassCastException.class, () -> array.indexOf("string"));
    }

    @Test
    void lastIndexOfWithNullOnEmptyArray() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertEquals(-1, array.lastIndexOf(null));
    }

    @Test
    void forEachThrowsOnNullAction() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertThrows(NullPointerException.class, () -> array.forEach(null));
    }

    @Test
    void sortThrowsOnNullComparator() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertThrows(NullPointerException.class, () -> array.sort(null));
    }

    @Test
    void toArrayWithNullArgument() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertThrows(IllegalArgumentException.class, () -> array.toArray((Integer[]) null));
    }

    @Test
    @SuppressWarnings("SuspiciousToArrayCall")
    void toArrayWithWrongTypeThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertThrows(IllegalArgumentException.class, () -> array.toArray(new String[1]));
    }

    @Test
    void getFirstOnEmptyArrayThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void getLastOnEmptyArrayThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, array::getLast);
    }

    @Test
    void removeFirstOnEmptyArrayThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NoSuchElementException.class, array::removeFirst);
    }

    @Test
    void removeLastOnEmptyArrayThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NoSuchElementException.class, array::removeLast);
    }

    @Test
    void listIteratorWithIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4);
        ListIterator<Integer> it = array.listIterator(2);
        assertEquals(3, it.next());
        assertEquals(4, it.next());
    }

    @Test
    void listIteratorWithIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(1));
    }

    @Test
    void listIteratorNextIndexReturnsCorrectValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        ListIterator<Integer> it = array.listIterator();
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());
    }

    @Test
    void listIteratorPreviousIndexReturnsCorrectValue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        ListIterator<Integer> it = array.listIterator();
        assertEquals(-1, it.previousIndex());
        it.next();
        assertEquals(0, it.previousIndex());
    }

    @Test
    void listIteratorPreviousThrowsWhenAtStart() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        ListIterator<Integer> it = array.listIterator();
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIteratorNextThrowsWhenAtEnd() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        ListIterator<Integer> it = array.listIterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIteratorRemoveRemovesLastReturnedElement() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        ListIterator<Integer> it = array.listIterator();
        it.next();
        it.remove();
        assertEquals(2, array.size());
        assertEquals(2, array.getFirst());
    }

    @Test
    void listIteratorSetUpdatesLastReturnedElement() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        ListIterator<Integer> it = array.listIterator();
        it.next();
        it.set(99);
        assertEquals(99, array.getFirst());
    }

    @Test
    void listIteratorSetThrowsWhenNotAdvanced() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        ListIterator<Integer> it = array.listIterator();
        assertThrows(IllegalStateException.class, () -> it.set(99));
    }

    @Test
    void listIteratorAddInsertsElement() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 3);
        ListIterator<Integer> it = array.listIterator();
        it.next();
        it.add(2);
        assertEquals(3, array.size());
        assertEquals(2, array.get(1));
    }

    @Test
    void subListThrowsOnNegativeFromIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(-1, 2));
    }

    @Test
    void subListThrowsWhenToIndexExceedsSize() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(0, 4));
    }

    @Test
    void subListThrowsWhenFromIndexGreaterThanToIndex() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(2, 1));
    }

    @Test
    void subListReturnsEmptyListWhenFromEqualsTo() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        List<Integer> sub = array.subList(1, 1);
        assertEquals(0, sub.size());
    }

    @Test
    void reversedReturnsReversedList() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4, 5);
        List<Integer> reversed = array.reversed();
        assertEquals(5, reversed.size());
        assertEquals(5, reversed.get(0));
        assertEquals(4, reversed.get(1));
        assertEquals(3, reversed.get(2));
        assertEquals(2, reversed.get(3));
        assertEquals(1, reversed.get(4));
    }

    @Test
    void retainAllKeepsOnlySpecifiedElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4, 5);
        boolean modified = array.retainAll(List.of(2, 4));
        assertTrue(modified);
        assertEquals(2, array.size());
        assertEquals(2, array.get(0));
        assertEquals(4, array.get(1));
    }

    @Test
    void replaceAllReplacesAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.replaceAll(x -> x * 2);
        assertEquals(3, array.size());
        assertEquals(2, array.get(0));
        assertEquals(4, array.get(1));
        assertEquals(6, array.get(2));
    }

    @Test
    void parallelStreamCollectsAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4, 5);
        List<Integer> collected = array.parallelStream().toList();
        assertEquals(5, collected.size());
        assertTrue(collected.containsAll(List.of(1, 2, 3, 4, 5)));
    }

    // ==================== TEST WITH OTHER TYPES ====================

    @Test
    void longArrayWorks() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(100L);
        array.add(200L);
        assertEquals(2, array.size());
        assertEquals(100L, array.get(0));
        assertEquals(200L, array.get(1));
    }

    @Test
    void longArraySorting() {
        ArenaDynArray<Long> array = new ArenaDynArray<>(Long.class);
        array.add(3L);
        array.add(1L);
        array.add(2L);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Long[]{1L, 2L, 3L}, array.toArray(new Long[0]));
    }

    @Test
    void floatArrayWorks() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(1.5f);
        array.add(2.5f);
        assertEquals(2, array.size());
        assertEquals(1.5f, array.getFirst());
    }

    @Test
    void floatArraySorting() {
        ArenaDynArray<Float> array = new ArenaDynArray<>(Float.class);
        array.add(3.5f);
        array.add(1.5f);
        array.add(2.5f);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Float[]{1.5f, 2.5f, 3.5f}, array.toArray(new Float[0]));
    }

    @Test
    void doubleArrayWorks() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(1.5);
        array.add(2.5);
        assertEquals(2, array.size());
        assertEquals(1.5, array.getFirst());
    }

    @Test
    void doubleArraySorting() {
        ArenaDynArray<Double> array = new ArenaDynArray<>(Double.class);
        array.add(3.5);
        array.add(1.5);
        array.add(2.5);
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Double[]{1.5, 2.5, 3.5}, array.toArray(new Double[0]));
    }

    @Test
    void charArrayWorks() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        array.add('b');
        array.add('c');
        assertEquals(3, array.size());
        assertEquals('b', array.get(1));
    }

    @Test
    void charArraySorting() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('c');
        array.add('a');
        array.add('b');
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Character[]{'a', 'b', 'c'}, array.toArray(new Character[0]));
    }

    @Test
    void booleanArrayWorks() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        array.add(false);
        array.add(true);
        assertEquals(3, array.size());
        assertTrue(array.get(0));
        assertFalse(array.get(1));
    }

    @Test
    void booleanArraySorting() {
        ArenaDynArray<Boolean> array = new ArenaDynArray<>(Boolean.class);
        array.add(true);
        array.add(false);
        array.add(true);
        array.add(false);
        array.sort(Comparator.naturalOrder());
        // false < true -> [false, false, true, true]
        assertEquals(false, array.get(0));
        assertEquals(false, array.get(1));
        assertEquals(true, array.get(2));
        assertEquals(true, array.get(3));
    }

    // ==================== STRESS TESTS AND CAPACITY TESTS ====================

    @Test
    void addManyElementsTriggersMultipleReallocations() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class, 2);
        for (int i = 0; i < 100; i++) {
            array.add(i);
        }
        assertEquals(100, array.size());
        assertEquals(0, array.getFirst());
        assertEquals(99, array.get(99));
    }

    @Test
    void clearAndReaddElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.clear();
        createIntArrayWithValues(array, 4, 5, 6);
        assertEquals(3, array.size());
        assertEquals(4, array.getFirst());
    }

    @Test
    void multipleRemoveOperationsPreserveOrder() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4, 5);
        array.remove(1);
        array.remove(2);
        assertEquals(3, array.size());
        assertEquals(1, array.get(0));
        assertEquals(3, array.get(1));
        assertEquals(5, array.get(2));
    }

    @Test
    void iteratorMultipleCallsToNext() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        Iterator<Integer> it = array.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(3, count);
        assertThrows(NoSuchElementException.class, it::next);
    }

    // ==================== ADDITIONAL CORNER CASES ====================

    @Test
    void addAllNullCollectionThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NullPointerException.class, () -> array.addAll(null));
    }

    @Test
    void addAllAtIndexNullCollectionThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NullPointerException.class, () -> array.addAll(0, null));
    }

    @Test
    void addNullElementThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NullPointerException.class, () -> array.add(null));
    }

    @Test
    void setNullElementThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        array.add(1);
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    void containsNullReturnsFalse() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertEquals(-1, array.indexOf(null));
        assertFalse(array.contains(null));
    }

    @Test
    void removeAllNullCollectionThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NullPointerException.class, () -> array.removeAll(null));
    }

    @Test
    void containsAllNullCollectionThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertThrows(NullPointerException.class, () -> array.containsAll(null));
    }

    @Test
    void addAtMaxCapacityBoundary() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class, 1);
        array.add(1);
        array.add(2); // triggers reallocation
        array.add(3);
        assertEquals(3, array.size());
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
        assertEquals(3, array.get(2));
    }

    @Test
    void addAtIndexAtSizeBoundary() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.add(3, 4); // valid: adds at the end
        assertEquals(4, array.size());
        assertEquals(4, array.get(3));
    }

    @Test
    void removeAtIndexMaxBoundary() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4, 5);
        array.remove(4); // last valid index
        assertEquals(4, array.size());
        assertEquals(4, array.get(3));
    }

    @Test
    void getAtMaxIndexBoundary() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertEquals(3, array.get(2)); // last valid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    void setAtMaxIndexBoundary() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.set(2, 99); // last valid index
        assertEquals(99, array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(3, 100));
    }

    @Test
    void iteratorRemoveWithoutNextThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        Iterator<Integer> it = array.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void iteratorDoubleRemoveThrows() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        Iterator<Integer> it = array.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void listIteratorRemoveAfterPreviousWorks() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        ListIterator<Integer> it = array.listIterator();
        it.next();
        it.next();
        it.previous();
        it.remove();
        assertEquals(2, array.size());
    }

    @Test
    void listIteratorAddAfterRemoveWorks() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        ListIterator<Integer> it = array.listIterator();
        it.next();
        it.remove();
        it.add(99);
        assertEquals(3, array.size());
    }

    @Test
    @Disabled("This test needs a big implementation")
    void subListModificationAffectsOriginal() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4);
        List<Integer> sub = array.subList(1, 3);
        sub.set(0, 99);
        assertEquals(99, array.get(1));
    }

    @Test
    void addAtIndexZeroShiftsAllElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        array.add(0, 0);
        assertEquals(4, array.size());
        assertEquals(0, array.get(0));
        assertEquals(1, array.get(1));
        assertEquals(2, array.get(2));
        assertEquals(3, array.get(3));
    }

    @Test
    void removeMiddleElementShiftsCorrectly() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3, 4, 5);
        array.remove(2);
        assertEquals(4, array.size());
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
        assertEquals(4, array.get(2));
        assertEquals(5, array.get(3));
    }

    @Test
    void multipleAddAllsWorkCorrectly() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class, 2);
        array.addAll(List.of(1, 2));
        array.addAll(List.of(3, 4));
        array.addAll(List.of(5, 6));
        assertEquals(6, array.size());
        assertEquals(1, array.get(0));
        assertEquals(6, array.get(5));
    }

    @Test
    void streamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertEquals(0, (long) array.size());
    }

    @Test
    void parallelStreamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertEquals(0, array.parallelStream().count());
    }

    @Test
    void spliteratorOnEmptyArrayHasNoElements() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        List<Integer> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(0, result.size());
    }

    @Test
    void constructorWithZeroCapacityWorks() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class, 0);
        assertEquals(0, array.size());
        array.add(1);
        assertEquals(1, array.size());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equalsOnSelfReturnsTrue() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        assertEquals(array, array);
    }

    @Test
    void equalsOnDifferentTypeReturnsFalse() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        assertNotEquals('c', array);
    }

    @Test
    void hashCodeConsistency() {
        ArenaDynArray<Integer> array = new ArenaDynArray<>(Integer.class);
        createIntArrayWithValues(array, 1, 2, 3);
        int hash1 = array.hashCode();
        int hash2 = array.hashCode();
        assertEquals(hash1, hash2);
    }
}
