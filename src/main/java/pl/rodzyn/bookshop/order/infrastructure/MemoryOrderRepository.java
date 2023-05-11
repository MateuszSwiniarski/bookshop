package pl.rodzyn.bookshop.order.infrastructure;

import org.springframework.stereotype.Repository;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryOrderRepository implements OrderRepository {
    private Map<Long, Order> storage = new ConcurrentHashMap<>();
    private AtomicLong ID_NEXT = new AtomicLong(0L);

    @Override
    public Order save(Order order) {
        if(order.getId() != null){
            storage.put(order.getId(), order);
        }else {
            long nextID = nextID();
            order.setId(nextID);
            order.setCreatedAt(LocalDateTime.now());
            storage.put(nextID, order);
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    private long nextID() {
        return ID_NEXT.getAndIncrement();
    }
}
