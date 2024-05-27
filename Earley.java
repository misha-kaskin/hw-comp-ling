import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class Earley {
    private static final String symbol = "@";
    private static final char startSymbol = 'E';
    private static final String empty = "eps";

    public static void main(String[] args) throws IOException {
        String name;
        if (args.length > 0) {
            name = args[0];
        } else {
            name = "grammar2.txt";
        }

        List<Character> n = new ArrayList<>();
        List<Character> sigma = new ArrayList<>();

        byte[] bytes = Files.readAllBytes(Path.of(name));
        String s1 = new String(bytes);
        String[] s1Split = s1.split("\n");

        String[] split = s1Split[0].split(",");
        for (String item : split) {
            char c = item.trim().toCharArray()[0];
            n.add(c);
        }

        split = s1Split[1].split(",");
        for (String value : split) {
            char c = value.trim().toCharArray()[0];
            sigma.add(c);
        }

        split = s1Split[2].split(",");
        List<String> p = new ArrayList<>(Arrays.asList(split));
        List<Rule> rules = p.stream()
                .map(el -> el.trim().replaceAll("\\s+", ""))
                .map(el -> new Rule(el.charAt(0), (el.split("->")[1].equals(empty)) ? "" : el.split("->")[1]))
                .collect(toList());

        String s = s1Split[3].split(",")[0].trim();
        Grammar g = new Grammar(n, sigma, rules, s);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String w = scanner.nextLine().trim().replaceAll("\\s+", "");
            if ("exit".equalsIgnoreCase(w)) {
                return;
            }
            System.out.println(earley(g, w));
        }
    }

    static boolean earley(Grammar g, String w) {
        int len = w.length();
        g.p.add(new Rule(startSymbol, g.s));

        Dynamic[] d = new Dynamic[len + 1];
        for (int i = 0; i < len + 1; i++) {
            d[i] = new Dynamic();
        }

        d[0].situations.add(new Situation(startSymbol, symbol + g.s, 0));

        for (int j = 0; j < len + 1; j++) {
            scan(d, j, w);
            int prevSize;
            int curSize = d[j].situations.size();
            do {
                prevSize = curSize;
                complete(d, j);
                predict(d, j, g);
                curSize = d[j].situations.size();
            } while (prevSize != curSize);
        }

        return d[len].situations.contains(new Situation(startSymbol, g.s + symbol, 0));
    }

    static void scan(Dynamic[] d, int j, String w) {
        if (j == 0) {
            return;
        }

        for (int i = 0; i < d[j - 1].situations.size(); i++) {
            Situation sit = d[j - 1].situations.get(i);
            int idx = sit.str.indexOf(symbol);
            if (idx != sit.str.length() - 1) {
                char sym = sit.str.charAt(idx + 1);
                if (sym == w.charAt(j - 1)) {
                    String str1 = sit.str.replaceAll(symbol, "")
                            .substring(0, idx + 1)
                            + symbol
                            + sit.str.substring(idx + 2);

                    Situation sit1 = new Situation(sit.left, str1, sit.idx);
                    if (!d[j].situations.contains(sit1)) {
                        d[j].situations.add(new Situation(sit.left, str1, sit.idx));
                    }
                }
            }
        }
    }

    static void complete(Dynamic[] d, int j) {
        boolean flag = true;
        int i = 0;

        while (flag) {
            Set<Situation> tmpSet = new HashSet<>();
            int i1 = i;

            for (int k = i; k < d[j].situations.size(); k++) {
                Situation sit = d[j].situations.get(k);
                if (sit.str.indexOf(symbol) == sit.str.length() - 1) {
                    for (int l = 0; l < d[sit.idx].situations.size(); l++) {
                        Situation sit2 = d[sit.idx].situations.get(l);
                        if (sit2.str.contains(symbol + sit.left)) {
                            int idx = sit2.str.indexOf(symbol);
                            String str1 = sit2.str.replaceAll(symbol, "")
                                    .substring(0, idx + 1)
                                    + symbol
                                    + sit2.str.substring(idx + 2);
                            tmpSet.add(new Situation(sit2.left, str1, sit2.idx));
                        }
                    }
                }
                i1++;
            }

            i = i1;
            flag = false;

            for (Situation sit : tmpSet) {
                if (!d[j].situations.contains(sit)) {
                    flag = true;
                    d[j].situations.add(sit);
                }
            }
        }
    }

    static void predict(Dynamic[] d, int j, Grammar g) {
        boolean flag = true;
        int i = 0;

        while (flag) {
            Set<Situation> tmpSet = new HashSet<>();
            int i1 = i;

            for (int k = i; k < d[j].situations.size(); k++) {
                Situation sit = d[j].situations.get(k);
                int idx = sit.str.indexOf(symbol);
                if (idx != sit.str.length() - 1) {
                    char sym = sit.str.charAt(idx + 1);
                    for (Rule rule : g.p) {
                        if (rule.left == sym) {
                            String str1 = symbol + rule.str;
                            tmpSet.add(new Situation(sym, str1, j));
                        }
                    }
                }
                i1++;
            }

            i = i1;
            flag = false;

            for (Situation sit : tmpSet) {
                if (!d[j].situations.contains(sit)) {
                    flag = true;
                    d[j].situations.add(sit);
                }
            }
        }
    }

    static class Rule {
        char left;
        String str;

        Rule(char left, String str) {
            this.left = left;
            this.str = str;
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "left=" + left +
                    ", str='" + str + '\'' +
                    '}';
        }
    }

    static class Situation {
        char left;
        String str;
        int idx;

        Situation(char left, String str, int idx) {
            this.left = left;
            this.str = str;
            this.idx = idx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Situation situation = (Situation) o;
            return left == situation.left && idx == situation.idx && Objects.equals(str, situation.str);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, str, idx);
        }

        @Override
        public String toString() {
            return "Situation{" +
                    "left=" + left +
                    ", str='" + str + '\'' +
                    ", idx=" + idx +
                    '}';
        }
    }

    static class Dynamic {
        List<Situation> situations = new ArrayList<>();

        @Override
        public String toString() {
            return "Dynamic{" +
                    "situations=" + situations +
                    '}';
        }
    }

    static class Grammar {
        List<Character> n;
        List<Character> sigma;
        List<Rule> p;
        String s;

        Grammar(List<Character> n, List<Character> sigma, List<Rule> p, String s) {
            this.n = n;
            this.sigma = sigma;
            this.p = p;
            this.s = s;
        }
    }
}
