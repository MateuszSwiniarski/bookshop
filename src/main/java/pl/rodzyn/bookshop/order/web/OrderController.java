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
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.Order;
import pl.rodzyn.bookshop.order.domain.OrderItem;
import pl.rodzyn.bookshop.order.domain.OrderStatus;
import pl.rodzyn.bookshop.order.domain.Recipient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase.RichOrder;

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

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public void createOrder(@RequestBody Order command) {
//        manipulateOrder.createOrder(command);
//    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public void createOrder(@RequestBody CreateOrderCommand command) {
//        manipulateOrder.placeOrder(command.toPlaceOrderCommand());
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody CreateOrderCommand command) {
        ManipulateOrderUseCase.PlaceOrderResponse placeOrderResponse = manipulateOrder.placeOrder(command.toPlaceOrderCommand());
        return ResponseEntity.created(createBookUri(placeOrderResponse)).build();
    }

    private URI createBookUri(ManipulateOrderUseCase.PlaceOrderResponse response) {
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
    static class CreateOrderCommand {
        List<OrderItemCommand> items;
        RecipientCommand recipient;

        PlaceOrderCommand toPlaceOrderCommand() {
            List<OrderItem> orderItems = items
                    .stream()
                    .map(item -> new OrderItem(item.getBookId(), item.quantity))
                    .collect(Collectors.toList());
            return new PlaceOrderCommand(orderItems, recipient.toRecipient());
        }
    }

    @Data
    static class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    @Data
    static class RecipientCommand {
        String name;
        String phone;
        String street;
        String city;
        String zipCode;
        String email;

        Recipient toRecipient() {
            return new Recipient(name, phone, street, city, zipCode, email);
        }
    }

    @Data
    static class UpdateStatusCommand {
        String status;
    }
}
