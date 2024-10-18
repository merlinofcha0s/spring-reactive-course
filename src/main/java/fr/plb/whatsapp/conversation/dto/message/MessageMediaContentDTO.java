package fr.plb.whatsapp.conversation.dto.message;

public record MessageMediaContentDTO(byte[] file,
                                     String mimetype) {
}
