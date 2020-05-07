import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {

    private static final String rege = "[a-zA-Z]+(?:-[a-zA-Z]+)?";
    private static TreeMap<Word,Long> wordSet = new TreeMap<>();
    private static ArrayList<Pair> posSet = new ArrayList<>();
    private static HashMap<Word, TreeSet<Pair>> posMap = new HashMap<>();

    private static boolean isCharacter(char x) {
        return x == '-' || (x >= 65 && x <= 90) || (x >= 97 && x <= 122);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder line = new StringBuilder();
        int row = 0;
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            row++;

            for (int i = 0; i < input.length(); i++) {
                boolean flag = (isCharacter(input.charAt(i)) &&
                        (i == 0 || !isCharacter(input.charAt(i - 1))));
                if (flag
                        && ((i == 0 && line.length() != 0 &&
                        line.charAt(line.length() - 1) == ' ') || i != 0 ||
                        line.length() == 0)) {
                    posSet.add(new Pair(row, i + 1));
                }
            }
            if (input.length() != 0 &&
                    input.charAt(input.length() - 1) == '-') {
                line.append(input.substring(0,input.length() - 1));
            }
            else {
                line.append(input).append(" ");
            }
        }

        // System.out.println(posSet);
        //System.out.println(posSet.size());
        Pattern regex = Pattern.compile(rege);
        Matcher mt = regex.matcher(line);
        int cnt = 0;
        while (mt.find()) {
            Word tmp =  new Word(line.substring(mt.start(),mt.end()).toLowerCase());
            if (wordSet.containsKey(tmp)) {
                wordSet.put(tmp,wordSet.get(tmp) + 1);
                posMap.get(tmp).add(posSet.get(cnt));
            }
            else {
                wordSet.put(tmp,1L);
                posMap.put(tmp,new TreeSet<Pair>());
                posMap.get(tmp).add(posSet.get(cnt));
            }
            cnt++;
        }
        Long sum = wordSet.values().stream().reduce((long) 0, Long::sum);
        //System.err.println(sum);
        for (Word out:wordSet.keySet()) {
            //System.err.println(wordSet.get(out));
            double ret = (double) wordSet.get(out) / sum;
            System.out.println(String.format("%s %d %.2f%%",
                    out, wordSet.get(out),
                    ret * 100));

            for (Pair pair : posMap.get(out)) {
                System.out.println("\t(" + pair.getXa()
                        + ", " + pair.getXb() + ")");
            }
        }
    }

}
