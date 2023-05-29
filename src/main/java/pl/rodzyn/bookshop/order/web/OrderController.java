package pl.rodzyn.bookshop.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.OrderStatus;

import java.net.URI;
import java.util.List;

import pl.rodzyn.bookshop.order.application.RichOrder;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {
    private final QueryOrderUseCase queryOrder;
    private final ManipulateOrderUseCase manipulateOrder;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> getAll(){
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getById(@PathVariable Long id){
        return queryOrder.findById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody PlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = manipulateOrder
                .placeOrder(command);
        return ResponseEntity.created(createBookUri(placeOrderResponse)).build();
    }

    private URI createBookUri(PlaceOrderResponse response) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + response.getOrderId().toString()).build().toUri();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
        OrderStatus orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + command.status));
        manipulateOrder.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }

    @Data
    static class UpdateStatusCommand {
        String status;
    }
}
