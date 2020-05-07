package parser;

import expression.Expression;
import expression.PolyItem;
import java.math.BigInteger;

public class InputParser {

    private Expression expression;
    private String original;
    private Integer cursor;
    private static final String legal = "[\\d+\\-* \\tx]+";
    private static final String integer = "[+-]?\\d";
    private static int length;

    private enum Sign {
        ADD, SUB
    }

    public InputParser(String input) throws Exception {
        expression = new Expression();
        cursor = 0;
        this.original = input;
        length = this.original.length();
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
        if (cursor >= length) {
            throw new Exception("Out of index!");
        }
        return original.charAt(cursor);
    }

    private void parseExpression() throws Exception {
        Expression expression = new Expression();
        parseSpace();
        Sign curSign = Sign.ADD;
        if (isSign(peek())) {
            curSign = parseSign();
        }
        parseSpace();
        while (hasNext()) {
            parseTerm(curSign);
            if (hasNext()) {
                curSign = parseSign();
            }
            parseSpace();
        }
    }

    private void parseTerm(Sign sign) throws Exception {
        /*
         * @term: factor {* factor}
         * @1: +/- powerfunction; simplified powerfunction; signed integer.
         * @2: [\d] powerfunction; unsigned integer.
         * @3:x powerfunction.
         */

        BigInteger tmp = BigInteger.ONE;
        if (hasNext() && isSign(peek())) {
            Sign signTwo = parseSign();
            if (!sign.equals(signTwo)) {
                tmp = tmp.negate();
            }
            if (isDigit(peek())) {
                tmp = parseSignedInteger();
                if (!sign.equals(signTwo)) {
                    tmp = tmp.negate();
                }
                parseSpace();
                if (hasNext() && peek() == '*') {
                    cursor++;
                    parseSpace();
                    parsePowerFunction(tmp);
                }
            }
            else {
                parseSpace();
                parsePowerFunction(tmp);
            }
        }
        else if (hasNext() && isDigit(peek())) {
            tmp = parseSignedInteger();
            if (sign.equals(Sign.SUB)) {
                tmp = tmp.negate();
            }
            parseSpace();
            if (hasNext() && peek() == '*') {
                cursor++;
                parseSpace();
                parsePowerFunction(tmp);
            }
            else {
                PolyItem tmpPoly = new PolyItem(tmp, BigInteger.ZERO);
                expression.addExpr(tmpPoly);
            }
        } else {
            if (sign.equals(Sign.SUB)) {
                tmp = tmp.negate();
            }
            parseSpace();
            parsePowerFunction(tmp);
        }
        parseSpace();

    }

    private BigInteger parsePowerFunction(BigInteger coef) throws Exception {
        parseSpace();
        BigInteger idx = BigInteger.ONE;
        if (hasNext()) {
            if (hasNext() && peek() == 'x') {
                cursor++;
            }
            else {
                PolyItem tmp = new PolyItem(coef, BigInteger.ZERO);
                expression.addExpr(tmp);
                return BigInteger.ZERO;
            }
            parseSpace();
            if (hasNext() && peek() == '*') {
                parsePowerSign();
                parseSpace();
                idx = parseSignedInteger();
            }
            //System.out.println(coef +  "    "+idx);
            PolyItem tmp = new PolyItem(coef, idx);
            expression.addExpr(tmp);
            parseSpace();
            return idx;
        }
        else {
            PolyItem tmp = new PolyItem(coef, BigInteger.ZERO);
            expression.addExpr(tmp);
            parseSpace();
            return BigInteger.ZERO;
        }
    }

    private BigInteger parseSignedInteger() throws Exception {
        int start;
        start = cursor;
        if (isSign(peek())) {
            cursor++;
        }
        if (!isDigit(peek())) {
            throw new Exception("Expected Digital Number in Signed Number!:" + peek());
        }
        while (hasNext() && isDigit(peek())) {
            cursor++;
        }
        return new BigInteger(original.substring(start, cursor));
    }

    private Sign parseSign() throws Exception {
        if (hasNext()) {
            char tmp = original.charAt(cursor);
            if (tmp == '+') {
                cursor++;
                return Sign.ADD;
            } else if (tmp == '-') {
                cursor++;
                return Sign.SUB;
            } else {
                throw new Exception("Expected Operation Signs instead of"
                        + peek());
            }
        }
        throw new Exception("Unexpected Reaching of parseSign!");
    }

    private void parseSpace() throws Exception {
        while (hasNext() && isSpace(original.charAt(cursor))) {
            cursor++;
        }
    }

    private void parsePowerSign() throws Exception {
        while (hasNext() && peek() == '*') {
            cursor++;
        }
    }

    private void selfAssert(char ch) throws Exception {
        if (peek() != ch) {
            throw new Exception("Expected \"" + ch + "\" rather than \"" + peek() + "\"!");
        }
        if (hasNext()) {
            cursor++;
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
