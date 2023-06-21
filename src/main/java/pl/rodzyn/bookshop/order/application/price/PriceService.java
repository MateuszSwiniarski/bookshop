package pl.rodzyn.bookshop.order.application.price;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rodzyn.bookshop.order.domain.Order;

import java.util.List;
import java.math.BigDecimal;

@Service
public class PriceService {
    private final List<DiscountStrategy> strategies = List.of(
            new DeliveryDiscountStrategy(),
            new TotalPriceDiscountStrategy()
    );

    @Transactional
    public OrderPrice calculatePrice(Order order) {
        return new OrderPrice(
                order.getItemsPrice(),
                order.getDeliveryPrice(),
                discounts(order)
        );
    }

    private BigDecimal discounts(Order order) {
        return strategies
                .stream()
                .map(strategy -> strategy.calculate(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
