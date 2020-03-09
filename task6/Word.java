import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Word implements Comparable<Word> {

    private String word;
    private ArrayList<Type> typeList = new ArrayList();
    private String substringA = "a{2,3}b{2,4}a{2,4}c{2,3}";
    private String substringB = "a{2,3}(ba)*(bc){2,4}";
    private String substringC = "a{2,3}(ba)*(bc){2,4}";
    private String sufD = ".*b{1,2}a{1,2}c{0,3}";
    private String preD = "a{0,3}b{1,1000000}c{2,3}.*";
    private String subsequenceE = ".*a.*b.*b.*c.*b.*c.*c.*";

    public Word(String word) {
        this.word = word;
        matchType();
        //this.word = reverse(word);
    }

    private void matchType() {
        Pattern regex = Pattern.compile(substringA);
        Matcher mt = regex.matcher(word);
        if (mt.find()) {
            typeList.add(Type.A);
        }
        regex = Pattern.compile(substringB);
        mt = regex.matcher(word);
        if (mt.find()) {
            typeList.add(Type.B);
        }
        regex = Pattern.compile(substringC);
        mt = regex.matcher(word.toLowerCase());
        if (mt.find()) {
            typeList.add(Type.C);
        }
        if (Pattern.matches(sufD,word.toLowerCase()) && Pattern.matches(preD,word)) {
            typeList.add(Type.D);
        }
        if (Pattern.matches(subsequenceE,word)) {
            typeList.add(Type.E);
        }
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
        StringBuilder ret = new StringBuilder();
        ret.append(typeList.size());
        if (typeList.size() != 0) {
            ret.append(" ");
            for (Type type:typeList) {
                ret.append(type.toString());
            }
        }
        return ret.toString();
    }
}
