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
}
