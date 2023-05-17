package pl.rodzyn.bookshop.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.db.AuthorJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Author;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.OrderItem;
import pl.rodzyn.bookshop.order.domain.Recipient;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
class AdminController {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;

    @PostMapping("/data")
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
    }

    private void placeOrder() {
        Book effectiveJava = catalog.findOneByTitle("Effective Java")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book puzzlers = catalog.findOneByTitle("Java Puzzlers")
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
                .item(new OrderItemCommand(effectiveJava.getId(), 16))
                .item(new OrderItemCommand(puzzlers.getId(), 7))
                .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(command);
        System.out.println("Created ORDER with id: " + response.getOrderId());

        //list all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice()
                        + " DETAILS: " + order));
    }

    private void initData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorRepository.save(joshua);
        authorRepository.save(neal);

        CatalogUseCase.CreateBookCommand effectiveJava = new CatalogUseCase.CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.00"),
                50L
        );
        CatalogUseCase.CreateBookCommand javaPuzzlers = new CatalogUseCase.CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00"),
                50L
        );

        catalog.addBook(javaPuzzlers);
        catalog.addBook(effectiveJava);
    }
}