package kitchenpos.application;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableGroupServiceTest {
    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupService tableGroupService;

    @Test
    @DisplayName("테이블을 정상적으로 그룹으로 묶는 테스트")
    void tableGroupingTest() {
        OrderTable orderTable1 = new OrderTable();
        OrderTable orderTable2 = new OrderTable();
        TableGroup tableGroup = new TableGroup();

        orderTable1.setEmpty(true);
        orderTable2.setEmpty(true);
        tableGroup.setOrderTables(Arrays.asList(orderTable1, orderTable2));

        when(orderTableDao.findAllByIdIn(any())).thenReturn(Arrays.asList(orderTable1, orderTable2));
        tableGroup.setId(1L);
        when(tableGroupDao.save(any())).thenReturn(tableGroup);

        assertThat(tableGroupService.create(tableGroup)).isNotNull();
    }

    @Test
    @DisplayName("테이블 1개만 가지고 그룹으로 묶을 때 예외 처리")
    void singleTableGroupExceptionTest() {
        OrderTable orderTable = new OrderTable();
        TableGroup tableGroup = new TableGroup();

        orderTable.setEmpty(true);

        tableGroup.setOrderTables(Collections.singletonList(orderTable));

        assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비어있지 않은 테이블을 묶으려고 할 때 예외 처리")
    void nonEmptyTableExceptionTest() {
        OrderTable orderTable1 = new OrderTable();
        OrderTable orderTable2 = new OrderTable();
        TableGroup tableGroup = new TableGroup();

        orderTable1.setEmpty(false);
        orderTable2.setEmpty(true);
        tableGroup.setOrderTables(Arrays.asList(orderTable1, orderTable2));

        when(orderTableDao.findAllByIdIn(any())).thenReturn(Arrays.asList(orderTable1, orderTable2));

        assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }
}