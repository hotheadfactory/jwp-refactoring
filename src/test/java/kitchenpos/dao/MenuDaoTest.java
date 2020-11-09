package kitchenpos.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MenuDaoTest extends DaoTest {

    @Autowired
    private MenuDao menuDao;

    @Test
    @DisplayName("메뉴 저장 테스트")
    void save() {
        Menu menu = new Menu();
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuGroupId(1L);

        Menu savedMenu = menuDao.save(menu);
        assertAll(() -> {
            assertThat(savedMenu.getName()).isEqualTo("후라이드치킨");
            assertThat(savedMenu.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15000));
            assertThat(savedMenu.getMenuGroupId()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("메뉴 id로 조회 테스트")
    void findById() {
        Menu menu = new Menu();
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuGroupId(1L);

        Menu savedMenu = menuDao.save(menu);

        Optional<Menu> foundMenu = menuDao.findById(savedMenu.getId());

        assertThat(foundMenu.isPresent()).isTrue();
    }

    @Test
    @DisplayName("전체 메뉴 조회 테스트")
    void findAll() {
        assertThat(menuDao.findAll()).hasSize(6);
    }

    @Test
    @DisplayName("메뉴의 id로 실제 유효한 주문의 갯수를 세는 테스트")
    void countByIdIn() {
        assertThat(menuDao.countByIdIn(Arrays.asList(1L, 3L, 5L))).isEqualTo(3);
        assertThat(menuDao.countByIdIn(Arrays.asList(1L, 3L, 8L))).isEqualTo(2);
    }
}
