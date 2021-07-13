package priv.cwu.mybank.backend;

import priv.cwu.mybank.backend.Account;

/**
 * priv.cwu.mybank.backend.Manager
 * The instance of this class is the maintainer/supervisor of this system.
 *
 * @author Colin Wu
 */
public class Manager extends Person {
    public Manager() {
        super();
    }

    public Manager(String name) {
        super(name);
    }

    public void check(Account account) {
        if (account.accNum % 2 == 0) {
            account.setLocked(false);
        }
    }

    @Override
    public int generateId() {
        return 0;
    }
}
