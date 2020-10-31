package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableService tableService;

    @BeforeEach
    void setUp() {
        tableService = new TableService(orderDao, orderTableDao);
    }

    @Test
    @DisplayName("테이블을 생성하는 테스트")
    void create() {
        OrderTable orderTable = new OrderTable();

        given(orderTableDao.save(any())).willReturn(orderTable);

        assertThat(tableService.create(orderTable)).isEqualTo(orderTable);
    }

    @Test
    @DisplayName("테이블을 조회하는 테스트")
    void list() {
        OrderTable orderTable1 = new OrderTable();
        OrderTable orderTable2 = new OrderTable();

        given(orderTableDao.findAll()).willReturn(Arrays.asList(orderTable1, orderTable2));

        assertThat(tableService.list()).hasSize(2);
    }

    @Test
    @DisplayName("테이블이 그룹지어져 있지 않고, 계산 완료인 경우 테이블을 비우는 테스트")
    void emptyTable() {
        OrderTable orderTable = new OrderTable();
        OrderTable savedOrderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(true);
        savedOrderTable.setId(1L);
        savedOrderTable.setEmpty(false);

        given(orderTableDao.findById(any())).willReturn(java.util.Optional.of(savedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(false);
        given(orderTableDao.save(any())).willReturn(orderTable);

        assertThat(tableService.changeEmpty(savedOrderTable.getId(), orderTable).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("테이블이 그룹지어져 있는 경우 테이블을 비우려 할 때 예외 처리")
    void emptyTableWhenGrouped() {
        OrderTable orderTable = new OrderTable();
        OrderTable savedOrderTable = new OrderTable();
        orderTable.setId(1L);
        savedOrderTable.setId(1L);
        savedOrderTable.setTableGroupId(2L);

        given(orderTableDao.findById(any())).willReturn(java.util.Optional.of(savedOrderTable));

        assertThatThrownBy(() -> tableService.changeEmpty(savedOrderTable.getId(), orderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블의 상태가 조리/식사인 경우 테이블을 비우려 할 때 예외 처리")
    void emptyTableWhenStatusNotCompletion() {
        OrderTable orderTable = new OrderTable();
        OrderTable savedOrderTable = new OrderTable();
        orderTable.setId(1L);
        savedOrderTable.setId(1L);

        given(orderTableDao.findById(any())).willReturn(java.util.Optional.of(savedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(true);

        assertThatThrownBy(() -> tableService.changeEmpty(savedOrderTable.getId(), orderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }
}