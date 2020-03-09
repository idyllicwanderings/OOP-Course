import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {

    private static final String rege = "[a-zA-Z]+(?:-[a-zA-Z]+)?";
    private static TreeMap<String,Long> wordSet = new TreeMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder line = new StringBuilder();
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.length() != 0 &&
                    input.charAt(input.length() - 1) == '-') {
                line.append(input.substring(0,input.length() - 1));
            }
            else {
                line.append(input);
                line.append(" ");
            }
        }
        Pattern regex = Pattern.compile(rege);
        Matcher mt = regex.matcher(line);
        while (mt.find()) {
            String tmp = line.substring(mt.start(),mt.end()).toLowerCase();
            if (wordSet.containsKey(tmp)) {
                wordSet.put(tmp,wordSet.get(tmp) + 1);
            }
            else {
                wordSet.put(tmp,1L);
            }
        }
        Long sum = wordSet.values().stream().reduce((long) 0, Long::sum);
        //System.err.println(sum);
        for (String out:wordSet.keySet()) {
            //System.err.println(wordSet.get(out));
            double ret = (double) wordSet.get(out) / sum;
            System.out.println(String.format("%s %d %.2f%%",
                    out, wordSet.get(out),
                    ret * 100));

        }
    }

}
