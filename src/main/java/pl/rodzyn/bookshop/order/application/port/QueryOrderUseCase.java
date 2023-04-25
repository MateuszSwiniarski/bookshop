package pl.rodzyn.bookshop.order.application.port;

import pl.rodzyn.bookshop.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
