package pl.rodzyn.bookshop.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CatalogControllerIT {

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void getAllBooks() {
        //given
        givenEffectiveJava();
        givenJavaConcurrencyInPractise();
        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());
        //then
        assertEquals(2, all.size());
    }

    @Test
    public void getBooksByAuthor() {
        //given
        givenEffectiveJava();
        givenJavaConcurrencyInPractise();
        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.of("Bloch"));
        //then
        assertEquals(1, all.size());
        assertEquals("Effective Java", all.get(0).getTitle());
    }

    @Test
    public void getBooksByTitle() {
        //given
        givenEffectiveJava();
        givenJavaConcurrencyInPractise();
        //when
        List<Book> all = controller.getAll(Optional.of("Java Concurrency in Practise"), Optional.empty());
        //then
        assertEquals(1, all.size());
        assertEquals("Java Concurrency in Practise", all.get(0).getTitle());
    }

    private void givenEffectiveJava() {
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
    }

    private void givenJavaConcurrencyInPractise() {
        Author goetz = authorJpaRepository.save(new Author("Brain Goetz"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practise",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
    }



}