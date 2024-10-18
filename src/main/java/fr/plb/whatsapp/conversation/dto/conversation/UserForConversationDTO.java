package fr.plb.whatsapp.conversation.dto.conversation;

import fr.plb.whatsapp.user.entity.User;
import org.jilt.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Builder
public record UserForConversationDTO(String lastName, String firstName,
                                     String publicId, String imageUrl,
                                     Instant lastSeen) {

    public static UserForConversationDTO from(User user) {
        UserForConversationDTOBuilder userForConversationBuilder = UserForConversationDTOBuilder.userForConversationDTO();

        if (user.getImageUrl() != null) {
            userForConversationBuilder.imageUrl(user.getImageUrl());
        }

        return userForConversationBuilder
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .publicId(user.getId())
                .lastSeen(user.getLastSeen())
                .build();
    }

    public static List<UserForConversationDTO> from(Set<User> users) {
        return users.stream().map(UserForConversationDTO::from).toList();
    }
}
