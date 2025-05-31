package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Constructor for dependency injection in tests (useful for mock DAOs)
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Registers a new account after performing validation.
     * @param account The account object to register (username and password).
     * @return The registered Account with its generated ID, or null if validation fails or registration fails.
     */
    public Account registerAccount(Account account) {
        // 1. Validation: username not blank
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return null;
        }

        // 2. Validation: password at least 4 characters long
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }

        // 3. Validation: An Account with that username does not already exist
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null; // Username already taken
        }

        // If all validations pass, attempt to register
        return accountDAO.registerAccount(account);
    }

/**
     * Verifies user login credentials.
     * @param account The account object containing username and password for login attempt.
     * @return The Account object if login is successful (credentials match), or null if login fails.
     */
    public Account loginAccount(Account account) {
        // Basic validation: username and password should not be blank/null
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().isEmpty()) {
            return null; // Invalid input
        }
        
        // Attempt to retrieve the account with matching username and password
        return accountDAO.getAccountByUsernameAndPassword(account.getUsername(), account.getPassword());
    }

    /**
     * Checks if an account with the given ID exists.
     * @param accountId The ID of the account to check.
     * @return true if the account exists, false otherwise.
     */
    public boolean doesAccountExist(int accountId) {
        return accountDAO.getAccountById(accountId) != null;
    }
}