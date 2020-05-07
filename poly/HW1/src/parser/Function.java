package parser;

public enum Function {

    X("x");

    Function(String str) {
        this.present = str;
    }

    public String getPresent() {
        return present;
    }

    private String present;

}
