import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {

    private static final String rege = "[a-zA-Z]+(?:-[a-zA-Z]+)?";
    private static TreeSet<String> wordSet = new TreeSet<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            Pattern regex = Pattern.compile(rege);
            Matcher mt = regex.matcher(input);
            while (mt.find()) {
                wordSet.add(input.substring(mt.start(), mt.end()).toLowerCase());
            }
        }
        for (String out:wordSet) {
            System.out.println(out);
        }
    }

}
