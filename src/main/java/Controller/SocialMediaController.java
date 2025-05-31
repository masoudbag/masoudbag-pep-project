package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import Model.Account; // Import Account model
import Service.AccountService; // Import AccountService
import Model.Message; // Import Message model
import Service.MessageService; // Import MessageService



/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private AccountService accountService; // Declare service
    private MessageService messageService; // Declare message service

    public SocialMediaController() {
        this.accountService = new AccountService(); // Initialize service in constructor
        this.messageService = new MessageService(); // Initialize message service
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        // --- Requirement 1: Register New User ---
        app.post("/register", ctx -> registerAccountHandler(ctx));

        // --- Requirement 2: User Login ---
        app.post("/login", ctx -> loginAccountHandler(ctx));

        // --- Requirement 3: Create New Message ---
        app.post("/messages", ctx -> createMessageHandler(ctx));

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
}    

