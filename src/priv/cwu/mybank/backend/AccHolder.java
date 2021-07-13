package priv.cwu.mybank.backend;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * priv.cwu.mybank.backend.AccHolder
 * Actions taken in this class should be thread-safe since a person can access his/her account from multiple
 * ends (e.g. mobile end, in-person, ATM, etc.) at the same time.
 *
 * Realized functionalities:
 * 1. Deposit
 * 2. Withdraw
 * 3. Transfer
 * 4. Show current accounts
 *
 * @author Colin Wu
 * @version 7/1/21
 */
public class AccHolder extends Person {
    private final int MAX_ACCOUNT = 100000;  // Maximal number of accounts a person can have
    private List<Account> accounts;  // List of accounts belonged to the holder

    private final ReadWriteLock readWriteLock;

    public AccHolder(String name) {
        super(name);
        this.accounts = new CopyOnWriteArrayList<>();  // Thread-safe list

        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Opens a new account for this account holder.
     * @return true if the action was successful; false if the action failed.
     */
    public synchronized boolean openAccount() {
        if (accounts.size() >= MAX_ACCOUNT) {
            System.out.println("You can't have more than " + MAX_ACCOUNT + " accounts");
            return false;
        }
        accounts.add(new Account(this));
        System.out.println("Successfully opened an account for you.");
        return true;
    }

    /**
     * Deletes the specified account from both holder's and bank's record
     * @param accNum account number
     * @return true if the action was successful; false if the action failed.
     */
    public synchronized boolean deleteAccount(int accNum) {
        // Step 1: Remove the account under the name of this account holder
        boolean isFound = false;
        for (int i = 0; i < accounts.size(); i++) {
            Account cur = accounts.get(i);
            if (cur.accNum == accNum) {
                isFound = true;

                // Protection mechanism
//                if (cur.money != 0) {
//                    System.out.println("Please clear your account before deleting it");
//                    return;
//                }

                accounts.remove(i);
                break;
            }
        }

        // If the account can't be found, return false
        if (!isFound) {
            System.out.println("The account number is invalid...");
            return false;
        }

        // Step 2: Remove the account from the account list of this bank
        Bank.accounts.removeIf(account -> account.accNum == accNum);
        System.out.println("Successfully removed your account.");
        return true;
    }

    /**
     * Deposits money to the account
     * @param accNum account number
     * @param money amount of money
     */
    public synchronized void deposit(int accNum, int money) {
        Account target = isValid(accNum);
        if (target == null) {
            System.out.println("The account number is invalid. Deposit aborted!");
            return;
        }
        System.out.println("Start depositing money to your account " + accNum);
        target.deposit(money);
        System.out.println("Deposit to your account " + accNum + " finished!");
    }

    /**
     * Withdrow money from the account
     * @param accNum account number
     * @param money amount of money
     * @return true if the withdraw was successful; false if the withdraw failed
     */
    public synchronized boolean withdraw(int accNum, int money) {
        Account target = isValid(accNum);
        if (target == null) {
            System.out.println("The account number is invalid. Withdraw aborted!");
            return false;
        }
        System.out.println("Start withdrawing money to your account " + accNum);
        if (target.withdraw(money)) {
            System.out.println("Withdraw from your account " + accNum + " finished!");
            return true;
        } else
            return false;
    }

    /**
     * Transfers money from one account to another
     * @param accFrom account where the money is withdrawn
     * @param accTo account where the money is going to be sent
     * @param money amount of money
     */
    public synchronized void transfer(int accFrom, int accTo, int money) {
        TempAccount tempAccount = new TempAccount();

        // Creates two threads to finish the work
        // The withdrawing thread will notify the depositing thread as it finishes
        new Thread(() -> {
            try {
                tempAccount.getMoney(accFrom, money);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                tempAccount.giveMoney(accTo, money);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Prints out the amount of money this account has. Provides functionality for users to check their money.
     * @param accNum account number
     */
    public void checkMoney(int accNum) {
        Account target = isValid(accNum);
        if (target == null) {
            System.out.println("The account number is invalid...");
            return;
        }
        readWriteLock.readLock().lock();
        try {
            System.out.println("Your account balance is " + target.money);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Show the corresponding account numbers this person holds. Simply prints out a line on the console
     */
    public void showAccount() {
        if (accounts.size() == 0)
            System.out.println("You don't have any account yet.");
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append(", you have ").append(accounts.size()).append(" accounts. Your accounts are as followed:");
            for (Account acc : accounts)
                sb.append(" ").append(acc.accNum);
            System.out.println(sb);
        }
    }

    /**
     * Checks if the accNum is a valid account number of this account holder.
     * TODO: this method can be improved in the future by implementing a more suitable data structure
     * @param accNum provided account number
     * @return The priv.cwu.mybank.backend.Account instance if it exists; null if it doesn't exist.
     */
    public Account isValid(int accNum) {
        Account target = null;
        for (Account acc : accounts) {
            if (acc.accNum == accNum)
                target = acc;
        }
        return target;
    }

    @Override
    public int generateId() {
        //TODO
        return 0;
    }

    // Getters and Setters

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * TempAccount
     * The temporary account class created for transfer feature
     */
    class TempAccount {
        private int temp;
        private Lock lock = new ReentrantLock();
        private Condition cond = lock.newCondition();

        /**
         * Gets money from the specified account
         * @param accNumFrom the account where money is going to be withdrawn
         * @param money amount money to transfer
         * @return true if the money is withdrawn from the account
         * @throws InterruptedException
         */
        public boolean getMoney(int accNumFrom, int money) throws InterruptedException {
            lock.lock();
            try {
                Account from = isValid(accNumFrom);
                if (from == null) {
                    System.out.println("You provided invalid information...");
                    return false;
                }
                if (withdraw(accNumFrom, money)) {
                    temp += money;
                    cond.signal();
                    return true;
                } else {
                    System.out.println("Withdraw failed!");
                    return false;
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * Puts money to anther account
         * @param accNumTo the account where the money is going to be sent
         * @param money amount of money to transfer
         * @return true if the money is sent to the account
         * @throws InterruptedException
         */
        public boolean giveMoney(int accNumTo, int money) throws InterruptedException {
            lock.lock();
            try {
                while (temp == 0) {
                    if (!cond.await(5, TimeUnit.SECONDS)) {
                        System.out.println("Timed out...");
                        return false;
                    }
                }
                Account to = isValid(accNumTo);
                if (to == null) {
                    System.out.println("You provided invalid information...");
                    return false;
                }
                deposit(accNumTo, money);
                return true;
            } finally {
                lock.unlock();
            }
        }
    }
}