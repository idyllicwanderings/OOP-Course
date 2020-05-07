import Derivative;
import java.util.HashSet;
import java.util.Vector;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public class Expression implements Derivative, Cloneable, Comparable<Expression> {

    private HashSet<Term> poly = new HashSet<>();
    private boolean isNested = false;
    private Term commonFactor = new Term();
    private boolean isExtracted = false;

    public Expression() {
        ;
    }

    public Expression(Term... terms) {
        for (Term term : terms) {
            add(term.clone());
        }
    }

    public Expression(HashSet<Term> terms) {
        this.poly = terms;
    }

    public Expression simplify(long startTime) {
        //wrong version 2.0
        Expression ret = clone();
        if (System.currentTimeMillis() - startTime >= 1000) {
            return this;
        }
        Vector<Derivative> cmnDerive = ret.getCmn();
        for (Derivative derivative:cmnDerive) {
            ret.commonFactor = ret.commonFactor.mult(derivative);
        }
        for (Term term:ret.poly) {
            term = term.divide(ret.commonFactor);
        }
        if (ret.toString().length() < toString().length()) {
            ret.isExtracted = true;
            return ret;
        }
        isExtracted = false;
        return this;
        //Vector<Derivative> cmnDerive = getCmn();
    }

    public HashSet<Derivative> simplify() {
        Queue<Expression> queue = new PriorityQueue<>();
        HashSet<Derivative> ret = new HashSet<>();
        Expression expr = clone();
        queue.add(expr);
        while (!queue.isEmpty()) {
            Expression expr1 = queue.poll();
            Vector<Derivative> cmnDerive = expr1.getCmn();
            for (Derivative deri:cmnDerive) {
                if (deri instanceof Factor) {
                    ret.add(deri);
                    expr1 = expr1.divide(deri);
                }
                else if (deri instanceof Expression) {
                    if (((Expression) deri).isTerm()) {
                        ret.add(((Expression) deri).poly.stream().findFirst().get());
                    }
                    else {
                        queue.add((Expression)deri);
                    }
                    expr1 = expr1.divide(deri);
                }
                else {
                    throw new ClassCastException("?");
                }
            }
            //.out.println(expr1);
            ret.add(expr1.setNested(true));
        }
        return ret;
    }

    public Term firstTerm() {
        assert isTerm();
        return poly.stream().findFirst().get();
    }

    private Expression divide(Derivative derivative) {
        Expression ret = new Expression();
        for (Term term:poly) {
            ret.add(term.divide(derivative));
        }
        return ret;
    }

    private Vector<Derivative> getCmn() {
        Optional<Term> first = poly.stream().findFirst();
        Vector<Derivative> ret = new Vector<>();
        if (!first.isPresent()) {
            return ret;
        }
        Term longest = first.get();
        for (Derivative derivative:longest.getFactors()) {
            if (poly.stream()
                    .allMatch(term1 -> term1.hasFactor(derivative))) {
                if (!derivative.canAbbr()) {
                    ret.add(derivative);
                }
            }
        }
        return ret;
    }

    public Expression negate() {
        for (Term term:poly) {
            term = term.negate();
        }
        return this;
    }

    public boolean isFactor() {
        return isTerm() && firstTerm().isFactor();
    }

    @Override
    public boolean canAbbr() {
        if (poly.size() == 1) {
            for (Term term:poly) {
                return term.canAbbr();
            }
        }
        return false;
    }

    public boolean isStandard() {
        if (poly.size() == 1) {
            for (Term term:poly) {
                return term.isStandard();
            }
        }
        return false;
    }

    public void add(Derivative derivative) {
        if (derivative instanceof Term) {
            add((Term)derivative);
        }
        else if (derivative instanceof Expression) {
            add((Expression)derivative);
        }
        else {
            throw new ClassCastException("Expected Term/Expression in add,Expression.java!");
        }
    }

    public void add(Term term) {
        if (term.isZero()) {
            return;
        }
        Optional<Term> optionalTerm = poly.stream().filter(term::isMergable).findAny();
        if (optionalTerm.isPresent()) {
            Term tmp = optionalTerm.get();
            poly.remove(tmp);
            poly.add(tmp.add(term));
        } else {
            poly.add(term);
        }
    }

    public void add(Expression expr) {
        for (Term term:expr.poly) {
            add(term);
        }
    }

    public Expression setNested(boolean set) {
        isNested = set;
        return this;
    }

    public boolean isTerm() {
        return poly.size() == 1;
    }

    @Override
    public boolean isZero() {
        return poly.size() == 0;
    }

    @Override
    public Expression derive() {
        Expression ret = new Expression();
        for (Term term:poly) {
            ret.add(term.derive());
        }
        return ret;
    }

    @Override
    public Derivative mult(Derivative derivative) {
        if (derivative instanceof Term) {
            return mult((Term)derivative);
        }
        else if (derivative instanceof Expression) {
            return mult((Expression)derivative);
        }
        else {
            throw new ClassCastException(
                    "Expected Class Term or Expression in mult(), Expression.java!");
        }
    }

    private Expression mult(Term term) {
        Expression ret = new Expression();
        for (Term term1:poly) {
            ret.add((Term) term1.mult(term));
        }
        return this;
    }

    private Expression mult(Expression expression) {
        Expression ret = new Expression();
        for (Term term:expression.poly) {
            ret.add((Expression)mult(term));
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder posHead = new StringBuilder();
        StringBuilder ret = new StringBuilder();
        if (isExtracted) {
            posHead.append("(");
        }
        boolean isFirstPos = true;
        for (Term term : poly) {

            if (term.isZero()) {
                continue;
            }

            if (term.signum() > 0 && isFirstPos) {
                posHead.append(term.toString());
                isFirstPos = false;
            } else {
                ret.append(term.toString());
            }
        }
        if (posHead.length() + ret.length() == 0) {
            ret.append("0");
        }
        if (posHead.length() > 0 && posHead.charAt(0) == '+') {
            posHead.deleteCharAt(0);
        }
        else if (posHead.length() == 0 && ret.charAt(0) == '+') {
            ret.deleteCharAt(0);
        }
        if (isNested) {
            posHead.insert(0,"(");
            ret.append(")");
        }
        if (isExtracted) {
            ret.append("(");
            if (commonFactor.signum() < 0) {
                ret.append("-");
            }
            ret.append(commonFactor.toString());
        }
        return posHead.toString() + ret.toString();
    }

    @Override
    public Expression clone() {
        //注意HashMap是浅拷贝。
        HashSet<Term> newExpr = new HashSet<>();
        for (Term term:this.poly) {
            newExpr.add(term.clone());
        }
        return new Expression(newExpr).setNested(isNested);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(poly);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Expression) {
            Expression expression = (Expression) obj;
            return poly.equals(((Expression) obj).poly);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Expression o) {
        return this.toString().length() - o.toString().length();
    }

}
