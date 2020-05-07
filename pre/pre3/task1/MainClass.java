import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {

    private static final String rege = "[a-zA-Z]+(?:-[a-zA-Z]+)?";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Pattern regex = Pattern.compile(rege);
        Matcher mt = regex.matcher(input);
        int count = 0;
        while (mt.find()) {
            count++;
        }
        System.out.println(count);
    }

}
