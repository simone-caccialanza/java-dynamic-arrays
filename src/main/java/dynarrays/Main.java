package dynarrays;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class Main {
    public static void main(String[] args) {
        var ararr = new ArenaDynArray<>(Integer.class);
        ararr.add(5);
        ararr.add(5);
        ararr.add(3);
        ararr.add(4);
        ararr.add(1);
        ararr.add(49);
        ararr.add(-5);
        ararr.sort(Integer::compare);
        Utils.printArenaDynArray(ararr);

        var ararr2 = new ArenaDynArray<>(Character.class);
        ararr2.add('c');
        ararr2.add('e');
        ararr2.add('h');
        ararr2.add('f');
        ararr2.add('z');
        ararr2.add('a');
        ararr2.sort(Character::compare);
        Utils.printArenaDynArray(ararr2);

        var ararr3 = new ArenaDynArray<>(String.class);
        try{
        ararr3.add("Hello");
        ararr3.add("World");
        ararr3.add("!");
        ararr3.sort(String::compareTo);
        Utils.printArenaDynArray(ararr3);
        } catch (Throwable _) {
            System.out.println("String is not yet supported");
        }
    }



    private static void basicArenaUsage() {
        String s = "My string\n";
        try (Arena arena = Arena.ofConfined()) {

            MemorySegment nativeText = arena.allocateFrom(s);

            for (int i = 0; i < s.length(); i++ ) {
                System.out.print((char)nativeText.get(ValueLayout.JAVA_BYTE, i));
            }
        }
    }
}