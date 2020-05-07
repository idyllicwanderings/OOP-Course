import expression.Expression;
import parser.InputParser;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        String input;
        Scanner scan = new Scanner(System.in);
        try {
            input = scan.nextLine();
            InputParser inputExpression = new InputParser(input);
            Expression express = inputExpression.getExpression();
            express.getDerivative();
        } catch (Exception e) {
            System.out.println("WRONG FORMAT!");
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        scan.close();
    }

}
