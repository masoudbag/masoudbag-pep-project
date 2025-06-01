package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.List; // Import List

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
/**
     * Retrieves all messages.
     * @return A list of all messages.
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
     * @return The updated Message object, or null if update fails (e.g., message not found, invalid text).
     */
    public Message updateMessage(int messageId, String newText) {
        // Validation: new message_text is not blank
        if (newText == null || newText.trim().isEmpty()) {
            return null;
        }
        // Validation: new message_text is not over 255 characters
        if (newText.length() > 255) {
            return null;
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
        // While not strictly required by the prompt's negative case (empty list is expected),
        // you *could* add a check here if you wanted to differentiate between "user doesn't exist"
        // and "user exists but has no messages". For now, we follow the prompt directly.
        // if (!accountService.doesAccountExist(accountId)) {
        //     return new ArrayList<>(); // Or throw an exception, or return null based on stricter requirements
        // }
        return messageDAO.getMessagesByAccountId(accountId);
    }
}
