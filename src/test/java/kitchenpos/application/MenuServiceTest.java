package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("정상적인 메뉴 추가 테스트")
    void menuAddTest() {
        Menu menu = new Menu();
        MenuProduct menuProduct = new MenuProduct();
        Product product = new Product();
        menu.setMenuGroupId(1L);
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menuProduct.setQuantity(2);
        product.setName("반반치킨");
        product.setPrice(BigDecimal.valueOf(5000));

        when(menuGroupDao.existsById(any())).thenReturn(true);
        when(productDao.findById(any())).thenReturn(java.util.Optional.of(product));
        when(menuDao.save(any())).thenReturn(menu);

        assertThat(menuService.create(menu)).isEqualTo(menu);
    }

    @Test
    @DisplayName("메뉴의 가격이 0보다 작은 경우 예외 테스트")
    void menuAddExceptionTest() {
        Menu menu = new Menu();
        MenuProduct menuProduct = new MenuProduct();
        Product product = new Product();
        menu.setMenuGroupId(1L);
        menu.setPrice(BigDecimal.valueOf(-3000));
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menuProduct.setQuantity(2);
        product.setName("반반치킨");
        product.setPrice(BigDecimal.valueOf(5000));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹이 유효하지 않을 경우 예외 테스트")
    void menuNoMenuGroupTest() {
        Menu menu = new Menu();
        MenuProduct menuProduct = new MenuProduct();
        Product product = new Product();
        menu.setMenuGroupId(2L);
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menuProduct.setQuantity(2);
        product.setName("반반치킨");
        product.setPrice(BigDecimal.valueOf(5000));

        when(menuGroupDao.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 메뉴의 목록을 조회하는 테스트")
    void menuListTest() {
        Menu menu1 = new Menu();
        Menu menu2 = new Menu();
        Menu menu3 = new Menu();
        MenuProduct menuProduct1 = new MenuProduct();
        MenuProduct menuProduct2 = new MenuProduct();
        MenuProduct menuProduct3 = new MenuProduct();
        menu1.setId(1L);
        menu1.setPrice(BigDecimal.valueOf(15000));
        menu1.setName("양념치킨");
        menu2.setId(2L);
        menu2.setPrice(BigDecimal.valueOf(14000));
        menu2.setName("반반치킨");
        menu3.setId(3L);
        menu3.setPrice(BigDecimal.valueOf(13000));
        menu3.setName("후라이드치킨");
        menuProduct1.setQuantity(1);
        menuProduct1.setMenuId(1L);
        menuProduct2.setQuantity(1);
        menuProduct2.setMenuId(2L);
        menuProduct3.setQuantity(1);
        menuProduct3.setMenuId(3L);

        when(menuDao.findAll()).thenReturn(Arrays.asList(menu1, menu2, menu3));
        when(menuProductDao.findAllByMenuId(1L)).thenReturn(Collections.singletonList(menuProduct1));
        when(menuProductDao.findAllByMenuId(2L)).thenReturn(Collections.singletonList(menuProduct2));
        when(menuProductDao.findAllByMenuId(3L)).thenReturn(Collections.singletonList(menuProduct3));

        List<Menu> menus = menuService.list();

        assertAll(
                () -> assertThat(menus).hasSize(3),
                () -> assertThat(menus).containsExactly(menu1, menu2, menu3),
                () -> assertThat(menus.get(0).getMenuProducts()).containsExactly(menuProduct1),
                () -> assertThat(menus.get(1).getMenuProducts()).containsExactly(menuProduct2),
                () -> assertThat(menus.get(2).getMenuProducts()).containsExactly(menuProduct3)
        );


    }

}