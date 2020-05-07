public enum Function {

    X("x"), SIN("sin"), COS("cos");

    private String str;

    Function(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

}
