package fr.plb.whatsapp.conversation.dto.message;


import org.jilt.Builder;

@Builder
public record MessageSendNewDTO(MessageContentDTO content,
                                String conversationPublicId) {
}
