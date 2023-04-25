package pl.rodzyn.bookshop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase.UpdateBookCommand;
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
        initData();
        findByTitle();
        findByAuthor();
        findAndUpdate();
        findByTitle();
    }

    private void findAndUpdate() {
        catalog.findOneByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .ifPresent(book -> {
                    UpdateBookCommand command = new UpdateBookCommand(
                            book.getId(),
                            "Pan Tadeusz, czyli Ostatni Zajazd na Litwie",
                            book.getAuthor(),
                            book.getYear()
                    );
                    catalog.updateBook(command);
                });
    }

    private void initData() {
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1983));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Chłopi", "Własysław Reymon", 1899));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Quo Vadis", "Henryk Sienkiewicz", 1954));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1896));

        catalog.addBook(new CatalogUseCase.CreateBookCommand("Harry Potter i komnata tajemnic", "JK Rowlink", 1999));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Władca pierścieni i dwie wieże", "JRR Tolkien", 1987));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Mężczyżni, którzy nienawidzą kobiet", "Stieg Larsson", 1981));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Sezon Burz", "Andrzej Sapkowski", 2014));
    }

    private void findByAuthor() {
        List<Book> byAuthor = catalog.findByAuthor(author);
        byAuthor.forEach(System.out::println);
    }

    private void findByTitle() {
        List<Book> books = catalog.findByTitle(title);
        books.stream().limit(limit).forEach(System.out::println);
    }
}
