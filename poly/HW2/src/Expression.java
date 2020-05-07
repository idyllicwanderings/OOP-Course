import Derivative;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Objects;

public class Expression implements Derivative, Cloneable, Comparable<Expression> {

    private HashMap<SimpTerm, BigInteger> simpExpr = new HashMap<>();
    private int min;
    private final int queueSize = 12;
    private final BigInteger two = new BigInteger("2");
    private int depth = 1;
    private PriorityQueue<Expression> queue = new PriorityQueue<>(queueSize);
    private Expression simp;

    public Expression() {
        ;
    }

    public Expression(Term... terms) {
        for (Term term : terms) {
            add(term);
        }
    }

    public Expression(HashMap<SimpTerm, BigInteger> simpExpr) {
        this.simpExpr = simpExpr;
    }

    public void simplify(long startTime) {
        min = toString().length();
        queue.add(this);
        HashSet<Expression> used = new HashSet<>();
        used.add(this);
        while (!queue.isEmpty() &&
                (System.currentTimeMillis() - startTime) <= 1000
                && depth <= 500) {
            Expression now = queue.poll();
            for (SimpTerm term : now.simpExpr.keySet()) {
                BigInteger a = term.getPowrIdx();
                BigInteger b = term.getSinIdx();
                BigInteger c = term.getCosIdx();

                SimpTerm r1 = new SimpTerm(a, b, c.subtract(two));
                reduce(used, now, term, r1, 1);
                reduce(used, now, term, r1, 2);

                SimpTerm r2 = new SimpTerm(a, b.subtract(two), c);
                reduce(used, now, term, r2, 3);
                reduce(used, now, term, r2, 4);

                SimpTerm r0 = new SimpTerm(a, b.add(two), c.subtract(two));
                reduce(used, now, term, r0, 5);
                reduce(used, now, term, r0, 6);
            }

        }
    }

    private void reduce(HashSet<Expression> used,
                        Expression now, SimpTerm cnow, SimpTerm cnxt, int mode) {
        if (!now.simpExpr.keySet().contains(cnxt)) {
            return;
        }
        Expression result = reduce(now,
                new Term(now.simpExpr.get(cnow), cnow.getIdx()),
                new Term(now.simpExpr.get(cnxt), cnxt.getIdx()), mode);
        assert result != null;
        if (!used.contains(result)) {
            int tmp = result.toString().length();
            if (tmp < min) {
                min = tmp;
                this.simp = result;
            }
            used.add(result);
            queue.add(result);
            depth++;
        }

    }

    private Expression reduce(Expression now, Term a, Term b, int mode) {
        Expression ret = now.clone();
        Term nxtA = new Term();
        Term nxtB = new Term();
        BigInteger coefA = a.getCoef();
        BigInteger coefB = b.getCoef();
        switch (mode) {
            /*
             * treats
             * coef1 a b c
             * coef2 a b c-2
             */
            case 1:
                nxtA = new Term(coefA.add(coefB), b.getIdx());
                nxtB = new Term(coefA.negate(), b.getPowrIdx(),
                        b.getSinIdx().add(two), b.getCosIdx());
                break;
            case 2:
                nxtA = new Term(coefA.add(coefB), a.getIdx());
                nxtB = new Term(coefB, b.getPowrIdx(),
                        b.getSinIdx().add(new BigInteger("2")), b.getCosIdx());
                break;
            /*
             * treats
             * coef1 a b c
             * coef2 a b-2 c
             */

            case 3:
                nxtA = new Term(coefA.add(coefB), b.getIdx());
                nxtB = new Term(coefA.negate(), b.getPowrIdx(),
                        b.getSinIdx(), b.getCosIdx().add(two));
                break;
            case 4:
                nxtA = new Term(coefA.add(coefB), a.getIdx());
                nxtB = new Term(coefB, b.getPowrIdx(),
                        b.getSinIdx(), b.getCosIdx().add(two));
                break;

            /*
             * treats
             * coef1 a b c
             * coef2 a b+2 c-2
             */
            case 5:
                nxtA = new Term(coefA, a.getPowrIdx(), a.getSinIdx(), b.getCosIdx());
                nxtB = new Term(coefB.subtract(coefA), b.getIdx());
                break;
            case 6:
                nxtA = new Term(coefA.subtract(coefB), a.getIdx());
                nxtB = new Term(coefB, a.getPowrIdx(), a.getSinIdx(), b.getCosIdx());
                break;
            default:
                break;
        }
        ret.extract(a);
        ret.extract(b);
        ret.add(nxtA);
        ret.add(nxtB);
        //System.err.println(now.simpExpr.size());
        return ret;
    }

    public void add(Term term) {
        add(new SimpTerm(term.getIdx()), term.getCoef());
    }

    public void add(Expression expr) {
        assert expr instanceof Expression;
        for (SimpTerm term : expr.simpExpr.keySet()) {
            add(term, expr.simpExpr.get(term));
        }
    }

    public void add(SimpTerm term, BigInteger integer) {
        if (integer.equals(BigInteger.ZERO)) {
            return;
        }
        if (simpExpr.containsKey(term)) {
            simpExpr.put(term, simpExpr.get(term).add(integer));
        } else {
            simpExpr.put(term, integer);
        }
    }

    @Override
    public boolean isZero() {
        return simpExpr.size() == 0;
    }

    @Override
    public Expression derive() {
        Expression ret = new Expression();
        for (SimpTerm term : simpExpr.keySet()) {
            ret.add(new Term(
                    simpExpr.get(term),
                    term.getIdx()).derive());
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder posHead = new StringBuilder();
        StringBuilder ret = new StringBuilder();
        boolean isFirstPos = true;
        for (SimpTerm simpterm : simpExpr.keySet()) {

            BigInteger c = simpExpr.get(simpterm);
            Term term = new Term(c, simpterm.getIdx());
            if (c.equals(BigInteger.ZERO)) {
                continue;
            }
            if (c.signum() > 0 && isFirstPos) {
                posHead.append(term.toString());
                isFirstPos = false;
            } else {
                if (c.signum() < 0) {
                    ret.append("-");
                } else {
                    ret.append("+");
                }
                ret.append(term.toString());
            }
        }
        if (posHead.length() + ret.length() == 0) {
            ret.append("0");
        }
        return posHead.toString() + ret.toString();
    }

    @Override
    public Expression clone() {
        //注意HashMap是浅拷贝。
        HashMap<SimpTerm, BigInteger> newExpr = new HashMap<>();
        for (SimpTerm term : this.simpExpr.keySet()) {
            newExpr.put(term, this.simpExpr.get(term));
        }
        return new Expression(newExpr);
    }

    private void extract(Term term) {
        simpExpr.remove(new SimpTerm(term.getIdx()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(simpExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Expression) {
            Expression expression = (Expression) obj;
            return simpExpr.equals(expression.simpExpr);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Expression o) {
        return this.toString().length() - o.toString().length();
    }
}
