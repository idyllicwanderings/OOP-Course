public enum Function {

    X("x"), SIN("sin"), COS("cos"),CONST("const");

    private String str;

    Function(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

}
