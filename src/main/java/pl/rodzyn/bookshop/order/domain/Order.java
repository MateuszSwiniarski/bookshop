package pl.rodzyn.bookshop.order.domain;

import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private OrderStatus status;
    private List<OrderItem> items;
    private Recipient recipient;
    private LocalDateTime createdAt;

    BigDecimal totalPrice() {
        return items.stream()
                .map(items -> items.getBook().getPrice().multiply(new BigDecimal(items.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
