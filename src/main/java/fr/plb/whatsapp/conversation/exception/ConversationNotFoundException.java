package fr.plb.whatsapp.conversation.exception;

public class ConversationNotFoundException extends RuntimeException {
  public ConversationNotFoundException(String message) {
    super(message);
  }
}
