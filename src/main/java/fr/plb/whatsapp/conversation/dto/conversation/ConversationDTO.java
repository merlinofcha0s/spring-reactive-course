package fr.plb.whatsapp.conversation.dto.conversation;

import fr.plb.whatsapp.conversation.dto.message.MessageDTO;
import fr.plb.whatsapp.conversation.entity.Conversation;
import org.jilt.Builder;

import java.util.List;

@Builder
public record ConversationDTO(String publicId, String name,
                              List<UserForConversationDTO> members,
                              List<MessageDTO> messages) {

    public static ConversationDTO from(Conversation conversation) {
        ConversationDTOBuilder restConversationBuilder = ConversationDTOBuilder.conversationDTO()
                .name(conversation.getName())
                .publicId(conversation.getId())
                .members(UserForConversationDTO.from(conversation.getUsers()));

        if (conversation.getMessages() != null) {
            restConversationBuilder.messages(MessageDTO.from(conversation.getMessages()));
        }

        return restConversationBuilder.build();
    }

}
