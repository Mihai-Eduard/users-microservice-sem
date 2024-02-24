package nl.tudelft.sem.template.example.integration.profiles;

import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockBookRepository")
@Configuration
public class MockBookRepository {

    @Bean
    @Primary
    public BookRepository getMockBookRepository() {
        return Mockito.mock(BookRepository.class);
    }
}
