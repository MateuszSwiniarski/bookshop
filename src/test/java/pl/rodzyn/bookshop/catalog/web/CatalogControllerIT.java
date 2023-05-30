package pl.rodzyn.bookshop.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.rodzyn.bookshop.catalog.db.AuthorJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Author;
import pl.rodzyn.bookshop.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class CatalogControllerIT {

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void getAllBook() {
        //given
        Author goetz = authorJpaRepository.save(new Author("Brain Goetz"));
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practise",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());
        //then
        assertEquals(2, all.size());
    }

}