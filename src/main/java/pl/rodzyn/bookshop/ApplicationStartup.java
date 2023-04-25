package pl.rodzyn.bookshop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.domain.Book;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final String title;
    private final long limit;
    private final String author;

    public ApplicationStartup(
            CatalogUseCase catalog,
            @Value("${bookshop.catalog.query}") String title,
            @Value("${bookshop.catalog.limit:9}") long limit,  ////colon + value - if limit not exit, then use default value 9
            @Value("${bookshop.catalog.author}") String author){
        this.catalog = catalog;
        this.title = title;
        this.limit = limit;
        this.author = author;
    }

    @Override
    public void run(String... args){
        List<Book> books = catalog.findByTitle(title);
        books.stream().limit(limit).forEach(System.out::println);

        List<Book> byAuthor = catalog.findByAuthor(author);
        byAuthor.forEach(System.out::println);
    }
}
