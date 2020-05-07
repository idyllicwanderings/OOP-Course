public enum Type {
    A("A"),B("B"),C("C"),D("D"),E("E");

    private String presentation;

    Type(String presentation) {
        this.presentation = presentation;
    }

    public String getPresentation() {
        return presentation;
    }
}
