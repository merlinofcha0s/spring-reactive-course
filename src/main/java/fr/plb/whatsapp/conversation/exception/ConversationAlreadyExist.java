package fr.plb.whatsapp.conversation.exception;

public class ConversationAlreadyExist extends RuntimeException {
    public ConversationAlreadyExist(String message) {
        super(message);
    }
}
