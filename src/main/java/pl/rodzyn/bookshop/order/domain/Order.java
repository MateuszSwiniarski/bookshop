package pl.rodzyn.bookshop.order.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class Order {
    private Long id;

    @Builder.Default
    private OrderStatus status = OrderStatus.NEW;

    private List<OrderItem> items;

    private Recipient recipient;

    private LocalDateTime createdAt;
}
