package pl.rodzyn.bookshop.order.application.price;

import org.junit.jupiter.api.Test;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.order.application.RichOrder;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderItem;
import pl.rodzyn.bookshop.order.domain.OrderStatus;
import pl.rodzyn.bookshop.order.domain.Recipient;

import java.util.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceServiceTest {

    PriceService priceService = new PriceService();

    @Test
    public void calculatesTotalPriceOfEmptyOrder(){
        //given
        Order order = Order
                .builder()
                .build();
        //when
        OrderPrice price = priceService.calculatePrice(order);
        //then
        assertEquals(BigDecimal.ZERO, price.finalPrice());
    }

    @Test
    public void calculatesTotalPrice(){
        //given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));

        Order order = Order
                .builder()
                .item(new OrderItem(book1, 2))
                .item(new OrderItem(book2, 5))
                .build();
        //when
        OrderPrice price = priceService.calculatePrice(order);
        //then
        assertEquals(new BigDecimal("194.95"), price.finalPrice());
        assertEquals(new BigDecimal("194.95"), price.getItemsPrice());
    }
}