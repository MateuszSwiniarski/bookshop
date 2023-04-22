package pl.rodzyn.bookshop;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.catalog.application.CatalogController;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogService;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogController catalogController;
    private final String title;
    private final long limit;
    private final String author;

    public ApplicationStartup(
            CatalogController catalogController,
            @Value("${bookshop.catalog.query}") String title,
            @Value("${bookshop.catalog.limit:9}") long limit,  ////colon + value - if limit not exit, then use default value 9
            @Value("${bookshop.catalog.author}") String author){
        this.catalogController = catalogController;
        this.title = title;
        this.limit = limit;
        this.author = author;
    }

    @Override
    public void run(String... args){
        List<Book> books = catalogController.findByTittle(title);
        books.stream().limit(limit).forEach(System.out::println);

        List<Book> byAuthor = catalogController.findByAuthor(author);
        byAuthor.forEach(System.out::println);
    }
}
