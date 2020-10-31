package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
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
}