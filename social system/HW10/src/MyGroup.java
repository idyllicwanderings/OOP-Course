import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Person;

import java.math.BigInteger;
import java.util.HashMap;

public class MyGroup implements Group {

    private int id;
    private HashMap<Integer, Person> people;
    private int totalAges;
    private BigInteger conflictSum;
    private int relationSum;
    private int valueSum;
    private int squareAges;

    public MyGroup(int id) {
        this.id = id;
        people = new HashMap<>();
        conflictSum = BigInteger.ZERO;
        totalAges = 0;
        squareAges = 0;
        valueSum = 0;
        relationSum = 0;
    }

    public /*@pure@*/ int getId() {
        return id;
    }

    public /*@pure@*/ boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Group)) {
            return false;
        }
        return this.id == ((Group) obj).getId();
    }

    public void addPerson(Person person) {
        relationSum++;
        people.put(person.getId(), person);
        int age = person.getAge();
        totalAges += age;
        squareAges += age * age;
        if (size() == 0) {
            conflictSum = person.getCharacter();
        } else {
            conflictSum = conflictSum.xor(person.getCharacter());
        }
    }

    public /*@pure@*/ boolean hasPerson(Person person) {
        return people.containsKey(person.getId());
    }

    public /*@pure@*/ int getRelationSum() {
        return relationSum;
    }

    public /*@pure@*/ int getValueSum() {
        return valueSum;
    }

    public /*@pure@*/ BigInteger getConflictSum() {
        return conflictSum;
    }

    public /*@pure@*/ int getAgeMean() {
        if (size() == 0) {
            return 0;
        }
        return totalAges / size();
    }

    public /*@pure@*/ int getAgeVar() {
        if (size() == 0) {
            return 0;
        }
        int mean = getAgeMean();
        int n = size();
        return (squareAges - 2 * mean * totalAges + n * mean * mean) / n;
    }

    public void checkRelation(Person p1, Person p2, int value) {
        relationSum += 2;
        valueSum += value * 2;
    }

    public void checkPerson(Person person) {
        for (Person person1 : people.values()) {
            if (person1.isLinked(person) && person1.getId() != person.getId()) {
                relationSum += 2;
                valueSum += person1.queryValue(person) * 2;
            }
        }
    }

    public int size() {
        return people.size();
    }

}
