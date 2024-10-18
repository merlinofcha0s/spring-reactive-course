package fr.plb.whatsapp.conversation.dto.message;

import fr.plb.whatsapp.conversation.entity.Message;
import fr.plb.whatsapp.conversation.entity.MessageSendState;
import fr.plb.whatsapp.conversation.entity.MessageType;
import org.jilt.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Builder
public class MessageDTO {

    private String textContent;
    private Instant sendDate;
    private MessageSendState state;
    private String publicId;
    private String conversationId;
    private MessageType type;
    private byte[] mediaContent;
    private String mimeType;
    private String senderId;

    public MessageDTO() {
    }

    public MessageDTO(String textContent, Instant sendDate, MessageSendState state, String publicId, String conversationId,
                      MessageType type, byte[] mediaContent, String mimeType, String senderId) {
        this.textContent = textContent;
        this.sendDate = sendDate;
        this.state = state;
        this.publicId = publicId;
        this.conversationId = conversationId;
        this.type = type;
        this.mediaContent = mediaContent;
        this.mimeType = mimeType;
        this.senderId = senderId;
    }

    public static MessageDTO from(Message message) {
        MessageDTOBuilder restMessageBuilder = MessageDTOBuilder.messageDTO()
                .textContent(message.getText())
                .sendDate(message.getSendTime())
                .state(message.getSendState())
                .conversationId(message.getConversation().getId())
                .type(message.getType())
                .senderId(message.getSender().getId());

        if (message.getType()!= MessageType.TEXT) {
            restMessageBuilder.mediaContent(message.getFile())
                    .mimeType(message.getFileContentType());
        }

        return restMessageBuilder.build();
    }

    public static List<MessageDTO> from(Set<Message> messages) {
        return messages.stream().map(MessageDTO::from).toList();
    }

    public boolean hasMedia() {
        return !type.equals(MessageType.TEXT);
    }

    public void setMediaAttachment(byte[] file, String contentType) {
        this.mediaContent = file;
        this.mimeType = contentType;
    }

    public String getTextContent() {
        return textContent;
    }

    public Instant getSendDate() {
        return sendDate;
    }

    public MessageSendState getState() {
        return state;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public MessageType getType() {
        return type;
    }

    public byte[] getMediaContent() {
        return mediaContent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getSenderId() {
        return senderId;
    }
}
