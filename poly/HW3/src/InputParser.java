import Const;
import Derivative;
import Power;
import Function;
import Tri;
import java.math.BigInteger;

public class InputParser {

    private Expression expression;
    private String original;
    private Integer cursor;
    private static final String legal = "[\\d+\\-* \\txcosin()]+";
    private static final BigInteger maxBound = new BigInteger("50");
    private boolean isInnerExpr = false;

    private enum Sign {
        ADD, SUB
    }

    public InputParser(String input,int start) throws Exception {
        expression = new Expression();
        cursor = start;
        this.original = input;
        checkLegal(original);
        expression.add(parseExpression());
        parseSpace();
        if (cursor < original.length()) {
            throw new Exception("Ended!");
        }
    }

    private boolean hasNext() {
        return cursor < original.length();
    }

    private void checkLegal(String input) throws Exception {
        if (!input.matches(legal)) {
            throw new Exception("Illegal Characters!");
        }
    }

    private char peek() throws Exception {
        if (cursor >= original.length()) {
            throw new Exception("Reaches the end unexpectedly!");
        }
        return original.charAt(cursor);
    }

    private char poll() {
        return original.charAt(cursor++);
    }

    private Expression parseExpression() throws Exception {
        /* @expression: [+/-] term {+/- term }*/
        Expression ret = new Expression();
        parseSpace();
        Sign curSign = Sign.ADD;
        if (isSign(peek())) {
            curSign = parseSign();
        }
        parseSpace();
        while (hasNext()) {
            ret.add(parseTerm(curSign));
            parseSpace();
            if (hasNext() && isSign(peek())) {
                curSign = parseSign();
            }
            else {
                break;
            }
            parseSpace();
        }
        return ret;
    }

    private Term parseTerm(Sign sign) throws Exception {
        //* @term: firstfactor {restfactor}
        isInnerExpr = false;
        Term re = new Term(new Factor(new Const("1")));
        Derivative factor = parseFirstFactor();
        re = re.mult(factor);
        parseSpace();
        while (hasNext() && peek() == '*') {
            selfAssert('*');
            parseSpace();
            isInnerExpr = false;
            Derivative factor1 = parseFactor();
            re = re.mult(factor1);
            parseSpace();
        }
        if (sign.equals(Sign.SUB)) {
            re = re.negate();
        }
        isInnerExpr = false;
        return re;
    }

    private Derivative parseFirstFactor() throws Exception {
        // FirstFactor : [+/-] x/cos(x)/sin(x)/int [^ int] /(expr)
        Sign sign = Sign.ADD;
        if (isSign(peek())) {
            sign = parseSign();
            parseSpace();
        }
        if (sign.equals(Sign.SUB)) {
            return parseFactor().negate();
        }
        return parseFactor();
    }

    private Derivative parseFactor() throws Exception {
        // FirstFactor : [+/-] x/cos(x)/sin(x)/int [^ int] /(expr)
        Factor factor;
        parseSpace();
        BigInteger integer = BigInteger.ONE;
        if (isDigit(peek()) || isSign(peek())) {
            factor = new Factor(new Const(parseSignedInteger()));
            parseSpace();
            return factor;
        }
        else if (peek() == '(') {
            cursor++;
            Expression expression = parseExpression();
            selfAssert(')');
            isInnerExpr = true;
            return expression;

        }
        else {
            Derivative function = parseFunction();
            parseSpace();
            return function;
        }
    }

    private Derivative parseFunction() throws Exception {
        //function: x/cos(poly)/sin(poly)/[^ int]
        parseSpace();
        String idenfer = parseIdenfer();
        if (idenfer.equals("x")) {
            parseSpace();
            return parsePowerFunction();
        } else {
            parseSpace();
            return parseTriFunction(idenfer);
        }
    }

    private String getSignIdenfer() throws Exception {
        int start = cursor;
        while (peek() == '*') {
            cursor++;
        }
        String ret = original.substring(start, cursor);
        cursor = start;
        return ret;
    }

    private Factor parsePowerFunction() throws Exception {
        parseSpace();
        BigInteger integer = BigInteger.ONE;
        if (hasNext() && getSignIdenfer().equals("**")) {
            cursor++;
            cursor++;
            parseSpace();
            integer = parseSignedInteger();
            checkOutOfIndex(integer);
            parseSpace();
        }
        return new Factor(integer,new Power());
    }

    private void checkOutOfIndex(BigInteger bigInteger) throws Exception {
        if (bigInteger.abs().compareTo(maxBound) > 0) {
            throw new Exception("Index Out of max Bound!");
        }
    }

    private boolean isCharacter(char ch) {
        return Character.isLowerCase(ch);
    }

    private String parseIdenfer() throws Exception {
        int start = cursor;
        while (hasNext() && isCharacter(peek())) {
            poll();
        }
        return original.substring(start, cursor);
    }

    private void selfAssert(char ch) throws Exception {
        if (peek() != ch) {
            throw new Exception("Expected \"" + ch
                    + "\" rather than \"" + peek() +  "at " + cursor + "\"!");
        }
        poll();
    }

    private Factor parseTriFunction(String func) throws Exception {
        Expression expression;
        parseSpace();
        selfAssert('(');
        parseSpace();
        Derivative der = parseFactor();
        if (der instanceof Factor) {
            expression = new Expression(new Term(der));
        }
        else if (der instanceof Term) {
            expression = new Expression((Term)der);
        }
        else if (der instanceof Expression) {
            expression = (Expression)der;
        }
        else {
            expression = new Expression();
            throw new Exception("parseTri Function Wrong!");
        }
        parseSpace();
        selfAssert(')');
        parseSpace();
        BigInteger integer = BigInteger.ONE;
        if (hasNext() && getSignIdenfer().equals("**")) {
            cursor++;
            cursor++;
            parseSpace();
            integer = parseSignedInteger();
            checkOutOfIndex(integer);
            parseSpace();
        } else {
            ;
        }
        isInnerExpr = false;
        return new Factor(integer,
                new Tri(expression,Function.valueOf(func.toUpperCase())));
    }

    private BigInteger parseSignedInteger() throws Exception {
        int start;
        start = cursor;
        if (isSign(peek())) {
            poll();
        }
        if (!isDigit(peek())) {
            throw new Exception("Expected Digital Number in Signed Number!:" + peek());
        }
        while (hasNext() && isDigit(peek())) {
            poll();
        }
        return new BigInteger(original.substring(start, cursor));
    }

    private Sign parseSign() throws Exception {
        if (hasNext()) {
            char tmp = original.charAt(cursor);
            if (tmp == '+') {
                poll();
                return Sign.ADD;
            } else if (tmp == '-') {
                poll();
                return Sign.SUB;
            } else {
                throw new Exception("Expected Operation Signs instead of"
                        + peek());
            }
        }
        throw new Exception("Unexpected Reaching of parseSign!");
    }

    private void parseSpace() {
        while (cursor < original.length() && isSpace(original.charAt(cursor))) {
            poll();
        }
    }

    private boolean isSpace(char a) {
        return a == '\t' || a == ' ';
    }

    private boolean isSign(char a) {
        return a == '+' || a == '-';
    }

    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    public Expression getExpression() {
        return expression;
    }

}
