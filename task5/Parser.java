public class Parser {

    private static final String legalSet = "[a-z],.\n ";
    private static final String rege = "[a-zA-Z]+(?:-[a-zA-Z]+)?";
    private String line;
    private int cursor = 0;

    public Parser(String line) throws Exception {
        this.line = line;
        checkLegal();
        //parseExpr();
    }

    private void checkLegal() throws Exception {
        if (!line.matches(legalSet)) {
            throw new Exception("Illegal characters!");
        }
    }

}
