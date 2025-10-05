package dynarrays;

public class Utils {

    public static void printArenaDynArray(ArenaDynArray<?> arr) {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.size()-1; i++) {
            sb.append(arr.get(i)).append(", ");
        }
        sb.append(arr.get(arr.size()-1)).append("]");
        System.out.println(sb);
    }

    public static void print(Object o) {
        System.out.println(o);
    }

}
