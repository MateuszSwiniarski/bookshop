package pl.rodzyn.bookshop.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rodzyn.bookshop.catalog.db.BookJpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.db.OrderJpaRepository;
import pl.rodzyn.bookshop.order.db.RecipientJpaRepository;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderItem;
import pl.rodzyn.bookshop.order.domain.OrderStatus;
import pl.rodzyn.bookshop.order.domain.Recipient;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
class ManipulateOrderService implements ManipulateOrderUseCase {
    private final OrderJpaRepository repository;
    private final BookJpaRepository bookRepository;
    private final RecipientJpaRepository recipientJpaRepository;


    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command
                .getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());
        Order order = Order
                .builder()
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .items(items)
                .build();
        Order save = repository.save(order);
        bookRepository.saveAll(updateBooks(items));
        return PlaceOrderResponse.success(save.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {

        return recipientJpaRepository
                .findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }

    private Set<Book> updateBooks(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = bookRepository.getOne(command.getBookId());
        int quantity = command.getQuantity();
        if(book.getAvailable() >= quantity) {
            return new OrderItem(book, quantity);
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + quantity + " of " + book.getAvailable() + " available ");

    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        repository.findById(id)
                .ifPresent(order -> {
                    order.updateStatus(status);
                    repository.save(order);
                });
    }
}
