package pl.rodzyn.bookshop.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionSystemException;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.db.BookJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.OrderStatus;
import pl.rodzyn.bookshop.order.domain.Recipient;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void userCanPlaceOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(jcip.getId(), 10))
                .build();
        //when
        PlaceOrderResponse response = service.placeOrder(command);
        //then
        assertTrue(response.isSuccess());
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        assertEquals(40L, AvailableCopiesOf(jcip));
    }

    @Test
    public void userCanRevokeOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        service.updateOrderStatus(command);
        //then
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, AvailableCopiesOf(effectiveJava));
    }

    @Test
    public void userCannotRevokePaidOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, recipient);
        service.updateOrderStatus(command);
        UpdateStatusCommand commandCanc = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(commandCanc);
        });
        //then
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
        assertTrue(exception.getMessage().contains("Unable to mark PAID order as CANCELED"));
    }

    @Test
    public void userCannotRevokedShippedOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand commandPaid = new UpdateStatusCommand(orderId, OrderStatus.PAID, recipient);
        service.updateOrderStatus(commandPaid);
        UpdateStatusCommand commandShipped = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, recipient);
        service.updateOrderStatus(commandShipped);
        UpdateStatusCommand commandCanceled = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(commandCanceled);
        });
        //then
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.SHIPPED, queryOrderService.findById(orderId).get().getStatus());
        assertTrue(exception.getMessage().contains("Unable to mark SHIPPED order as CANCELED"));
    }

    @Test
    public void userCannotOrderNoExistingBooks() {
        //given
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(42L, 0))
                .build();
        //when
        assertThrows(EntityNotFoundException.class, () -> {
            service.placeOrder(command);
        });
        //then
        PlaceOrderResponse failure = PlaceOrderResponse.failure("Book doesn't exist");
        assertFalse(failure.isSuccess());
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), -19))
                .build();
        //when
        assertThrows(TransactionSystemException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertEquals(50L, AvailableCopiesOf(effectiveJava));
    }

    @Test
    public void userCannotRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, adam);
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, "marek@example.org");
        service.updateOrderStatus(command);
        //then
        assertEquals(35L, AvailableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void userCanOrderMoreBooksThanAvailable() {
        //given
        Book effectiveJava = givenEffectiveJava(5L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(jcip.getId(), 10))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() +  " requested"));
    }

    private Long placeOrder(Long bookId, int copies, String recipient){
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return service.placeOrder(command).getOrderId();
    }

    private Long placeOrder(Long bookId, int copies){
        return placeOrder(bookId, copies, "john@example.org");
    }

    private Book givenJavaConcurrency(long available) {
        return bookJpaRepository.save(new Book("Java Concurrency in Practise", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("123.00"), available));
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }

    private Long AvailableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                            .get()
                            .getAvailable();
    }
}