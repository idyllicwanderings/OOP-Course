package expression;

import java.util.HashSet;
import java.util.Optional;
import java.util.TreeSet;

public class Expression {

    private HashSet<PolyItem> polys = new HashSet<>();
    private TreeSet<PolyItem> derivatives = new TreeSet<>();

    public Expression() {
        ;
    }

    public void addExpr(PolyItem term) {
        Optional<PolyItem> objectTerm = polys.stream().filter(term::equivalent).findAny();
        if (objectTerm.isPresent()) {
            polys.remove(objectTerm.get());
            polys.add(objectTerm.get().add(term));
        }
        else {
            //System.out.println(term.getCoef()+" "+term.getIdx());
            polys.add(term);
        }
    }

    public void getDerivative() {
        for (PolyItem curPoly : polys) {
            PolyItem item = curPoly.derive();
            Optional<PolyItem> objectTerm = derivatives.stream().filter(item::equivalent).findAny();
            if (objectTerm.isPresent()) {
                derivatives.remove(objectTerm.get());
                derivatives.add(objectTerm.get().add(item));
            } else {
                derivatives.add(item);
            }
        }
        System.out.println(toString());
    }

    public boolean checkLegal() {
        return polys.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        boolean first = true;
        for (PolyItem item : derivatives) {
            if (!item.isZero()) {
                if (!first && item.isPos()) {
                    out.append("+");
                }
                out.append(item.toString());
                first = false;
            }
        }
        if (out.length() == 0) {
            return "0";
        }
        return out.toString();
    }
}
