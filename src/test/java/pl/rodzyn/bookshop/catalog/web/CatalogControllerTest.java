package pl.rodzyn.bookshop.catalog.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CatalogController.class})
class CatalogControllerTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void shouldGetAllBooks() {
        //givnen
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L);
        Book concurrency = new Book("Java Concurrency", 2006, new BigDecimal("99.00"), 50L);
        when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));
        //then
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());
        //when
        assertEquals(2, all.size());
    }

}