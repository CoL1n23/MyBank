package priv.cwu.mybank.backend;

import java.lang.annotation.*;
import java.util.*;

/**
 * priv.cwu.mybank.backend.Bank - priv.cwu.mybank.db.Test class
 *
 * @author Colin Wu
 */
public class Bank {
    public String name;
    public static List<Account> accounts;

    public Bank() {
        name = "mybank";
        accounts = Collections.synchronizedList(new ArrayList<>());  // Thread-safe list
    }

    public Bank(String name) {
        this.name = name;
        accounts = Collections.synchronizedList(new ArrayList<>());  // Thread-safe list
    }

    @MyAnnotation(10)
    public static void main(String[] args) throws Exception {
        Bank bank = new Bank();
        AccHolder me = new AccHolder("me");
        me.openAccount();
        me.openAccount();
        me.showAccount();
        me.isValid(2).askForPermission();

//
//        // Reflection test
//        Class ref = Class.forName("priv.cwu.mybank.backend.Bank");
//        Constructor constructor = ref.getDeclaredConstructor(String.class);
//        Object obj = constructor.newInstance("CitiBank");
//        System.out.println(obj instanceof priv.cwu.mybank.backend.Bank);
    }
}

/**
 * User-Defined Annotation
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation {
    int value() default -1;
}