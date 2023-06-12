package pl.rodzyn.bookshop.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionSystemException;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.db.BookJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.clock.Clock;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.Delivery;
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
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jcip));
    }

    @Test
    public void userCanRevokeOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        service.updateOrderStatus(command);
        //then
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, availableCopiesOf(effectiveJava));
    }

    @Test
    public void userCannotRevokePaidOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, recipient);
        service.updateOrderStatus(command);
        UpdateStatusCommand commandCanc = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(commandCanc));
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
        assertTrue(exception.getMessage().contains("Unable to mark PAID order as CANCELED"));
    }

    @Test
    public void userCannotRevokedShippedOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand commandPaid = new UpdateStatusCommand(orderId, OrderStatus.PAID, recipient);
        service.updateOrderStatus(commandPaid);
        UpdateStatusCommand commandShipped = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, recipient);
        service.updateOrderStatus(commandShipped);
        UpdateStatusCommand commandCanceled = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(commandCanceled));
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
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
        assertThrows(EntityNotFoundException.class, () -> service.placeOrder(command));
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
        assertThrows(TransactionSystemException.class, () -> service.placeOrder(command));
        //then
        assertEquals(50L, availableCopiesOf(effectiveJava));
    }

    @Test
    public void userCannotRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        String adam = "adam@exapmle.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, adam);
        service.updateOrderStatus(command);
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, admin);
        service.updateOrderStatus(command);
        //then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanMarkORderAsPaid() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@exapmle.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, "marek@exapmle.org");
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, admin);
        service.updateOrderStatus(command);
        //then
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(35L, availableCopiesOf(effectiveJava));
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.placeOrder(command));
        //then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() +  " requested"));
    }

    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placeOrder(book.getId(), 1);
        //then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }

    @Test
    public void shippingCostsAreDiscountedOver100złotys() {
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placeOrder(book.getId(), 3);
        //then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrderPrice().getItemsPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsHalfPriceWhenTotalOver200zlotys(){
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placeOrder(book.getId(), 5);
        //then
        RichOrder order = orderOf(orderId);
        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsFreeWhenTotalOver400zlotys(){
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placeOrder(book.getId(), 10);
        //then
        RichOrder order = orderOf(orderId);
        assertEquals("449.10", order.getFinalPrice().toPlainString());
    }

    private RichOrder orderOf(Long orderId){
        return queryOrderService.findById(orderId).get();
    }

    private Book givenBook(long available, String price) {
        return bookJpaRepository.save(new Book("Java Concurrency in Practise", 2006, new BigDecimal(price), available));
    }

    private Long placeOrder(Long bookId, int copies, String recipient){
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .delivery(Delivery.COURIER)
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

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                            .get()
                            .getAvailable();
    }
}