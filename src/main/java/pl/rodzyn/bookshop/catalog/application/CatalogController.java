package pl.rodzyn.bookshop.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService service;

    public List<Book> findByTittle(String tittle){
        return service.findByTitle(tittle);
    }
}
