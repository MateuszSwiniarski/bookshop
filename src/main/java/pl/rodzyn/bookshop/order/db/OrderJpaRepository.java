package pl.rodzyn.bookshop.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rodzyn.bookshop.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
