package dynarrays;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class Main {
    public static void main(String[] args) {
        var ararr = new ArenaDynArray<>(Integer.class);
        ararr.add(3);
        ararr.add(4);
        ararr.add(5);
        ararr.remove(Integer.valueOf(4));


        Utils.printArenaDynArray(ararr);
        System.out.println(ararr);
    }

    private static void print(Object o) {
        System.out.println(o);
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