package fr.plb.whatsapp.conversation.dto.message;

import fr.plb.whatsapp.conversation.entity.MessageType;
import org.jilt.Builder;

@Builder
public record MessageContentDTO(String text,
                                MessageType type,
                                MessageMediaContentDTO media) {
}
