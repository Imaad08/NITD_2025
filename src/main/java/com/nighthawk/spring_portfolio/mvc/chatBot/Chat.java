package com.nighthawk.spring_portfolio.mvc.chatBot;

import java.sql.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

/**
 * Chat class is a model for the chat messages that are stored in the database.
 * 
 * This class implements the following interfaces:
 * - Serializable: Ensures that Chat instances can be serialized, review comments at end of file for more information.
 * - Comparable<Chat>: Allows Chat instances to be compared based on a specific field, useful for sorting.
 * 
 * Sample object creation, for message, response, timestamp, and personId:
 * {@code Chat chat = new Chat("Hello", "Hi there!", new Date(System.currentTimeMillis()), 1L);}
 * 
 * Required methods for Chat History feature (many provided automatically by Lombok annotations):
 * - getId()
 * - getPersonId()
 * - getChatMessage()
 * - getChatResponse()
 * - getTimestamp()
 * - toJSON() (implemented with Jackson ObjectMapper)
 * - toString() (provided by @ToString annotation)
 * 
 * Use Lombok annotations for boilerplate code like getters, setters, and toString method.
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Chat implements Serializable, Comparable<Chat> {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String chatMessage;
    private String chatResponse;
    private Date timestamp;
    private Long personId;

	// Custom constructor without id
	public Chat(String chatMessage, String chatResponse, Date timestamp, Long personId) {
		this.chatMessage = chatMessage;
		this.chatResponse = chatResponse;
		this.timestamp = timestamp;
		this.personId = personId;
	}

    // Implement the compareTo method for Comparable interface
    @Override
    public int compareTo(Chat other) {
        return this.timestamp.compareTo(other.getTimestamp());
    }

    // Custom toJSON method using Jackson ObjectMapper
    public String toJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

/*
// Searlizable use case allows instances of the class to be converted into a byte stream, 
// which can then be reverted back into a copy of the object. 
//
// This is useful for storing objects in files or sending them over the network.

import java.io.*;

public class ChatSerializationExample {
    public static void main(String[] args) {
        // Create a Chat object
        Chat chat = new Chat("Hello", "Hi there!", new java.sql.Date(System.currentTimeMillis()), 1L);

        // Serialize the Chat object to a file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chat.ser"))) {
            oos.writeObject(chat);
            System.out.println("Chat object serialized to file chat.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Deserialize the Chat object from the file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("chat.ser"))) {
            Chat deserializedChat = (Chat) ois.readObject();
            System.out.println("Chat object deserialized from file chat.ser");
            System.out.println(deserializedChat);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
 
 */