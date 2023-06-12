package pl.rodzyn.bookshop.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.db.BookJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.clock.Clock;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.rodzyn.bookshop.order.domain.OrderStatus;
import pl.rodzyn.bookshop.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = "app.orders.payment-period=1H"
)
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    QueryOrderService queryOrderService;

    @Autowired
    ManipulateOrderService manipulateOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    Clock.Fake clock;

    @Autowired
    AbandonedOrdersJob ordersJob;

    @Test
    public void shouldMarkOrdersAsAbandoned() {
        //given - orders
        Book book = givenEffectiveJava(50L);
        Long orderId = placeOrder(book.getId(), 15);
        //when - run
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        //then - status changed
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, availableCopiesOf(book));


    }

    private Long placeOrder(Long bookId, int copies){
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new ManipulateOrderUseCase.OrderItemCommand(bookId, copies))
                .build();
        return manipulateOrderService.placeOrder(command).getOrderId();
    }

    private Recipient recipient() {
        return Recipient.builder().email("market@example.org").build();
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("123.00"), available));
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                .get()
                .getAvailable();
    }

}