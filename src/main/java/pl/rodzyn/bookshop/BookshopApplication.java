package pl.rodzyn.bookshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class BookshopApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BookshopApplication.class, args);
    }

    CatalogService catalogService;

    public BookshopApplication(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void run(String... args){
        List<Book> books = catalogService.findByTitle("Pan Tadeusz");
        books.forEach(System.out::println);
    }
}
