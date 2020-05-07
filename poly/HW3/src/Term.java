import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Objects;
import java.util.Vector;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

public class Term extends AbstractCollection<Derivative> implements Derivative, Comparable<Term> {

    private Factor coef = new Factor(BigInteger.ONE);
    private Factor pwr = new Factor(BigInteger.ZERO, new Power());
    private HashSet<Factor> triSet = new HashSet<>();
    private Vector<Expression> exprSet = new Vector<>();

    public Term(Derivative... derivative) {
        for (Derivative derivative1 : derivative) {
            put(derivative1.clone());
        }
    }

    public Term(Factor coef, Factor pwr, HashSet<Factor> triSet,
                Vector<Expression> exprSet) {
        this.coef = coef.clone();
        this.pwr = pwr.clone();
        for (Factor derivative1 : triSet) {
            put(derivative1.clone());
        }
        for (Expression de : exprSet) {
            put(de.clone());
        }
    }

    @Override
    public Term clone() {
        return new Term(toArray(new Derivative[0]));
    }

    @Override
    public boolean isZero() {
        return coef.isZero();
    }

    @Override
    public Expression derive() {
        Expression re = new Expression();
        if (!pwr.canAbsAbbr()) {
            Term term = divide(pwr);
            Derivative de = pwr.derive();
            Derivative derivative = term.mult(de);
            re.add(derivative);
            //System.out.println(derivative.toString());
        }
        for (Factor factor : triSet) {
            //System.out.println(divide(factor));
            Derivative derivative = divide(factor).mult(factor.derive());
            re.add(derivative);
            //System.out.println(derivative.toString());
        }
        for (Expression expression:exprSet) {
            Derivative derivative = divide(expression).mult(expression.derive());
            re.add(derivative);
            //System.out.println(derivative.toString());
        }
        return re;
    }

    public Term divide(Derivative derivative) {
        Term ret = this.clone();
        if (ret.exprSet.contains(derivative)) {
            ret.exprSet.remove((Expression) derivative);
        } else if (ret.triSet.contains(derivative)) {
            ret.triSet.remove((Factor) derivative);
        } else if (derivative instanceof Factor
                && ((Factor) derivative).getFuncType().equals(Function.CONST)) {
            ret.coef = new Factor(BigInteger.ONE);
        } else if (derivative instanceof Factor
                && ((Factor) derivative).getFuncType().equals(Function.X)) {
            ret.pwr = new Factor(BigInteger.ZERO, new Power());
        } else {
            throw new ClassCastException("basicFunc Type detected!");
        }
        return ret;
    }

    @Override
    public Term mult(Derivative derivative) {
        if (derivative.isZero()) {
            this.coef = new Factor(new BigInteger("0"));
            return this;
        }
        if (derivative instanceof Factor) {
            return mult((Factor) derivative);
        } else if (derivative instanceof Term) {
            return mult((Term) derivative);
        } else if (derivative instanceof Expression) {
            if (((Expression) derivative).isTerm()) {
                Expression expression = (Expression)derivative;
                return mult(expression.firstTerm());
            }
            for (Derivative derivative1 : ((Expression) derivative).simplify()) {
                if (derivative1 instanceof Factor) {
                    mult((Factor) derivative1);
                } else if (derivative1 instanceof Term) {
                    mult((Term) derivative1);
                } else {
                    //assert !exprSet.contains((Expression)derivative1);
                    exprSet.add((Expression) derivative1);
                }
            }
        } else {
            throw new ClassCastException("Expected Class Term or Factor in mult(), Term.java!");
        }
        return this;
    }

    private Term mult(Factor factor) {
        if (factor.isZero()) {
            coef = new Factor(BigInteger.ZERO);
            return this;
        }

        if (factor.getFuncType().equals(Function.X)) {
            pwr = new Factor(pwr.getIdx().add(factor.getIdx()),
                    new Power());
        } else if (factor.getFuncType().equals(Function.SIN) ||
                factor.getFuncType().equals(Function.COS)) {
            Factor tmp;
            Optional<Factor> optionalTerm = triSet.stream().filter(factor::isMergable).findAny();
            if (optionalTerm.isPresent()) {
                triSet.remove(optionalTerm.get());
                tmp = (Factor) optionalTerm.get().mult(factor);
                triSet.add(tmp);
            }
            else {
                triSet.add(factor);
            }
        } else if (factor.getFuncType().equals(Function.CONST)) {
            coef = new Factor((Const) coef.getFunc().mult(factor.getFunc()));
        } else {
            ;
        }
        return this;
    }

    private Term mult(Term term) {
        if (term.isZero()) {
            coef = new Factor(BigInteger.ZERO);
            return this;
        }
        coef = new Factor((Const) coef.getFunc().mult(term.coef.getFunc()));
        pwr = new Factor(pwr.getIdx().add(term.pwr.getIdx()),
                new Power());
        for (Factor tri : term.triSet) {
            mult(tri);
        }
        for (Expression expression : term.exprSet) {
            mult(expression);
        }
        return this;
    }

    public Term add(Term term) {
        assert isMergable(term);
        Const one = (Const) this.coef.getFunc();
        Const another = (Const) term.coef.getFunc();
        this.coef = new Factor(one.getCoef().add(another.getCoef()));
        return this;
    }

    public void put(Derivative derivative) {
        if (derivative instanceof Factor) {
            Factor factor = (Factor) derivative;
            if (factor.getFuncType().equals(Function.CONST)) {
                mult(factor);
            } else if (factor.getFuncType().equals(Function.X)) {
                this.pwr = factor;
            }
            else {
                mult(factor);
            }
        } else if (derivative instanceof Expression) {
            mult(derivative);
        } else if (derivative instanceof Term) {
            mult(derivative);
        } else {
            throw new ClassCastException(
                    "Expected Const/factor/expression/term in add(),term.java!");
        }
    }

    public Term negate() {
        Const one = (Const) coef.getFunc();
        this.coef = new Factor(new Const(one.getCoef().negate()));
        return this;
    }

    @Override
    public boolean canAbbr() {
        return coef.canAbbr() && pwr.canAbbr()
                && triSet.size() == 0 && exprSet.size() == 0;
    }

    private boolean isConst() {
        return pwr.canAbbr() && triSet.size() == 0
                && exprSet.size() == 0;
    }

    @Override
    public Iterator<Derivative> iterator() {
        ArrayList<Derivative> ret = new ArrayList<>(exprSet);
        ret.addAll(triSet);
        if (!pwr.canAbbr()) {
            ret.add(pwr);
        }
        if (!coef.canAbbr()) {
            ret.add(coef);
        }
        return ret.iterator();
    }

    @Override
    public int size() {
        int ret = 0;
        if (!pwr.canAbbr()) {
            ret++;
        }
        if (!coef.canAbbr()) {
            ret++;
        }
        return ret + triSet.size() + exprSet.size();
    }

    @Override
    public String toString() {
        /*Warning: output the ABS of coef!*/
        StringBuilder str = new StringBuilder();

        if (coef.isZero()) {
            return "";
        }

        if (isConst()) {
            if (!coef.canAbsAbbr()) {
                return coef.toString();
            }
            else {
                return coef.toString() + "1";
            }
        }

        boolean isAbbr = coef.canAbsAbbr();

        str.append(coef.toString());

        if (!pwr.canAbbr()) {
            if (!isAbbr) {
                str.append("*");
            }
            str.append(pwr.toString());
            isAbbr = false;
        }

        for (Factor factor : triSet) {
            if (factor.canAbbr()) {
                continue;
            }
            if (!isAbbr) {
                str.append("*");
            }
            str.append(factor.toString());
            isAbbr = false;
        }

        for (Expression expression : exprSet) {
            if (expression.canAbbr()) {
                continue;
            }
            if (!isAbbr) {
                str.append("*");
            }
            if (expression.isTerm()) {
                str.append(expression.toString());
            } else {
                str.append(expression.setNested(!expression.isTerm()).toString());
            }
            isAbbr = false;
        }
        return str.toString();
    }

    public boolean isStandard() {
        return coef.equals(new Const("1")) && pwr.isStandard()
                && triSet.size() == 0 && exprSet.size() == 0;
    }

    public boolean isFactor() {
        int num = 0;
        Const one = (Const) coef.getFunc();
        //const
        if (isZero() || isConst()) {
            return true;
        }
        //pwr
        if (one.isStd() && triSet.size() + exprSet.size() == 0) {
            return true;
        }
        if (one.isStd() && pwr.canAbbr() &&
                triSet.size() + exprSet.size() == 1) {
            return true;
        }
        return false;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Term) {
            Term term = (Term) obj;
            return this.coef.equals(term.coef) && isMergable(term);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.coef, this.pwr, this.triSet, this.exprSet);
    }

    @Override
    public int compareTo(Term o) {
        return this.toString().length() - o.toString().length();
    }

    public Vector<Derivative> getFactors() {
        Vector<Derivative> ret = new Vector<>();
        ret.addAll(triSet);
        ret.addAll(exprSet);
        if (!coef.canAbbr()) {
            ret.add(coef);
        }
        if (!pwr.canAbbr()) {
            ret.add(pwr);
        }
        return ret;
    }

    public boolean hasFactor(Derivative derivative) {
        return coef.equals(derivative) || pwr.equals(derivative)
                || triSet.contains(derivative) || exprSet.contains(derivative);
    }

    public int signum() {
        Const onew = (Const) coef.getFunc();
        return onew.getCoef().signum();
    }

    public boolean isMergable(Term term) {
        return pwr.equals(term.pwr)
                && triSet.equals(term.triSet) && exprSet.equals(term.exprSet);
    }

}
