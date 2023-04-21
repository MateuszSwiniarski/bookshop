package pl.rodzyn.bookshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.catalog.application.CatalogController;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogService;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogController catalogController;

    public ApplicationStartup(CatalogController catalogController) {
        this.catalogController = catalogController;
    }

    @Override
    public void run(String... args){
        List<Book> books = catalogController.findByTittle("Pan");
        books.forEach(System.out::println);
    }
}
