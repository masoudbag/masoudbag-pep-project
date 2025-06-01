// DAO/AccountDAO.java
package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class AccountDAO {

    /**
     * Registers a new account by inserting it into the database.
     * @param account The account object to be inserted. The account_id will be populated upon successful insertion.
     * @return The Account object with its generated ID if successful, null otherwise.
     */
    public Account registerAccount(Account account) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                account.setAccount_id(rs.getInt(1));
            }
            return account;

        } catch (SQLException e) {
            // Log the exception for debugging purposes
            System.err.println("SQL Exception during account registration: " + e.getMessage());
            return null; // Return null if there's a database error (e.g., duplicate username)
        }
    }

    /**
     * Retrieves an account by its username.
     * @param username The username to search for.
     * @return The Account object if found, null otherwise.
     */
    public Account getAccountByUsername(String username) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("SQL Exception while getting account by username: " + e.getMessage());
            return null;
        }
    }

/**
     * Retrieves an account by its username and password.
     * @param username The username for login.
     * @param password The password for login.
     * @return The Account object if credentials match, null otherwise.
     */
    public Account getAccountByUsernameAndPassword(String username, String password) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
            return null; 
        } catch (SQLException e) {
            System.err.println("SQL Exception while getting account by username and password: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves an account by its ID to verify if a user exists.
     * @param accountId The ID of the account.
     * @return The Account object if found, null otherwise.
     */
    public Account getAccountById(int accountId) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("SQL Exception while getting account by ID: " + e.getMessage());
            return null;
        }
    }
}