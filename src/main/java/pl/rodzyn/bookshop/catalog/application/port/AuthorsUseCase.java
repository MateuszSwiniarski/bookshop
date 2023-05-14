package pl.rodzyn.bookshop.catalog.application.port;

import pl.rodzyn.bookshop.catalog.domain.Author;

import java.util.List;

public interface AuthorsUseCase {
    List<Author> findAll();
}
