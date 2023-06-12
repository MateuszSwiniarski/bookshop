package pl.rodzyn.bookshop.order.application.price;

import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderItem;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        if(isGreaterOrEqual(order, 400)) {
            return lowestBoogPrice(order.getItems());
        }else if(isGreaterOrEqual(order, 200)) {
            BigDecimal lowestPrice = lowestBoogPrice(order.getItems());
            return lowestPrice.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal lowestBoogPrice(Set<OrderItem> items){
        return items.stream()
                .map(x -> x.getBook().getPrice())
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private boolean isGreaterOrEqual(Order order, int value) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(value)) >= 0;
    }
}
