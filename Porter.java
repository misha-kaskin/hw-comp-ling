import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Porter {
    static List<Function<String, String>> functionList = List.of(
            Porter::step1,
            Porter::step2a,
            Porter::step2b,
            Porter::step3,
            Porter::step4,
            Porter::step5,
            Porter::step6,
            Porter::step7a,
            Porter::step7b
    );
    static boolean isSuccess = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String str = scanner.nextLine();
            if ("exit".equalsIgnoreCase(str)) {
                return;
            }
            isSuccess = false;
            for (Function<String, String> f : functionList) {
                str = f.apply(str);
            }
            System.out.println(str);
        }
    }

    static String step1(String str) {
        if (str.endsWith("sses")) {
            return str.substring(0, str.length() - 4) + "ss";
        }

        if (str.endsWith("ies")) {
            return str.substring(0, str.length() - 3) + "i";
        }

        if (str.endsWith("ss")) {
            return str.substring(0, str.length() - 2) + "ss";
        }

        if (str.endsWith("s")) {
            return str.substring(0, str.length() - 1);
        }

        return str;
    }

    static String step2a(String str) {
        if (str.endsWith("eed")) {
            String str1 = makeSubstring(str, "eed");
            if (countM(str1) > 0) {
                return str1 + "ee";
            }
            return str;
        }

        if (str.endsWith("ed")) {
            String str1 = makeSubstring(str, "ed");
            if (stemContainVowel(str1)) {
                isSuccess = true;
                return str1;
            }
            return str;
        }

        if (str.endsWith("ing")) {
            String str1 = makeSubstring(str, "ing");
            if (stemContainVowel(str1)) {
                isSuccess = true;
                return str1;
            }
            return str;
        }

        return str;
    }

    static String step2b(String str) {
        if (!isSuccess) {
            return str;
        }

        if (str.endsWith("at")) {
            return str + "e";
        }

        if (str.endsWith("bl")) {
            return str + "e";
        }

        if (str.endsWith("iz")) {
            return str + "e";
        }

        if (isDoubleC(str)) {
            if (!(str.endsWith("l") || str.endsWith("s") || str.endsWith("z"))) {
                return str.substring(0, str.length() - 1);
            }
            return str;
        }

        if (countM(str) == 1 && oCondition(str)) {
            return str + "e";
        }

        return str;
    }

    static String step3(String str) {
        if (str.endsWith("y")) {
            String str1 = str.substring(0, str.length() - 1);
            if (stemContainVowel(str1)) {
                return str1 + "i";
            }
        }

        return str;
    }

    static String step4(String str) {
        Map<String, String> map1 = Map.of(
                "ational", "ate",
                "ization", "ize"
        );

        Map<String, String> map2 = Map.of(
                "tional", "tion",
                "enci", "ence",
                "anci", "ance",
                "izer", "ize",
                "abli", "able",
                "alli", "al",
                "entli", "ent",
                "eli", "e",
                "ousli", "ous"
        );

        Map<String, String> map3 = Map.of(
                "ation", "ate",
                "ator", "ate",
                "alism", "al",
                "iveness", "ive",
                "fulness", "ful",
                "ousness", "ous",
                "aliti", "al",
                "iviti", "ive",
                "biliti", "ble"
        );

        List<Map<String, String>> mapList = List.of(map1, map2, map3);

        for (Map<String, String> map : mapList) {
            for (var entry : map.entrySet()) {
                if (str.endsWith(entry.getKey())) {
                    String str1 = makeSubstring(str, entry.getKey());
                    if (countM(str1) > 0) {
                        return str1 + entry.getValue();
                    }
                    return str;
                }
            }
        }

        return str;
    }

    static String step5(String str) {
        Map<String, String> map = Map.of(
                "icate", "ic",
                "ative", "",
                "alize", "al",
                "iciti", "ic",
                "ful", "",
                "ness", ""
        );

        for (var entry : map.entrySet()) {
            if (str.endsWith(entry.getKey())) {
                String str1 = makeSubstring(str, entry.getKey());
                if (countM(str1) > 0) {
                    return str1 + entry.getValue();
                }
                return str;
            }
        }

        return str;
    }

    static String step6(String str) {
        List<String> list = List.of(
                "al",
                "ance",
                "ence",
                "er",
                "ic",
                "ible",
                "ant",
                "ement",
                "ment",
                "ent",
                "ou",
                "ism",
                "ate",
                "iti",
                "ous",
                "ive",
                "ize"
        );

        for (String suffix : list) {
            if (str.endsWith(suffix)) {
                String str1 = makeSubstring(str, suffix);
                if (countM(str1) > 1) {
                    return str1;
                }
                return str;
            }
        }

        if (str.endsWith("ion")) {
            String str1 = makeSubstring(str, "ion");
            if (str1.endsWith("s") || str1.endsWith("t")) {
                return str1;
            }
            return str;
        }

        return str;
    }

    static String step7a(String str) {
        if (str.endsWith("e")) {
            String str1 = makeSubstring(str, "e");
            if (countM(str1) > 1 || countM(str1) == 1 && !oCondition(str1)) {
                return str1;
            }
            return str;
        }

        return str;
    }

    static String step7b(String str) {
        if (countM(str) > 1 && isDoubleC(str) && str.endsWith("l")) {
            return makeSubstring(str, "l");
        }
        return str;
    }

    private static String makeSubstring(String str, String suffix) {
        return str.substring(0, str.length() - suffix.length());
    }

    private static boolean oCondition(String str) {
        char[] arr = getVC(str);
        int len = arr.length;
        if (len > 2) {
            if (arr[len - 3] == 'c' && arr[len - 2] == 'v' && arr[len - 1] == 'c') {
                char c = str.charAt(len - 1);
                return c != 'w' && c != 'x' && c != 'y';
            }
        }
        return false;
    }

    private static boolean isDoubleC(String str) {
        char[] arr = getVC(str);
        int len = arr.length;
        if (len > 1) {
            return arr[len - 1] == arr[len - 2]
                    && arr[len - 2] == 'c'
                    && str.charAt(len - 1) == str.charAt(len - 2);
        }
        return false;
    }

    private static int countM(String str) {
        char[] arr = getVC(str);
        int m = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] == 'v' && arr[i] == 'c') {
                m++;
            }
        }
        return m;
    }

    private static boolean stemContainVowel(String str) {
        char[] arr = getVC(str);
        for (char c : arr) {
            if (c == 'v') {
                return true;
            }
        }
        return false;
    }

    private static char[] getVC(String str) {
        char[] arr = new char[str.length()];
        String vowels = "aeiou";
        for (int i = 0; i < arr.length; i++) {
            if (vowels.contains(str.charAt(i) + "")) {
                arr[i] = 'v';
            } else {
                if (i > 0 && str.charAt(i) == 'y') {
                    if (arr[i - 1] == 'c') {
                        arr[i] = 'v';
                    } else {
                        arr[i] = 'c';
                    }
                } else {
                    arr[i] = 'c';
                }
            }
        }
        return arr;
    }
}
