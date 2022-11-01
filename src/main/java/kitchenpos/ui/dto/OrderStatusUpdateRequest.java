package kitchenpos.ui.dto;

public class OrderStatusUpdateRequest {

    private String orderStatus;

    public OrderStatusUpdateRequest() {
    }

    public OrderStatusUpdateRequest(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
