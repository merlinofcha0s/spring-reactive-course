package fr.plb.whatsapp.user.entity;

import fr.plb.whatsapp.conversation.entity.AbstractAuditingEntity;
import org.jilt.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(value = "whatsapp_user")
@Builder
public class User extends AbstractAuditingEntity<String> {

    @Id
    @Field(value = "id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field("last_name")
    private String lastName;

    @Field("first_name")
    private String firstName;

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("image_url")
    private String imageUrl;

    @Field("last_seen")
    private Instant lastSeen = Instant.now();

    @Field("authorities")
    private Set<Authority> authorities = new HashSet<>();

    public User(String id, String lastName, String firstName,
                String email, String password, String imageUrl,
                Instant lastSeen, Set<Authority> authorities) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.lastSeen = lastSeen;
        this.authorities = authorities;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

//    public Set<ConversationEntity> getConversations() {
//        return conversations;
//    }
//
//    public void setConversations(Set<ConversationEntity> conversations) {
//        this.conversations = conversations;
//    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName) && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, firstName, email, imageUrl, lastSeen);
    }
}
