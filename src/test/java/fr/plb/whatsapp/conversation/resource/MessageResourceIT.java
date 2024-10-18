package fr.plb.whatsapp.conversation.resource;

import fr.plb.whatsapp.IntegrationTest;
import fr.plb.whatsapp.conversation.dto.message.MessageContentDTO;
import fr.plb.whatsapp.conversation.dto.message.MessageContentDTOBuilder;
import fr.plb.whatsapp.conversation.dto.message.MessageSendNewDTO;
import fr.plb.whatsapp.conversation.dto.message.MessageSendNewDTOBuilder;
import fr.plb.whatsapp.conversation.entity.Conversation;
import fr.plb.whatsapp.conversation.entity.ConversationBuilder;
import fr.plb.whatsapp.conversation.entity.MessageType;
import fr.plb.whatsapp.conversation.repository.ConversationRepository;
import fr.plb.whatsapp.user.entity.User;
import fr.plb.whatsapp.user.entity.UserBuilder;
import fr.plb.whatsapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Set;

public class MessageResourceIT extends IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    private Conversation newConversation;

    private final static String DEFAULT_EMAIL = "johnathan.doe@example.com";
    private final static String DEFAULT_CONVERSATION_ID = "6711c7ffb1f87c7272091424";
    private final static String DEFAULT_USER_ID = "66ff988c8cb81d007e7609c9";

    @BeforeEach
    public void setUp() {
        conversationRepository.deleteAll().block();
        userRepository.deleteAll().block();

        User user1 = UserBuilder.user()
                .id(DEFAULT_USER_ID)
                .email(DEFAULT_EMAIL)
                .build();
        userRepository.save(user1).block();

        newConversation = ConversationBuilder.conversation()
                .id(DEFAULT_CONVERSATION_ID)
                .name("Test Conversation 1")
                .users(Set.of(user1))
                .build();
        conversationRepository.save(newConversation).block();
    }


    @Test
    @WithMockUser(username = DEFAULT_EMAIL)
    public void createAndSendMessage_ShouldWork() throws JsonProcessingException {
        MessageContentDTO content = MessageContentDTOBuilder.messageContentDTO()
                .text("Hello, this is a text message!")
                .media(null)
                .type(MessageType.TEXT)
                .build();

        MessageSendNewDTO sendNewMessageDTO = MessageSendNewDTOBuilder.messageSendNewDTO()
                .content(content)
                .conversationPublicId(newConversation.getId())
                .build();

        webTestClient.post()
                .uri("/api/messages/send")
                .bodyValue(objectMapper.writeValueAsString(sendNewMessageDTO))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.textContent").isEqualTo("Hello, this is a text message!")
                .jsonPath("$.type").isEqualTo("TEXT")
                .jsonPath("$.conversationId").isEqualTo(newConversation.getId());
    }
}
