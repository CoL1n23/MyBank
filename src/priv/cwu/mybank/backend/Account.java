package priv.cwu.mybank.backend;

import priv.cwu.mybank.backend.AccHolder;

/**
 * priv.cwu.mybank.backend.Account
 * A person can deposit money in an account. A person can also have multiple accounts at the same time.
 *
 * @author Colin Wu
 * @version 6/29/21
 */
public class Account {
    int accNum;
    AccHolder accHolder;
    int money;
    private boolean isLocked;

    public Account(AccHolder accHolder) {
        this.accHolder = accHolder;
        accNum = Bank.accounts.size() + 1;
        Bank.accounts.add(this);
        isLocked = false;
    }

    /**
     * Deposits money to this account. Usually called by an priv.cwu.mybank.backend.Account instance.
     * Attention: Try not to call it alone since it's not thread-safe.
     * @param amount amount of money
     */
    public void deposit(int amount) {
        money += amount;
        System.out.println("Money deposit. Now your account has " + money);
    }

    /**
     * Withdraw money from this account. Usually called by an priv.cwu.mybank.backend.Account instance.
     * Attention: Try not to call it alone since it's not thread-safe.
     * @param amount amount of money
     */
    public boolean withdraw(int amount) {
        // TODO: Try using keyword 'synchronized' here

        if (amount > money) {
            System.out.println("You don't have enough money...");
            return false;
        }
        money -= amount;
        System.out.println("Money withdrew. Now your account has " + money);
        return true;
    }

    /**
     * Some actions may need manager's permission to proceed. This method will ask a manager to grant permission.
     * @throws InterruptedException
     */
    public synchronized void askForPermission() throws InterruptedException {
        setLocked(true);
        long startTime = System.currentTimeMillis();

        // To mimic asking a manager to check this transaction
        new Thread(() -> {
            Manager manager = new Manager();
            manager.check(this);
        }).start();

        while (isLocked) {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 5000) {
                System.out.println("Your account has been locked.");
                return;
            }
        }
        System.out.println("Permission granted!");
    }


    // Getters and Setters

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
