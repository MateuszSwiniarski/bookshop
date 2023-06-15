package pl.rodzyn.bookshop.order.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.rodzyn.bookshop.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.rodzyn.bookshop.order.application.port.QueryOrderUseCase;
import pl.rodzyn.bookshop.order.domain.OrderStatus;

import java.net.URI;
import java.util.*;

import pl.rodzyn.bookshop.order.application.RichOrder;
import pl.rodzyn.bookshop.security.UserSecurity;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {
    private final QueryOrderUseCase queryOrder;
    private final ManipulateOrderUseCase manipulateOrder;
    private final UserSecurity userSecurity;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> getOrders(){
        return queryOrder.findAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id, @AuthenticationPrincipal User user){
        return queryOrder.findById(id)
                .map(order -> authorize(order, user))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<RichOrder> authorize(RichOrder order, User user){
        if(userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
                .parseString(status)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + status));
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, "admin@example.org");
        manipulateOrder.updateOrderStatus(command);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }
}
