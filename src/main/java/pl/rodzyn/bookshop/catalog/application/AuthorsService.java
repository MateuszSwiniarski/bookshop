package pl.rodzyn.bookshop.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rodzyn.bookshop.catalog.application.port.AuthorsUseCase;
import pl.rodzyn.bookshop.catalog.db.AuthorJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorsService implements AuthorsUseCase {
    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
