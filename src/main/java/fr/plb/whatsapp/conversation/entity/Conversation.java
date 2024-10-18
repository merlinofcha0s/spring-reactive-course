package fr.plb.whatsapp.conversation.entity;

import fr.plb.whatsapp.user.entity.User;
import org.jilt.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document("conversation")
@Builder
public class Conversation extends AbstractAuditingEntity<String> {

    @Id
    @Field(value = "id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field("name")
    private String name;

    private Set<Message> messages = new HashSet<>();

    private Set<User> users = new HashSet<>();

    public Conversation(String id, String name, Set<Message> messages, Set<User> users) {
        this.id = id;
        this.name = name;
        this.messages = messages;
        this.users = users;
    }

    public Conversation() {
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
