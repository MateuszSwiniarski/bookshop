package pl.rodzyn.bookshop.order.domain;

import lombok.Value;
import pl.rodzyn.bookshop.catalog.domain.Book;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
