package fr.plb.whatsapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class ReactorConfiguration {
//    public ReactorConfiguration() {
//        Hooks.onOperatorDebug();
//    }
}
