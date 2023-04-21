package pl.rodzyn.bookshop.catalog.application;

import org.springframework.context.annotation.Configuration;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogService;

import java.util.List;

@Configuration
public class CatalogController {

    CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    public List<Book> findByTittle(String tittle){
        return service.findByTitle(tittle);
    }
}
