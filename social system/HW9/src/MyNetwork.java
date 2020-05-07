import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

public class MyNetwork implements Network {

    private TreeMap<Integer, Person> people;
    private HashMap<Integer, Integer> unionSet; //(id,element)

    public MyNetwork() {
        people = new TreeMap<>();
        unionSet = new HashMap<>();
    }

    public /*@pure@*/ boolean contains(int id) {
        return people.containsKey(id);
    }

    public /*@pure@*/ Person getPerson(int id) {
        return people.getOrDefault(id, null);
    }

    public void addPerson(Person person) throws EqualPersonIdException {
        int id = person.getId();
        if (people.containsKey(id)) {
            throw new EqualPersonIdException();
        }
        people.put(id, person);
        unionSet.put(id, id);
    }

    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        MyPerson p1 = (MyPerson) getPerson(id1);
        MyPerson p2 = (MyPerson) getPerson(id2);
        if (id1 == id2) { return; }
        if (p1.isLinked(p2) || p2.isLinked(p1)) {
            throw new EqualRelationException();
        }
        p1.addLink(p2, value);
        p2.addLink(p1, value);
        int u1 = unionSet.get(id1);
        int u2 = unionSet.get(id2);
        if (u1 != u2) {
            //merge union set
            for (int p:unionSet.keySet()) {
                if (unionSet.get(p) == u1) {
                    unionSet.put(p,u2);
                }
            }
        }
    }

    /*@ public normal_behavior
      @ requires contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2));
      @ ensures \result == getPerson(id1).queryValue(getPerson(id2));
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id1) || !contains(id2);
      @ signals (RelationNotFoundException e) contains(id1) && contains(id2) &&
      @         !getPerson(id1).isLinked(getPerson(id2));
      @*/
    public /*@pure@*/ int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        Person p1 = (MyPerson) getPerson(id1);
        Person p2 = (MyPerson) getPerson(id2);
        if (!p1.isLinked(p2)) {
            throw new RelationNotFoundException();
        }
        return p1.queryValue(p2);
    }

    public /*@pure@*/ BigInteger queryConflict(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        return getPerson(id1).getCharacter().xor(getPerson(id2).getCharacter());
    }

    public /*@pure@*/ int queryAcquaintanceSum(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new PersonIdNotFoundException();
        }
        return getPerson(id).getAcquaintanceSum();
    }

    public /*@pure@*/ int compareAge(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        return getPerson(id1).getAge() - getPerson(id2).getAge();
    }

    public /*@pure@*/ int compareName(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        return getPerson(id1).getName().compareTo(getPerson(id2).getName());
    }

    public /*@pure@*/ int queryPeopleSum() {
        return people.size();
    }

    public /*@pure@*/ int queryNameRank(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new PersonIdNotFoundException();
        }
        int sum = 1;
        for (Integer id2:people.keySet()) {
            if (compareName(id,id2) > 0) {
                sum += 1;
            }
        }
        return sum;
    }

    public /*@pure@*/ boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        if (id1 == id2) {
            return true;
        }
        return unionSet.get(id1).equals(unionSet.get(id2));
    }

}
