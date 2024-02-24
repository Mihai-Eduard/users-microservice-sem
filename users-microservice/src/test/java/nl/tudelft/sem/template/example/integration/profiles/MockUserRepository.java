package nl.tudelft.sem.template.example.integration.profiles;

import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockUserRepository")
@Configuration
public class MockUserRepository {

    @Bean
    @Primary
    public UserRepository getMockUserRepository() {
        return Mockito.mock(UserRepository.class);
    }
}
