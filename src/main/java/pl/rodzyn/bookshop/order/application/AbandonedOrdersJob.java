package pl.rodzyn.bookshop.order.application;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.db.OrderJpaRepository;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderStatus;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
class AbandonedOrdersJob {

    @Transactional
    @Scheduled(fixedRate = 60_000)
    public void run(){
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(5);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, timestamp);
        System.out.println("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> orderUseCase.updateOrderStatus(order.getId(), OrderStatus.ABANDONED));
    }

    private final OrderJpaRepository repository;

    private final ManipulateOrderUseCase orderUseCase;
}
