package fr.plb.whatsapp.conversation.resource;

import fr.plb.whatsapp.conversation.dto.conversation.ConversationDTO;
import fr.plb.whatsapp.conversation.dto.conversation.ConversationToCreateDTO;
import fr.plb.whatsapp.conversation.exception.ConversationAlreadyExist;
import fr.plb.whatsapp.conversation.exception.ConversationNotFoundException;
import fr.plb.whatsapp.conversation.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConversationResource {

    private final ConversationService conversationService;

    public ConversationResource(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    Flux<ConversationDTO> getAll() {
        return conversationService.findAll().map(ConversationDTO::from);
    }

    @PostMapping
    Mono<ResponseEntity<ConversationDTO>> create(@RequestBody
                                                  ConversationToCreateDTO conversationToCreateDTO) {
        return conversationService.create(conversationToCreateDTO)
                .map(conversation -> ResponseEntity.ok(ConversationDTO.from(conversation)))
                .onErrorReturn(ConversationAlreadyExist.class,
                        ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST
                                , "Conversation already exist")).build());
    }

    @DeleteMapping
    Mono<ResponseEntity<String>> delete(@RequestParam String publicId) {
        return conversationService.delete(publicId)
                .map(nbDeleted -> {
                    if (nbDeleted == 0) {
                        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST
                                , "Not allowed to delete conversation");
                        return ResponseEntity.of(problemDetail).build();
                    } else {
                        return ResponseEntity.ok(publicId);
                    }
                });
    }

    @GetMapping("/get-one-by-public-id")
    Mono<ResponseEntity<ConversationDTO>> getOneByPublicId(@RequestParam String conversationId) {
        return conversationService.findOne(conversationId)
                .map(conversation -> ResponseEntity.ok(ConversationDTO.from(conversation)))
                .onErrorReturn(ConversationNotFoundException.class,
                        ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST
                                , "Not able to find this conversation")).build()
                );
    }
}
