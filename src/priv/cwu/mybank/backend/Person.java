package priv.cwu.mybank.backend;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * priv.cwu.mybank.backend.Person
 * This class defines general features of classes related to people.
 *
 * @author Colin Wu
 * @version 7/5/21
 */
public abstract class Person {
    private int id;
    private String name;
    private Lock lock;

    public Person() {
        id = generateId();
        this.name = "default";
        lock = new ReentrantLock();
    }

    public Person(String name) {
        id = generateId();
        this.name = name;
    }

    public abstract int generateId();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }
}
