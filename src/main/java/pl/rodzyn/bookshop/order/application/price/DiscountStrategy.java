package pl.rodzyn.bookshop.order.application.price;

import pl.rodzyn.bookshop.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
