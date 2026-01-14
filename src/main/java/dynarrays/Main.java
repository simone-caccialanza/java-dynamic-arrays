package dynarrays;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
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