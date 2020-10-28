package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(menuDao, orderDao, orderLineItemDao, orderTableDao);

    }

    @Test
    @DisplayName("정상적인 주문 처리 테스트")
    void createTest() {
        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(any())).willReturn(java.util.Optional.of(new OrderTable()));

        Order order = new Order();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(1L);

        given(orderDao.save(any())).willReturn(order);

        order.setOrderTableId(1L);
        order.setOrderLineItems(Collections.singletonList(orderLineItem));

        assertThat(orderService.create(order)).isNotNull();
    }

    @Test
    @DisplayName("주문할 때, 빈 주문일 경우 에러 테스트")
    void emptyOrderLineItemsTest() {
        Order order = new Order();
        order.setOrderTableId(1L);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문할 때, 비어있는 테이블일 경우 에러 테스트")
    void emptyOrderTableTest() {
        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(any())).willReturn(java.util.Optional.of(new OrderTable()));

        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(true);

        given(orderTableDao.findById(any())).willReturn(java.util.Optional.of(orderTable));

        Order order = new Order();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(1L);

        order.setOrderTableId(1L);
        order.setOrderLineItems(Collections.singletonList(orderLineItem));

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("한 번에 모든 주문 상태를 조회하는 테스트")
    void listTest() {
        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();
        OrderLineItem orderLineItem = new OrderLineItem();

        given(orderDao.findAll()).willReturn(Arrays.asList(order1, order2, order3));
        given(orderLineItemDao.findAllByOrderId(any())).willReturn(Collections.singletonList(orderLineItem));

        List<Order> orderList = orderService.list();
        assertThat(orderList.size()).isEqualTo(3);
        for (Order order : orderList) {
            assertThat(order.getOrderLineItems().contains(orderLineItem));
        }
    }

    @Test
    @DisplayName("주문 상태를 변경하는 테스트")
    void changeOrderStatusTest() {
        Order savedOrder = new Order();
        savedOrder.setOrderStatus(OrderStatus.COOKING.name());
        Order changedOrder = new Order();
        changedOrder.setOrderStatus(OrderStatus.MEAL.name());

        given(orderDao.findById(any())).willReturn(java.util.Optional.of(savedOrder));

        Order order = orderService.changeOrderStatus(1L, changedOrder);

        assertThat(order).isEqualTo(savedOrder);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
    }

    @Test
    @DisplayName("계산이 완료된 주문은 상태를 변경할 수 없음을 테스트")
    void completedOrderExceptionTest() {
        Order savedOrder = new Order();
        savedOrder.setOrderStatus(OrderStatus.COMPLETION.name());
        Order changedOrder = new Order();
        changedOrder.setOrderStatus(OrderStatus.MEAL.name());

        given(orderDao.findById(any())).willReturn(java.util.Optional.of(savedOrder));

        assertThatThrownBy(() -> orderService.changeOrderStatus(1L, changedOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }
}