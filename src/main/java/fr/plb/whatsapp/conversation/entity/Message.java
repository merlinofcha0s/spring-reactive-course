package fr.plb.whatsapp.conversation.entity;

import fr.plb.whatsapp.user.entity.User;
import org.jilt.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.util.Objects;

@Document("message")
@Builder
public class Message extends AbstractAuditingEntity<String> {

    @Id
    @Field(value = "id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field("send_time")
    private Instant sendTime;

    @Field("text")
    private String text;

    @Field("type")
    private MessageType type;

    @Field("send_state")
    private MessageSendState sendState;

    @Field("file")
    private byte[] file;

    @Field("file_content_type")
    private String fileContentType;

    @Field("sender")
    private User sender;

    private Conversation conversation;

    public Message() {
    }

    public Message(String id, Instant sendTime, String text, MessageType type, MessageSendState sendState,
                   byte[] file, String fileContentType, User sender, Conversation conversation) {
        this.id = id;
        this.sendTime = sendTime;
        this.text = text;
        this.type = type;
        this.sendState = sendState;
        this.file = file;
        this.fileContentType = fileContentType;
        this.sender = sender;
        this.conversation = conversation;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getSendTime() {
        return sendTime;
    }

    public void setSendTime(Instant sendTime) {
        this.sendTime = sendTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageSendState getSendState() {
        return sendState;
    }

    public void setSendState(MessageSendState sendState) {
        this.sendState = sendState;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message that = (Message) o;
        return Objects.equals(sendTime, that.sendTime) && Objects.equals(text, that.text) && type == that.type && sendState == that.sendState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sendTime, text, type, sendState);
    }
}
