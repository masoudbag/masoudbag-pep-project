package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import Model.Account; // Import Account model
import Service.AccountService; // Import AccountService
import Model.Message; // Import Message model
import Service.MessageService; // Import MessageService
import java.util.List; // Import List



/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    // Declaring Services
    private AccountService accountService; 
    private MessageService messageService; 

    public SocialMediaController() {

    // Initialize services
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
        app.get("example-endpoint", this::exampleHandler);

        // --- Register New User ---
        app.post("/register", ctx -> registerAccountHandler(ctx));

        // --- User Login ---
        app.post("/login", ctx -> loginAccountHandler(ctx));

        // --- Create New Message ---
        app.post("/messages", ctx -> createMessageHandler(ctx));

        // --- Retrieve All Messages ---
        app.get("/messages", ctx -> getAllMessagesHandler(ctx));

        // --- Retrieve a message by its ID ---
        app.get("/messages/{message_id}", ctx -> getMessageByIdHandler(ctx));

        // --- Delete a message by a message ID ---
        app.delete("/messages/{message_id}", ctx -> deleteMessageHandler(ctx));

        // --- Update a message by a message ID ---
        app.patch("/messages/{message_id}", ctx -> updateMessageTextHandler(ctx));

        // --- Retrieve all messages by a particular user ---
        app.get("/accounts/{account_id}/messages", ctx -> getMessagesByAccountIdHandler(ctx));


        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


/**
     * Handler for POST /register endpoint.
     * Processes new user registration.
     * @param context The Javalin Context object.
     */
    // THIS registerAccountHandler METHOD MUST BE DIRECTLY INSIDE THE SocialMediaController CLASS
    // (and not inside startAPI or any other method).
    private void registerAccountHandler(Context context) {
        Account newAccount = context.bodyAsClass(Account.class); // Parse JSON body into Account object
        Account registeredAccount = accountService.registerAccount(newAccount);

        if (registeredAccount != null) {
            context.status(200); // Default, but explicit
            context.json(registeredAccount); // Return the registered account with its ID
        } else {
            context.status(400); // Bad Request (client error)
        }
    }

    /**
     * Handler for POST /login endpoint.
     * Processes user login verification.
     * @param context The Javalin Context object.
     */
    private void loginAccountHandler(Context context) {
        Account loginAttempt = context.bodyAsClass(Account.class); // Parse JSON body into Account object
        Account loggedInAccount = accountService.loginAccount(loginAttempt);

        if (loggedInAccount != null) {
            context.status(200); // OK
            context.json(loggedInAccount); // Return the logged-in account
        } else {
            context.status(401); // Unauthorized
        }
    }

/**
     * Handler for POST /messages endpoint.
     * Processes the creation of new messages.
     * @param context The Javalin Context object.
     */
    private void createMessageHandler(Context context) {
        Message newMessage = context.bodyAsClass(Message.class); // Parse JSON body into Message object
        Message createdMessage = messageService.createMessage(newMessage);

        if (createdMessage != null) {
            context.status(200); // OK
            context.json(createdMessage); // Return the created message with its ID
        } else {
            context.status(400); // Client error (invalid message, or posted_by user doesn't exist)
        }
    }

    /**
     * Handler for GET /messages endpoint.
     * Retrieves all messages.
     * @param context The Javalin Context object.
     */
    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.status(200); // Always 200 OK
        context.json(messages); // Returns an empty list if no messages
    }

    /**
     * Handler for GET /messages/{message_id} endpoint.
     * Retrieves a message by its ID.
     * @param context The Javalin Context object.
     */
    private void getMessageByIdHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);

        if (message != null) {
            context.status(200); // OK
            context.json(message);
        } else {
            // As per requirement, if message does not exist, status is 200, body is empty
            context.status(200);
            context.json(""); // Represents an empty JSON response body
        }
    }

    /**
     * Handler for DELETE /messages/{message_id} endpoint.
     * Deletes a message identified by its ID.
     * @param context The Javalin Context object.
     */
    private void deleteMessageHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessage(messageId);

        context.status(200); // Always 200 OK as per requirement (idempotent DELETE)
        if (deletedMessage != null) {
            context.json(deletedMessage); // Return the deleted message if it existed
        } else {
            context.json(""); // Return empty body if message did not exist
        }
    }

    /**
     * Helper class to parse the JSON body for PATCH /messages/{message_id}
     * Assuming the body is like: `{"message_text": "new text"}`
     */
    private static class MessageUpdateBody {
        public String message_text;

        public String getMessage_text() {
            return message_text;
        }

        public void setMessage_text(String message_text) {
            this.message_text = message_text;
        }
    }

    /**
     * Handler for PATCH /messages/{message_id} endpoint.
     * Updates the text of an existing message.
     * @param context The Javalin Context object.
     */
    private void updateMessageTextHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        
        // Parse the request body into our helper class
        MessageUpdateBody updateBody = context.bodyAsClass(MessageUpdateBody.class);
        String newText = updateBody.getMessage_text();

        Message updatedMessage = messageService.updateMessage(messageId, newText);

        if (updatedMessage != null) {
            context.status(200); // OK
            context.json(updatedMessage); // Return the full updated message
        } else {
            context.status(400); // Client error (message not found, invalid text, etc.)
        }
    }

    /**
     * Handler for GET /accounts/{account_id}/messages endpoint.
     * Retrieves all messages posted by a particular user.
     * @param context The Javalin Context object.
     */
    private void getMessagesByAccountIdHandler(Context context) {
        int accountId = Integer.parseInt(context.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesByAccountId(accountId);

        context.status(200); // Always 200 OK
        context.json(messages); // Returns an empty list if no messages or user doesn't exist (as per current service logic)
    }
}

