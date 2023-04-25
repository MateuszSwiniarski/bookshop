package pl.rodzyn.bookshop.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {
    private final OrderRepository repository;
    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }
}
