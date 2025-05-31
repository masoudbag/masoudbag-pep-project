package Service;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {

    private MessageDAO messageDAO;
    private AccountService accountService; // We need this to validate posted_by

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
     * Creates a new message after performing validation.
     * @param message The message object to create.
     * @return The created Message with its generated ID, or null if validation fails or creation fails.
     */
    public Message createMessage(Message message) {
        // 1. Validation: message_text is not blank
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            return null;
        }

        // 2. Validation: message_text is not over 255 characters
        if (message.getMessage_text().length() > 255) {
            return null;
        }

        // 3. Validation: posted_by refers to a real, existing user
        // We need AccountService to check if the user exists
        if (!accountService.doesAccountExist(message.getPosted_by())) {
            return null; // User does not exist
        }

        return messageDAO.createMessage(message);
    }
}