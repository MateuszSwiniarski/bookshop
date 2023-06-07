package pl.rodzyn.bookshop.order.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.rodzyn.bookshop.clock.Clock;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.rodzyn.bookshop.order.db.OrderJpaRepository;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderStatus;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
class AbandonedOrdersJob {
    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;
    private final Clock clock;

    @Transactional
    @Scheduled(cron = "${app.orders.abandon-crone}")
    public void run(){
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime orderThan = clock.now().minus(paymentPeriod);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, orderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            String adminEmail = "admin@example.org";
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, adminEmail);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
