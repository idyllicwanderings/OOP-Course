import com.oocourse.spec1.main.Person;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class MyPerson implements Person {

    private int id;
    private String name;
    private BigInteger character;
    private int age;
    private HashMap<Integer,Person> acquaintance;
    private HashMap<Integer,Integer> value;

    public MyPerson(int id, String name, BigInteger character, int age) {
        this.id = id;
        this.name = name;
        this.character = character;
        this.age = age;
        this.acquaintance = new HashMap<>();
        this.value = new HashMap<>();
    }

    public /*@pure@*/ int getId() {
        return id;
    }

    public /*@pure@*/ String getName() {
        return name;
    }

    public /*@pure@*/ BigInteger getCharacter() {
        return character;
    }

    public /*@pure@*/ int getAge() {
        return age;
    }

    public /*@pure@*/ boolean equals(Object obj) {
        if (obj == null || !(obj instanceof  Person)) {
            return false;
        }
        return ((Person) obj).getId() == this.id;
    }

    public /*@pure@*/ boolean isLinked(Person person) {
        return acquaintance.containsKey(person.getId()) || person.getId() == this.id;
    }

    public /*@pure@*/ int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        }
        return 0;
    }

    public /*@pure@*/ int getAcquaintanceSum() {
        return acquaintance.size();
    }

    public /*@pure@*/ int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    public void addLink(Person person,int val) {
        int id = person.getId();
        acquaintance.put(id,person);
        value.put(id,val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
