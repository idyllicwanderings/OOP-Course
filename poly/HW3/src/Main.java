import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String input;
        Scanner scan = new Scanner(System.in);
        long startTime = System.currentTimeMillis();
        try {
            input = scan.nextLine();
            //System.err.println(input.length());
            InputParser inputExpression = new InputParser(input,0);
            Expression express = inputExpression.getExpression();
            express = express.derive();
            //System.err.println("derive: " + express.toString());
            //express = express.simplify(startTime);
            System.out.println(express.toString());
            //System.err.println(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            System.out.println("WRONG FORMAT!");
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        scan.close();
    }
}

