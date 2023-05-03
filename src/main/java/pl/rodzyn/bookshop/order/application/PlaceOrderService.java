package pl.rodzyn.bookshop.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rodzyn.bookshop.order.application.port.PlaceOrderUseCase;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderRepository;

@Service
@RequiredArgsConstructor
class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order.builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }
}
