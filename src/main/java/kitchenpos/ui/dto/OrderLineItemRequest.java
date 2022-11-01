package kitchenpos.ui.dto;

public class OrderLineItemRequest {

    private Long menuId;
    private Long quantity;

    public OrderLineItemRequest() {
    }

    public OrderLineItemRequest(final Long menuId, final Long quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
