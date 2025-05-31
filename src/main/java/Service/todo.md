You will need to design and create your own Service classes from scratch.
You should refer to prior mini-project lab examples and course material for guidance.
Completed Code

Here are the new and modified files:
DAO/AccountDAO.java

This class handles database operations for the Account table.
package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDAO {

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
            e.printStackTrace();
            return null;
        }
    }

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
            e.printStackTrace();
            return null;
        }
    }

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
            e.printStackTrace();
            return null;
        }
    }

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
            e.printStackTrace();
            return null;
        }
    }
}

DAO/MessageDAO.java

This class handles database operations for the Message table.

package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public Message createMessage(Message message) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                message.setMessage_id(rs.getInt(1));
            }
            return message;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM message";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Message getMessageById(int messageId) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message deleteMessage(int messageId) {
        Message deletedMessage = getMessageById(messageId); // Retrieve before deleting

        if (deletedMessage != null) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "DELETE FROM message WHERE message_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, messageId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    return deletedMessage;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // Return null if message didn't exist or deletion failed
    }

    public Message updateMessageText(int messageId, String newText) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newText);
            ps.setInt(2, messageId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return getMessageById(messageId); // Fetch the updated message
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}

Service/AccountService.java

This class contains the business logic for Account operations.
package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Constructor for dependency injection in tests
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Registers a new account.
     * @param account The account object to register.
     * @return The registered Account with its generated ID, or null if registration fails.
     */
    public Account registerAccount(Account account) {
        // Validation: username not blank, password at least 4 chars
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return null; // Invalid input
        }

        // Validation: username must not already exist
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null; // Username already taken
        }

        return accountDAO.registerAccount(account);
    }

    /**
     * Verifies user login.
     * @param account The account object containing username and password for login attempt.
     * @return The Account object if login is successful, or null if login fails.
     */
    public Account loginAccount(Account account) {
        // Validation: username and password should not be blank
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().isEmpty()) {
            return null; // Invalid input
        }
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

Service/MessageService.java

This class contains the business logic for Message operations.

package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {

    private MessageDAO messageDAO;
    private AccountService accountService; // To check if posted_by account exists

    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.accountService = new AccountService(); // Initialize AccountService
    }

    // Constructor for dependency injection in tests
    public MessageService(MessageDAO messageDAO, AccountService accountService) {
        this.messageDAO = messageDAO;
        this.accountService = accountService;
    }

    /**
     * Creates a new message.
     * @param message The message object to create.
     * @return The created Message with its generated ID, or null if creation fails.
     */
    public Message createMessage(Message message) {
        // Validation: message_text not blank, not over 255 chars
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty() ||
            message.getMessage_text().length() > 255) {
            return null; // Invalid message text
        }

        // Validation: posted_by refers to a real, existing user
        if (!accountService.doesAccountExist(message.getPosted_by())) {
            return null; // User does not exist
        }

        return messageDAO.createMessage(message);
    }

    /**
     * Retrieves all messages from the database.
     * @return A list of all messages, or an empty list if none exist.
     */
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    /**
     * Retrieves a message by its ID.
     * @param messageId The ID of the message to retrieve.
     * @return The Message object if found, or null if not found.
     */
    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    /**
     * Deletes a message by its ID.
     * @param messageId The ID of the message to delete.
     * @return The deleted Message object if it existed, or null if it didn't.
     */
    public Message deleteMessage(int messageId) {
        return messageDAO.deleteMessage(messageId);
    }

    /**
     * Updates the text of an existing message.
     * @param messageId The ID of the message to update.
     * @param newText The new text for the message.
     * @return The updated Message object, or null if update fails.
     */
    public Message updateMessage(int messageId, String newText) {
        // Validation: newText not blank, not over 255 chars
        if (newText == null || newText.trim().isEmpty() || newText.length() > 255) {
            return null; // Invalid message text
        }

        // Check if the message exists before attempting to update
        if (messageDAO.getMessageById(messageId) == null) {
            return null; // Message does not exist
        }

        return messageDAO.updateMessageText(messageId, newText);
    }

    /**
     * Retrieves all messages posted by a specific user.
     * @param accountId The ID of the user whose messages to retrieve.
     * @return A list of messages posted by the user, or an empty list if none exist.
     */
    public List<Message> getMessagesByAccountId(int accountId) {
        // Optional: You could add a check here to ensure the accountId actually exists
        // if (!accountService.doesAccountExist(accountId)) {
        //     return new ArrayList<>(); // Or throw an exception, depending on desired behavior
        // }
        return messageDAO.getMessagesByAccountId(accountId);
    }
}

Controller/SocialMediaController.java (MODIFIED)

This class now includes all the required API endpoints and uses the Service layer.

package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // 1. Register new User
        app.post("/register", this::registerAccountHandler);

        // 2. User login
        app.post("/login", this::loginAccountHandler);

        // 3. Create new message
        app.post("/messages", this::createMessageHandler);

        // 4. Retrieve all messages
        app.get("/messages", this::getAllMessagesHandler);

        // 5. Retrieve a message by its ID
        app.get("/messages/{message_id}", this::getMessageByIdHandler);

        // 6. Delete a message by its ID
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        // 7. Update a message text by its ID
        app.patch("/messages/{message_id}", this::updateMessageTextHandler);

        // 8. Retrieve all messages written by a particular user
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);

        return app;
    }

    // --- Account Handlers ---

    /**
     * Handler for POST /register
     * Registers a new account.
     * @param context Javalin Context
     */
    private void registerAccountHandler(Context context) {
        Account newAccount = context.bodyAsClass(Account.class);
        Account registeredAccount = accountService.registerAccount(newAccount);

        if (registeredAccount != null) {
            context.status(200); // OK
            context.json(registeredAccount);
        } else {
            context.status(400); // Client error
        }
    }

    /**
     * Handler for POST /login
     * Verifies user login.
     * @param context Javalin Context
     */
    private void loginAccountHandler(Context context) {
        Account loginAttempt = context.bodyAsClass(Account.class);
        Account loggedInAccount = accountService.loginAccount(loginAttempt);

        if (loggedInAccount != null) {
            context.status(200); // OK
            context.json(loggedInAccount);
        } else {
            context.status(401); // Unauthorized
        }
    }

    // --- Message Handlers ---

    /**
     * Handler for POST /messages
     * Creates a new message.
     * @param context Javalin Context
     */
    private void createMessageHandler(Context context) {
        Message newMessage = context.bodyAsClass(Message.class);
        Message createdMessage = messageService.createMessage(newMessage);

        if (createdMessage != null) {
            context.status(200); // OK
            context.json(createdMessage);
        } else {
            context.status(400); // Client error
        }
    }

    /**
     * Handler for GET /messages
     * Retrieves all messages.
     * @param context Javalin Context
     */
    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.status(200); // OK
        context.json(messages);
    }

    /**
     * Handler for GET /messages/{message_id}
     * Retrieves a message by its ID.
     * @param context Javalin Context
     */
    private void getMessageByIdHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);

        if (message != null) {
            context.status(200); // OK
            context.json(message);
        } else {
            context.status(200); // As per requirement, return empty if not found
            context.json(""); // Represents empty body
        }
    }

    /**
     * Handler for DELETE /messages/{message_id}
     * Deletes a message by its ID.
     * @param context Javalin Context
     */
    private void deleteMessageHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessage(messageId);

        context.status(200); // Always 200 for DELETE idempotency
        if (deletedMessage != null) {
            context.json(deletedMessage); // Return the deleted message if it existed
        } else {
            context.json(""); // Return empty body if message didn't exist
        }
    }

    /**
     * Handler for PATCH /messages/{message_id}
     * Updates a message's text.
     * @param context Javalin Context
     */
    private void updateMessageTextHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        // The request body contains only the new message_text.
        // We need to parse it as a simple string or a custom DTO
        // For simplicity, assuming body is just the string for message_text
        // Or if it's a JSON object like {"message_text": "new text"}
        // Let's assume it's a JSON object that contains the "message_text" field.
        
        // Define a simple class to parse the incoming JSON for message_text
        class MessageUpdateBody {
            public String message_text;
            public String getMessage_text() { return message_text; }
            public void setMessage_text(String message_text) { this.message_text = message_text; }
        }

        MessageUpdateBody updateBody = context.bodyAsClass(MessageUpdateBody.class);
        String newText = updateBody.getMessage_text();

        Message updatedMessage = messageService.updateMessage(messageId, newText);

        if (updatedMessage != null) {
            context.status(200); // OK
            context.json(updatedMessage);
        } else {
            context.status(400); // Client error (e.g., message not found, invalid text)
        }
    }

    /**
     * Handler for GET /accounts/{account_id}/messages
     * Retrieves all messages posted by a particular user.
     * @param context Javalin Context
     */
    private void getMessagesByAccountIdHandler(Context context) {
        int accountId = Integer.parseInt(context.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesByAccountId(accountId);

        context.status(200); // OK
        context.json(messages);
    }
}

