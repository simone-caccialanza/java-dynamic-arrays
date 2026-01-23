package dynarrays;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ArenaDynArrayCharacterTest {

    void createArrayWithValues(ArenaDynArray<Character> array, char... values) {
        for (char v : values) array.add(v);
    }

    @Test
    void sizeIsZeroOnNewArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertEquals(0, array.size());
    }

    @Test
    void isEmptyTrueOnNewArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertTrue(array.isEmpty());
    }

    @Test
    void isEmptyFalseAfterAdd() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertFalse(array.isEmpty());
    }

    @Test
    void addIncreasesSizeAndStoresValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('x');
        assertEquals(1, array.size());
        assertEquals('x', array.getFirst());
    }

    @Test
    void addThrowsOnUnsupportedType() {
        assertThrows(UnsupportedDynArrayTypeException.class, () -> new ArenaDynArray<>(Object.class));
    }

    @Test
    void addAllAddsAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.addAll(List.of('a', 'b', 'c'));
        assertEquals(3, array.size());
        assertEquals('b', array.get(1));
    }

    @Test
    void addAllAtIndexInsertsElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.addAll(1, List.of('x', 'y'));
        assertEquals(5, array.size());
        assertEquals('x', array.get(1));
        assertEquals('y', array.get(2));
    }

    @Test
    void addAllAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        var list = List.of('a', 'b', 'c');
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(-1, list));
        assertThrows(IndexOutOfBoundsException.class, () -> array.addAll(1, list));
    }

    @Test
    void containsReturnsTrueForPresentValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('z');
        assertTrue(array.contains('z'));
    }

    @Test
    void containsReturnsFalseForAbsentValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('z');
        assertFalse(array.contains('y'));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void containsThrowsOnWrongType() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(ClassCastException.class, () -> array.contains("string"));
    }

    @Test
    void getReturnsCorrectValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('m');
        assertEquals('m', array.getFirst());
    }

    @Test
    void getThrowsOnInvalidIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void setReplacesValueAndReturnsOld() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        Character old = array.set(0, 'z');
        assertEquals('a', old);
        assertEquals('z', array.getFirst());
    }

    @Test
    void setThrowsOnInvalidIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, 'a'));
    }

    @Test
    void addAtIndexInsertsValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('b');
        array.add(0, 'a');
        assertEquals('a', array.get(0));
        assertEquals('b', array.get(1));
    }

    @Test
    void addAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 'a'));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 'a'));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'a');
        assertTrue(array.remove(Character.valueOf('a')));
        assertEquals(2, array.size());
        assertEquals('b', array.getFirst());
    }

    @Test
    void removeByValueReturnsFalseIfAbsent() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertFalse(array.remove(Character.valueOf('b')));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void removeByValueThrowsOnWrongType() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(ClassCastException.class, () -> array.remove("string"));
    }

    @Test
    void removeAtIndexRemovesCorrectValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        Character removed = array.remove(1);
        assertEquals('b', removed);
        assertEquals(2, array.size());
        assertEquals('c', array.get(1));
    }

    @Test
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    void removeAtIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NoSuchElementException.class, () -> array.remove(0));
    }

    @Test
    void containsAllReturnsTrueIfAllPresent() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertTrue(array.containsAll(List.of('a', 'b')));
    }

    @Test
    void containsAllReturnsFalseIfAnyAbsent() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertFalse(array.containsAll(List.of('a', 'd')));
    }

    @Test
    void removeAllRemovesAllPresentValues() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'b');
        array.removeAll(List.of('b', 'c'));
        assertEquals(1, array.size());
        assertEquals('a', array.getFirst());
    }

    @Test
    void removeIfRemovesMatchingValues() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd');
        array.removeIf(c -> c > 'b');
        assertEquals(2, array.size());
        assertEquals('a', array.get(0));
        assertEquals('b', array.get(1));
    }

    @Test
    void removeIfReturnsFalseIfNoMatch() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertFalse(array.removeIf(c -> c > 'z'));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void removeIfThrowsOnNullPredicate() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NullPointerException.class, () -> array.removeIf(null));
    }

    @Test
    @SuppressWarnings("ConstantValue")
    void clearEmptiesArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void indexOfReturnsCorrectIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'b');
        assertEquals(1, array.indexOf('b'));
    }

    @Test
    void indexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertEquals(-1, array.indexOf('d'));
    }

    @Test
    void lastIndexOfReturnsCorrectIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'b');
        assertEquals(3, array.lastIndexOf('b'));
    }

    @Test
    void lastIndexOfReturnsMinusOneIfAbsent() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertEquals(-1, array.lastIndexOf('d'));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void lastIndexOfThrowsOnWrongType() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IllegalArgumentException.class, () -> array.lastIndexOf("string"));
    }

    @Test
    void iteratorHasNextFalseOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertFalse(array.iterator().hasNext());
    }

    @Test
    void iteratorNextThrowsOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        var iterator = array.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorHasNextTrueOnNonEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertTrue(array.iterator().hasNext());
    }

    @Test
    @SuppressWarnings("UseBulkOperation")
    void forEachExecutesActionOnAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        List<Character> result = new ArrayList<>();
        array.forEach(result::add);
        assertEquals(List.of('a', 'b', 'c'), result);
    }

    @Test
    void toArrayReturnsObjectArrayWithValues() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        array.add('b');
        array.add('c');
        Object[] result = array.toArray();
        assertArrayEquals(new Object[]{'a', 'b', 'c'}, result);
    }

    @Test
    void toArrayTArrayReturnsFilledArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('x');
        array.add('y');
        Character[] input = new Character[2];
        Character[] result = array.toArray(input);
        assertArrayEquals(new Character[]{'x', 'y'}, result);
    }

    @Test
    void toArrayTArrayExpandsAndFillsArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        array.add('b');
        array.add('c');
        Character[] input = new Character[2];
        Character[] result = array.toArray(input);
        assertArrayEquals(new Character[]{'a', 'b', 'c'}, result);
    }

    @Test
    void toArrayTArraySetsNullIfArrayIsLarger() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('z');
        Character[] input = new Character[3];
        Character[] result = array.toArray(input);
        assertEquals('z', result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
    }

    @Test
    void toArrayIntFunctionReturnsFilledArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('m');
        array.add('n');
        Character[] result = array.toArray(Character[]::new);
        assertArrayEquals(new Character[]{'m', 'n'}, result);
    }

    @Test
    void toArrayEmptyArrayReturnsEmpty() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertArrayEquals(new Object[0], array.toArray());
        assertArrayEquals(new Character[0], array.toArray(Character[]::new));
        assertArrayEquals(new Character[0], array.toArray(new Character[0]));
    }

    @Test
    void sortSortsInAscendingOrder() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'e', 'b', 'z', 'a', 'c');
        array.sort(Comparator.naturalOrder());
        assertArrayEquals(new Character[]{'a', 'b', 'c', 'e', 'z'}, array.toArray(new Character[0]));
    }

    @Test
    void sortSortsInDescendingOrder() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'e', 'b', 'z', 'a', 'c');
        array.sort(Comparator.reverseOrder());
        assertArrayEquals(new Character[]{'z', 'e', 'c', 'b', 'a'}, array.toArray(new Character[0]));
    }

    @Test
    void sortOnEmptyArrayDoesNothing() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.sort(Comparator.naturalOrder());
        assertEquals(0, array.size());
    }

    @Test
    void constructorWithZeroCapacityWorks() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class, 0);
        assertEquals(0, array.size());
        array.add('a');
        assertEquals(1, array.size());
    }

    @Test
    void listIteratorIteratesAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.addAll(List.of('a', 'b', 'c'));
        ListIterator<Character> it = array.listIterator();
        assertTrue(it.hasNext());
        assertEquals('a', it.next());
        assertEquals('b', it.next());
        assertEquals('c', it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals('c', it.previous());
    }

    @Test
    void subListReturnsCorrectElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.addAll(List.of('w', 'x', 'y', 'z'));
        List<Character> sub = array.subList(1, 3);
        assertEquals(2, sub.size());
        assertEquals('x', sub.get(0));
        assertEquals('y', sub.get(1));
    }

    @Test
    void spliteratorIteratesAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.addAll(List.of('p', 'q', 'r'));
        List<Character> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(List.of('p', 'q', 'r'), result);
    }

    @Test
    void streamCollectsAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.addAll(List.of('s', 't', 'u'));
        List<Character> collected = array.stream().toList();
        assertEquals(List.of('s', 't', 'u'), collected);
    }

    @Test
    void addThrowsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArenaDynArray<>(Character.class, -1));
    }

    @Test
    void addTriggersReallocationWhenCapacityReached() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class, 2);
        array.add('a');
        array.add('b');
        array.add('c'); // should trigger reallocation
        assertEquals(3, array.size());
        assertEquals('a', array.get(0));
        assertEquals('b', array.get(1));
        assertEquals('c', array.get(2));
    }

    @Test
    void addAllAtIndexZeroOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.addAll(0, List.of('a', 'b', 'c'));
        assertEquals(3, array.size());
        assertEquals('a', array.getFirst());
    }

    @Test
    void addAllAtEndOfArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b');
        array.addAll(2, List.of('c', 'd'));
        assertEquals(4, array.size());
        assertEquals('d', array.get(3));
    }

    @Test
    void containsAllOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertFalse(array.containsAll(List.of('a', 'b', 'c')));
    }

    @Test
    void removeAllRemovesAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.removeAll(List.of('a', 'b', 'c'));
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
    }

    @Test
    void removeIfOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertFalse(array.removeIf(_ -> true));
    }

    @Test
    void indexOfWithNullOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertEquals(-1, array.indexOf(null));
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void indexOfThrowsOnWrongType() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(ClassCastException.class, () -> array.indexOf("string"));
    }

    @Test
    void lastIndexOfWithNullOnEmptyArray() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertEquals(-1, array.lastIndexOf(null));
    }

    @Test
    void forEachThrowsOnNullAction() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertThrows(NullPointerException.class, () -> array.forEach(null));
    }

    @Test
    void sortThrowsOnNullComparator() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertThrows(NullPointerException.class, () -> array.sort(null));
    }

    @Test
    void toArrayWithNullArgument() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertThrows(IllegalArgumentException.class, () -> array.toArray((Character[]) null));
    }

    @Test
    @SuppressWarnings("SuspiciousToArrayCall")
    void toArrayWithWrongTypeThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertThrows(IllegalArgumentException.class, () -> array.toArray(new String[1]));
    }

    @Test
    void getFirstOnEmptyArrayThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IndexOutOfBoundsException.class, array::getFirst);
    }

    @Test
    void getLastOnEmptyArrayThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IndexOutOfBoundsException.class, array::getLast);
    }

    @Test
    void removeFirstOnEmptyArrayThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NoSuchElementException.class, array::removeFirst);
    }

    @Test
    void removeLastOnEmptyArrayThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NoSuchElementException.class, array::removeLast);
    }

    @Test
    void listIteratorWithIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd');
        ListIterator<Character> it = array.listIterator(2);
        assertEquals('c', it.next());
        assertEquals('d', it.next());
    }

    @Test
    void listIteratorWithIndexThrowsOnInvalidIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.listIterator(1));
    }

    @Test
    void listIteratorNextIndexReturnsCorrectValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        ListIterator<Character> it = array.listIterator();
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());
    }

    @Test
    void listIteratorPreviousIndexReturnsCorrectValue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        ListIterator<Character> it = array.listIterator();
        assertEquals(-1, it.previousIndex());
        it.next();
        assertEquals(0, it.previousIndex());
    }

    @Test
    void listIteratorPreviousThrowsWhenAtStart() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        ListIterator<Character> it = array.listIterator();
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIteratorNextThrowsWhenAtEnd() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        ListIterator<Character> it = array.listIterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIteratorRemoveRemovesLastReturnedElement() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        ListIterator<Character> it = array.listIterator();
        it.next();
        it.remove();
        assertEquals(2, array.size());
        assertEquals('b', array.getFirst());
    }

    @Test
    void listIteratorSetUpdatesLastReturnedElement() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        ListIterator<Character> it = array.listIterator();
        it.next();
        it.set('z');
        assertEquals('z', array.getFirst());
    }

    @Test
    void listIteratorSetThrowsWhenNotAdvanced() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        ListIterator<Character> it = array.listIterator();
        assertThrows(IllegalStateException.class, () -> it.set('z'));
    }

    @Test
    void listIteratorAddInsertsElement() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'c');
        ListIterator<Character> it = array.listIterator();
        it.next();
        it.add('b');
        assertEquals(3, array.size());
        assertEquals('b', array.get(1));
    }

    @Test
    void subListThrowsOnNegativeFromIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(-1, 2));
    }

    @Test
    void subListThrowsWhenToIndexExceedsSize() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(0, 4));
    }

    @Test
    void subListThrowsWhenFromIndexGreaterThanToIndex() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertThrows(IndexOutOfBoundsException.class, () -> array.subList(2, 1));
    }

    @Test
    void subListReturnsEmptyListWhenFromEqualsTo() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        List<Character> sub = array.subList(1, 1);
        assertEquals(0, sub.size());
    }

    @Test
    void reversedReturnsReversedList() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd', 'e');
        List<Character> reversed = array.reversed();
        assertEquals(5, reversed.size());
        assertEquals('e', reversed.get(0));
        assertEquals('d', reversed.get(1));
        assertEquals('c', reversed.get(2));
        assertEquals('b', reversed.get(3));
        assertEquals('a', reversed.get(4));
    }

    @Test
    void retainAllKeepsOnlySpecifiedElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd', 'e');
        boolean modified = array.retainAll(List.of('b', 'd'));
        assertTrue(modified);
        assertEquals(2, array.size());
        assertEquals('b', array.get(0));
        assertEquals('d', array.get(1));
    }

    @Test
    void replaceAllReplacesAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.replaceAll(x -> (char) (x + 1));
        assertEquals(3, array.size());
        assertEquals('b', array.get(0));
        assertEquals('c', array.get(1));
        assertEquals('d', array.get(2));
    }

    @Test
    void parallelStreamCollectsAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd', 'e');
        List<Character> collected = array.parallelStream().toList();
        assertEquals(5, collected.size());
        assertTrue(collected.containsAll(List.of('a', 'b', 'c', 'd', 'e')));
    }

    @Test
    void addManyElementsTriggersMultipleReallocations() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class, 2);
        for (int i = 0; i < 26; i++) {
            array.add((char) ('a' + i));
        }
        assertEquals(26, array.size());
        assertEquals('a', array.getFirst());
        assertEquals('z', array.get(25));
    }

    @Test
    void clearAndReaddElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.clear();
        createArrayWithValues(array, 'd', 'e', 'f');
        assertEquals(3, array.size());
        assertEquals('d', array.getFirst());
    }

    @Test
    void multipleRemoveOperationsPreserveOrder() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd', 'e');
        array.remove(1);
        array.remove(2);
        assertEquals(3, array.size());
        assertEquals('a', array.get(0));
        assertEquals('c', array.get(1));
        assertEquals('e', array.get(2));
    }

    @Test
    void iteratorMultipleCallsToNext() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        Iterator<Character> it = array.iterator();
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
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NullPointerException.class, () -> array.addAll(null));
    }

    @Test
    void addAllAtIndexNullCollectionThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NullPointerException.class, () -> array.addAll(0, null));
    }

    @Test
    void addNullElementThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NullPointerException.class, () -> array.add(null));
    }

    @Test
    void setNullElementThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        array.add('a');
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    void containsNullReturnsFalse() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertEquals(-1, array.indexOf(null));
        assertFalse(array.contains(null));
    }

    @Test
    void removeAllNullCollectionThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NullPointerException.class, () -> array.removeAll(null));
    }

    @Test
    void containsAllNullCollectionThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertThrows(NullPointerException.class, () -> array.containsAll(null));
    }

    @Test
    void addAtMaxCapacityBoundary() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class, 1);
        array.add('a');
        array.add('b'); // triggers reallocation
        array.add('c');
        assertEquals(3, array.size());
        assertEquals('a', array.get(0));
        assertEquals('b', array.get(1));
        assertEquals('c', array.get(2));
    }

    @Test
    void addAtIndexAtSizeBoundary() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.add(3, 'd'); // valid: adds at the end
        assertEquals(4, array.size());
        assertEquals('d', array.get(3));
    }

    @Test
    void removeAtIndexMaxBoundary() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd', 'e');
        array.remove(4); // last valid index
        assertEquals(4, array.size());
        assertEquals('d', array.get(3));
    }

    @Test
    void getAtMaxIndexBoundary() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertEquals('c', array.get(2)); // last valid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    void setAtMaxIndexBoundary() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.set(2, 'z'); // last valid index
        assertEquals('z', array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(3, 'x'));
    }

    @Test
    void iteratorRemoveWithoutNextThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        Iterator<Character> it = array.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void iteratorDoubleRemoveThrows() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        Iterator<Character> it = array.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void listIteratorRemoveAfterPreviousWorks() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        ListIterator<Character> it = array.listIterator();
        it.next();
        it.next();
        it.previous();
        it.remove();
        assertEquals(2, array.size());
    }

    @Test
    void listIteratorAddAfterRemoveWorks() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        ListIterator<Character> it = array.listIterator();
        it.next();
        it.remove();
        it.add('x');
        assertEquals(3, array.size());
    }

    @Test
    void addAtIndexZeroShiftsAllElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        array.add(0, 'z');
        assertEquals(4, array.size());
        assertEquals('z', array.get(0));
        assertEquals('a', array.get(1));
        assertEquals('b', array.get(2));
        assertEquals('c', array.get(3));
    }

    @Test
    void removeMiddleElementShiftsCorrectly() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c', 'd', 'e');
        array.remove(2);
        assertEquals(4, array.size());
        assertEquals('a', array.get(0));
        assertEquals('b', array.get(1));
        assertEquals('d', array.get(2));
        assertEquals('e', array.get(3));
    }

    @Test
    void multipleAddAllsWorkCorrectly() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class, 2);
        array.addAll(List.of('a', 'b'));
        array.addAll(List.of('c', 'd'));
        array.addAll(List.of('e', 'f'));
        assertEquals(6, array.size());
        assertEquals('a', array.get(0));
        assertEquals('f', array.get(5));
    }

    @Test
    void streamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertEquals(0, (long) array.size());
    }

    @Test
    void parallelStreamOnEmptyArrayReturnsEmptyStream() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertEquals(0, array.parallelStream().count());
    }

    @Test
    void spliteratorOnEmptyArrayHasNoElements() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        List<Character> result = new ArrayList<>();
        array.spliterator().forEachRemaining(result::add);
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equalsOnSelfReturnsTrue() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        assertEquals(array, array);
    }

    @Test
    void equalsOnDifferentTypeReturnsFalse() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        assertNotEquals(1, array);
    }

    @Test
    void hashCodeConsistency() {
        ArenaDynArray<Character> array = new ArenaDynArray<>(Character.class);
        createArrayWithValues(array, 'a', 'b', 'c');
        int hash1 = array.hashCode();
        int hash2 = array.hashCode();
        assertEquals(hash1, hash2);
    }
}
