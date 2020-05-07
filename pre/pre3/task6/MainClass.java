import java.util.Scanner;
import java.util.TreeMap;

public class MainClass {

    private static final String rege = "[^a-zA-Z-]+";
    private static TreeMap<Word,Long> wordSet = new TreeMap<>();

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
        int cnt = 0;
        for (String mt:line.toString().split(rege)) {
            Word tmp =  new Word(mt);
            if (wordSet.containsKey(tmp)) {
                wordSet.put(tmp,wordSet.get(tmp) + 1);
            }
            else {
                wordSet.put(tmp,1L);
            }
            cnt++;
        }
        Long sum = wordSet.values().stream().reduce((long) 0, Long::sum);
        //System.err.println(sum);
        for (Word out:wordSet.keySet()) {
            //System.err.println(wordSet.get(out));
            System.out.println(out.toString());
        }
    }

}
