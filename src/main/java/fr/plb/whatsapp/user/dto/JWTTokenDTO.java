package fr.plb.whatsapp.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JWTTokenDTO(@JsonProperty("id_token") String idToken) {
}
