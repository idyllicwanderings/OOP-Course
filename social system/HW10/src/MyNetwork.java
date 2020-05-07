import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.exceptions.EqualGroupIdException;
import com.oocourse.spec2.exceptions.GroupIdNotFoundException;
import com.oocourse.spec2.main.Group;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MyNetwork implements Network {

    private HashMap<Integer, Person> people;
    private HashMap<Integer, HashSet<Integer>> linkedNodes;
    private ArrayList<Integer> ids;
    //private HashMap<Integer, Integer> unionSet; //(id,element)
    private HashMap<Integer, Group> groups;

    private static final int pplSize = 5001;
    private static final int grpSize = 11;

    public MyNetwork() {
        people = new HashMap<>();
        //unionSet = new HashMap<>(pplSize);
        groups = new HashMap<>();
        ids = new ArrayList<>();
        linkedNodes = new HashMap<>();
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
        ids.add(id);
        linkedNodes.put(id,new HashSet<>());
        //unionSet.put(id, id);
    }

    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (!contains(id1) || !contains(id2)) {
            throw new PersonIdNotFoundException();
        }
        MyPerson p1 = (MyPerson) getPerson(id1);
        MyPerson p2 = (MyPerson) getPerson(id2);
        if (id1 == id2) {
            return;
        }
        if (p1.isLinked(p2) || p2.isLinked(p1)) {
            throw new EqualRelationException();
        }
        p1.addLink(p2, value);
        p2.addLink(p1, value);
        /*int u1 = unionSet.get(id1);
        int u2 = unionSet.get(id2);
        if (u1 != u2) {
            merge union set
            for (int p:unionSet.keySet()) {
                if (unionSet.get(p) == u1) {
                    unionSet.put(p,u2);
                }
            }
        }*/
        linkedNodes.get(id1).add(id2);
        linkedNodes.get(id2).add(id1);
        for (Group group : groups.values()) {
            if (group.hasPerson(p1) && group.hasPerson(p2)) {
                MyGroup myGroup = (MyGroup) group;
                myGroup.checkRelation(p1, p2, value);
            }
        }
    }

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
        for (Integer id2 : people.keySet()) {
            if (compareName(id, id2) > 0) {
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
        boolean res = false;
        LinkedList<Integer> queue = new LinkedList<>();
        HashMap<Integer, Boolean> visit = new HashMap<>();
        queue.push(id1);
        while (!queue.isEmpty()) {
            int x = queue.peekFirst();
            if (x == id2) {
                res = true;
                break;
            }
            queue.pollFirst();
            visit.put(x, true);
            for (int adj:linkedNodes.get(x)) {
                if (adj == id2) {
                    res = true;
                    break;
                }
                if (!visit.getOrDefault(adj,false)) {
                    queue.add(adj);
                }
            }
        }
        return res;
    }

    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) {
            throw new EqualGroupIdException();
        }
        groups.put(group.getId(), group);
    }

    public /*@pure@*/ Group getGroup(int id) {
        return groups.get(id);
    }

    public void addtoGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new GroupIdNotFoundException();
        }
        if (!contains(id1)) {
            throw new PersonIdNotFoundException();
        }
        Group group = getGroup(id2);
        Person person = getPerson(id1);
        if (group.hasPerson(person)) {
            throw new EqualPersonIdException();
        }
        MyGroup myGroup = (MyGroup) group;
        if (myGroup.size() >= 1111) {
            return;
        }
        group.addPerson(person);
        myGroup.checkPerson(person);
    }

    public /*@pure@*/ int queryGroupSum() {
        return groups.size();
    }

    public /*@pure@*/ int queryGroupPeopleSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new GroupIdNotFoundException();
        }
        MyGroup myGroup = (MyGroup) getGroup(id);
        return myGroup.size();
    }

    public /*@pure@*/ int queryGroupRelationSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new GroupIdNotFoundException();
        }
        return getGroup(id).getRelationSum();
    }

    public /*@pure@*/ int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new GroupIdNotFoundException();
        }
        return getGroup(id).getValueSum();
    }

    public /*@pure@*/ BigInteger queryGroupConflictSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new GroupIdNotFoundException();
        }
        return getGroup(id).getConflictSum();
    }

    public /*@pure@*/ int queryGroupAgeMean(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new GroupIdNotFoundException();
        }
        return getGroup(id).getAgeMean();
    }

    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new GroupIdNotFoundException();
        }
        return getGroup(id).getAgeVar();
    }

}
