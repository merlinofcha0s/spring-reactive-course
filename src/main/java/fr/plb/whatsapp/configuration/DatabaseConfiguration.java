package fr.plb.whatsapp.configuration;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableMongock
@EnableReactiveMongoRepositories(basePackages = "fr.plb.whatsapp")
public class DatabaseConfiguration {
}
