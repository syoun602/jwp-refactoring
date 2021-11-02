package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TablesTest {

    private TableGroup tableGroup;
    private OrderTable orderTable;
    private OrderTable orderTable2;

    @BeforeEach
    void setUp() {
        tableGroup = new TableGroup(1L);
        orderTable = new OrderTable(null, 0, true);
        orderTable2 = new OrderTable(null, 0, true);
    }

    @DisplayName("테이블 개수 검증 성공")
    @Test
    void validateSize() {
        Tables tables = new Tables(Arrays.asList(orderTable, orderTable2));
        assertThatCode(() -> tables.validateSize(2))
                .doesNotThrowAnyException();
    }

    @DisplayName("테이블 개수 검증 성공 실패")
    @Test
    void validateSizeFail() {
        Tables tables = new Tables(Arrays.asList(orderTable, orderTable2));
        assertThatThrownBy(() -> tables.validateSize(1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 조건 검증 성공")
    @Test
    void validateCondition() {
        Tables tables = new Tables(Arrays.asList(orderTable, orderTable2));
        assertThatCode(tables::validateCondition)
                .doesNotThrowAnyException();
    }

    @DisplayName("테이블 조건 검증 실패 - 빈 테이블 아닌 경우")
    @Test
    void validateConditionFailWhenTableIsNotEmpty() {
        orderTable2 = new OrderTable(null, 0, false);
        Tables tables = new Tables(Arrays.asList(orderTable, orderTable2));
        assertThatThrownBy(tables::validateCondition)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 조건 검증 실패 - 테이블 그룹이 있는 경우")
    @Test
    void validateConditionFailWhenTableHasTableGroup() {
        orderTable2 = new OrderTable(tableGroup, 0, true);
        Tables tables = new Tables(Arrays.asList(orderTable, orderTable2));
        assertThatThrownBy(tables::validateCondition)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 조건 변경")
    @Test
    void changeCondition() {
        Tables tables = new Tables(Arrays.asList(orderTable, orderTable2));
        assertThat(orderTable.tableGroupId()).isNull();
        assertThat(orderTable2.tableGroupId()).isNull();
        assertThat(orderTable.isEmpty()).isTrue();
        assertThat(orderTable2.isEmpty()).isTrue();
        tables.changeCondition(tableGroup);
        assertThat(orderTable.tableGroupId()).isNotNull();
        assertThat(orderTable2.tableGroupId()).isNotNull();
        assertThat(orderTable.isEmpty()).isFalse();
        assertThat(orderTable.isEmpty()).isFalse();
    }
}