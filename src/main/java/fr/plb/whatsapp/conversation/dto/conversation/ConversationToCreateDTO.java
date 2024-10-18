package fr.plb.whatsapp.conversation.dto.conversation;

import org.jilt.Builder;

import java.util.Set;

@Builder
public record ConversationToCreateDTO(Set<String> members, String name) {
}
