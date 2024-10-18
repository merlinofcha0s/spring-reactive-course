package fr.plb.whatsapp.configuration.dbmigrations;

import fr.plb.whatsapp.configuration.security.AuthoritiesConstants;
import fr.plb.whatsapp.user.entity.Authority;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Creates the initial database setup.
 */
@ChangeUnit(id = "auth-initialization", order = "001", author = "plb")
public class InitialSetupMigration {

    private final MongoTemplate template;

    public InitialSetupMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        Authority userAuthority = createUserAuthority();
        template.save(userAuthority);
        Authority adminAuthority = createAdminAuthority();
        template.save(adminAuthority);
    }

    @RollbackExecution
    public void rollback() {
    }

    private Authority createAuthority(String authority) {
        Authority adminAuthority = new Authority();
        adminAuthority.setName(authority);
        return adminAuthority;
    }

    private Authority createAdminAuthority() {
        return createAuthority(AuthoritiesConstants.ADMIN);
    }

    private Authority createUserAuthority() {
        return createAuthority(AuthoritiesConstants.USER);
    }
}
