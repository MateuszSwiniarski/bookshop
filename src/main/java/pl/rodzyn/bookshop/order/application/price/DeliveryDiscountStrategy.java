package pl.rodzyn.bookshop.order.application.price;

import pl.rodzyn.bookshop.order.domain.Order;

import java.math.BigDecimal;

public class DeliveryDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        return null;
    }
}
