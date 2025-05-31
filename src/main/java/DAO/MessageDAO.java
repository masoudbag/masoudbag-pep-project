package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList; // Import ArrayList
import java.util.List;     // Import List

public class MessageDAO {

    /**
     * Creates a new message by inserting it into the database.
     * @param message The message object to be inserted. The message_id will be populated upon successful insertion.
     * @return The Message object with its generated ID if successful, null otherwise.
     */
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
            System.err.println("SQL Exception during message creation: " + e.getMessage());
            return null;
        }
    }

/**
     * Retrieves all messages from the database.
     * @return A list of all messages, or an empty list if none exist.
     */
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
            System.err.println("SQL Exception while getting all messages: " + e.getMessage());
        }
        return messages;
    }

    /**
     * Retrieves a message by its unique ID.
     * @param messageId The ID of the message to retrieve.
     * @return The Message object if found, null otherwise.
     */
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
            return null; // No message with that ID found
        } catch (SQLException e) {
            System.err.println("SQL Exception while getting message by ID: " + e.getMessage());
            return null;
        }
    }
}    