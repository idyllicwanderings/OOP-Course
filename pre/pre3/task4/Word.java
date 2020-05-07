public class Word implements Comparable<Word> {

    private String word;

    public Word(String word) {
        this.word = word;
        //this.word = reverse(word);
    }

    private String reverse(String str) {
        StringBuilder ret = new StringBuilder();
        ret.append(str);
        ret = ret.reverse();
        if (ret.toString().compareTo(str) > 0) {
            return str;
        }
        return ret.toString();
    }

    @Override
    public int compareTo(Word o) {
        return word.compareTo(o.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Word && ((Word) obj).word.equals(word);
    }

    @Override
    public String toString() {
        return word;
    }
}
