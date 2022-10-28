package kitchenpos.application;

import static kitchenpos.domain.OrderStatus.COOKING;
import static kitchenpos.domain.OrderStatus.MEAL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.dao.OrderRepository;
import kitchenpos.dao.OrderTableRepository;
import kitchenpos.dao.TableGroupRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.ui.dto.TableGroupCreateRequest;
import kitchenpos.ui.dto.TableGroupResponse;
import kitchenpos.ui.dto.TableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableGroupService {

    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableGroupService(final OrderRepository orderRepository,
                             final OrderTableRepository orderTableRepository,
                             final TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
    }

    @Transactional
    public TableGroupResponse create(final TableGroupCreateRequest request) {
        final List<Long> orderTableIds = request.getOrderTableIds();
        validateOrderTablesSize(orderTableIds);
        final List<OrderTable> savedOrderTables = orderTableRepository.findByIdIn(orderTableIds);
        validateTablesExist(orderTableIds, savedOrderTables);
        final TableGroup tableGroup = TableGroup.groupTables(savedOrderTables, LocalDateTime.now());
        tableGroupRepository.save(tableGroup);

        return new TableGroupResponse(
                tableGroup.getId(),
                tableGroup.getCreatedDate(),
                tableGroup.getOrderTables()
                        .stream()
                        .map(it -> new TableResponse(
                                it.getId(),
                                it.getTableGroupIdOrElseNull(),
                                it.getNumberOfGuests(),
                                it.isEmpty()
                        ))
                        .collect(Collectors.toList())
        );
    }

    private static void validateTablesExist(final List<Long> orderTableIds, final List<OrderTable> savedOrderTables) {
        if (orderTableIds.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateOrderTablesSize(final List<Long> orderTableIds) {
        if (orderTableIds.isEmpty() || orderTableIds.size() < 2) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        final List<OrderTable> orderTables = orderTableRepository.findAllByTableGroupId(tableGroupId);

        if (orderRepository.existsByOrderTableInAndOrderStatusIn(orderTables, List.of(COOKING, MEAL))) {
            throw new IllegalArgumentException();
        }

        orderTables.forEach(OrderTable::ungroup);
    }
}
