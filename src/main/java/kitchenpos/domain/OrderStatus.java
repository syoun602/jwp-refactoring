package kitchenpos.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderStatus {
    COOKING, MEAL, COMPLETION;

    public static List<OrderStatus> getUndoneStatuses() {
        return Arrays.stream(values())
                .filter(it -> !it.isCompleted())
                .collect(Collectors.toList());
    }

    public boolean isCompleted() {
        return this.equals(COMPLETION);
    }
}
