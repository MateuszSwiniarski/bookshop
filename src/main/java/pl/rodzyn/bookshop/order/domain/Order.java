package pl.rodzyn.bookshop.order.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {
    private Long id;

    @Builder.Default
    private OrderStatus status = OrderStatus.NEW;

    private List<OrderItem> items;

    private Recipient recipient;

    private LocalDateTime createdAt;

    public BigDecimal totalPrice() {
        return items.stream()
                .map(items -> items.getBook().getPrice().multiply(new BigDecimal(items.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}