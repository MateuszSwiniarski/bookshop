package pl.rodzyn.bookshop.order.domain;

import lombok.Value;

@Value
public class UpdateStatusResult {
    OrderStatus newStatus;
    boolean revoke;

    static UpdateStatusResult ok(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, false);
    }

    static UpdateStatusResult revoked(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, true);
    }
}
