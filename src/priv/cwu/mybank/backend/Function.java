package priv.cwu.mybank.backend;

/**
 * priv.cwu.mybank.backend.Function - Practice for Enum
 * This class includes all functionalities this program can realize.
 *
 * @author Colin Wu
 * @version 7/4/21
 */
public enum Function {
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    TRANSFER("Transfer"),
    SHOW("Show accounts");

    private final String func;

    Function(String func) {
        this.func = func;
    }

    public String getFunc() {
        return func;
    }

    @Override
    public String toString() {
        return "priv.cwu.mybank.backend.Function{" +
                "func='" + func + '\'' +
                '}';
    }
}
