package pl.rodzyn.bookshop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.OrderItem;
import pl.rodzyn.bookshop.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

import static pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.*;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final String title;
    private final long limit;
    private final String author;

    public ApplicationStartup(
            CatalogUseCase catalog,
            ManipulateOrderUseCase placeOrder,
            QueryOrderUseCase queryOrder,
            @Value("${bookshop.catalog.query}") String title,
            @Value("${bookshop.catalog.limit:9}") long limit,  ////colon + value - if limit not exit, then use default value 9
            @Value("${bookshop.catalog.author}") String author){
        this.catalog = catalog;
        this.placeOrder = placeOrder;
        this.queryOrder = queryOrder;
        this.title = title;
        this.limit = limit;
        this.author = author;
    }

    @Override
    public void run(String... args){
        initData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {
        Book panTadeusz = catalog.findOneByTitle("Pan Tadeusz")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalog.findOneByTitle("Chłopi")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        //create recipient
        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Armii Krajowej 23")
                .city("Poznań")
                .zipCode("45-342")
                .email("jan@kowalski.pl")
                .build();

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItem(panTadeusz.getId(), 16))
                .item(new OrderItem(chlopi.getId(), 7))
                .build();

        PlaceOrderResponse response = placeOrder.placeOrder(command);
        System.out.println("Created ORDER with id: " + response.getOrderId());

        //list all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice()
                + " DETAILS: " + order));
    }


    private void searchCatalog() {
        findByTitle();
        findByAuthor();
        findAndUpdate();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1983, new BigDecimal("19.9")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Chłopi", "Własysław Reymon", 1899,new BigDecimal("14.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Quo Vadis", "Henryk Sienkiewicz", 1954, new BigDecimal("16.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1896, new BigDecimal("29.90")));

        catalog.addBook(new CatalogUseCase.CreateBookCommand("Harry Potter i komnata tajemnic", "JK Rowlink", 1999, new BigDecimal("54.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Władca pierścieni i dwie wieże", "JRR Tolkien", 1987, new BigDecimal("27.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Mężczyżni, którzy nienawidzą kobiet", "Stieg Larsson", 1981, new BigDecimal("30.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Sezon Burz", "Andrzej Sapkowski", 2014, new BigDecimal("34.90")));
    }

    private void findAndUpdate() {
        System.out.println("Updating book....");
        catalog.findOneByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .ifPresent(book -> {
                    UpdateBookCommand command = UpdateBookCommand.builder()
                            .id(book.getId())
                            .title("Pan Tadeusz, czyli Ostatni Zajazd na Litwie")
                            .build();
                    CatalogUseCase.UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result: " +  response.isSuccess());
                });
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
