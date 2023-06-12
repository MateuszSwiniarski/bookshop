package pl.rodzyn.bookshop.order.application.port;

import lombok.*;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.order.domain.Delivery;
import pl.rodzyn.bookshop.order.domain.OrderStatus;
import pl.rodzyn.bookshop.order.domain.Recipient;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Min;

import static java.util.Collections.emptyList;

public interface ManipulateOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> items;
        Recipient recipient;
        @Builder.Default
        Delivery delivery = Delivery.COURIER;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    @Value
    @Setter
    class UpdateStatusCommand {
        Long orderId;
        OrderStatus status;
        String email;
    }

    @Getter
    @Value
    class PlaceOrderResponse {
        boolean success;
        Long orderId;
        List<String> errors;

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, orderId, emptyList());
        }
        public static PlaceOrderResponse failure(String... errors) {
            return new PlaceOrderResponse(false, null, Arrays.asList(errors));
        }
    }


    @Getter
    @Value
    class UpdateStatusResponse {
        boolean success;
        OrderStatus status;
        List<String> errors;

        public static UpdateStatusResponse success(OrderStatus status) {
            return new UpdateStatusResponse(true, status, emptyList());
        }
        public static UpdateStatusResponse failure(String... errors) {
            return new UpdateStatusResponse(false, null, Arrays.asList(errors));
        }
    }
}
