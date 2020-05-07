import java.math.BigInteger;

public class InputParser {

    private Expression expression;
    private String original;
    private Integer cursor;
    private static final String legal = "[\\d+\\-* \\txcosin()]+";
    private boolean isConformSimp = true;
    private boolean hasSign = false;

    private enum Sign {
        ADD, SUB
    }

    public InputParser(String input) throws Exception {
        expression = new Expression();
        cursor = 0;
        this.original = input;
        checkLegal(original);
        parseExpression();
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

    private void parseExpression() throws Exception {
        /* @expression: [+/-] term {+/- term }*/
        Expression expression = new Expression();
        parseSpace();
        Sign curSign = Sign.ADD;
        if (isSign(peek())) {
            curSign = parseSign();
        }
        parseSpace();
        while (hasNext()) {
            parseTerm(curSign);
            parseSpace();
            if (hasNext()) {
                curSign = parseSign();
            }
            parseSpace();
        }
    }

    private void parseTerm(Sign sign) throws Exception {
        //* @term: firstfactor {restfactor}
        Factor factor = parseFirstFactor();
        parseSpace();
        while (hasNext() && peek() == '*') {
            selfAssert('*');
            parseSpace();
            factor.multiply(parseFactor());
            parseSpace();
        }
        if (sign.equals(Sign.SUB)) {
            factor.negate();
        }
        expression.add(factor.toTerm());
    }

    private Factor parseFirstFactor() throws Exception {
        // FirstFactor : [+/-] x/cos(x)/sin(x)/int [^ int]
        Sign sign = Sign.ADD;
        isConformSimp = true;
        hasSign = false;
        if (isSign(peek())) {
            sign = parseSign();
            parseSpace();
            hasSign = true;
        }
        if (sign.equals(Sign.SUB)) {
            return parseFactor().negate();
        }
        return parseFactor();
    }

    private Factor parseFactor() throws Exception {
        Factor factor = new Factor();
        parseSpace();
        BigInteger integer = BigInteger.ONE;
        if (isDigit(peek()) || isSign(peek())) {
            factor.setCoef(parseSignedInteger());
            parseSpace();
            return factor;
        } else {
            BasicFunction function = parseFunction();
            parseSpace();
            factor.setIdx(function.getFunctionType(), function.getIdx());
            return factor;
        }
    }

    private BasicFunction parseFunction() throws Exception {
        //function: x/cos(x)/sin(x)/[^ int]
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

    private Power parsePowerFunction() throws Exception {
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
        return new Power(Function.X, integer);
    }

    private void checkOutOfIndex(BigInteger bigInteger) throws Exception {
        if (bigInteger.abs().compareTo(new BigInteger("10000")) > 0) {
            throw new Exception("Index Out of 10000 Bound!");
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
            throw new Exception("Expected \"" + ch + "\" rather than \"" + peek() + "\"!");
        }
        poll();
    }

    private Tri parseTriFunction(String func) throws Exception {
        parseSpace();
        selfAssert('(');
        parseSpace();
        selfAssert('x');
        parseSpace();
        selfAssert(')');
        parseSpace();
        BigInteger integer = BigInteger.ONE;
        if (hasNext() && getSignIdenfer().equals("**")) {
            cursor++;
            cursor++;
            parseSpace();
            integer = parseSignedInteger();
            parseSpace();
        } else {
            ;
        }
        return new Tri(Function.valueOf(func.toUpperCase()), integer);
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
