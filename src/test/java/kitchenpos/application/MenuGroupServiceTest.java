package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹을 만드는 것을 테스트한다")
    void create() {
        MenuGroup menuGroup = new MenuGroup();

        when(menuGroupDao.save(any())).thenReturn(menuGroup);

        assertThat(menuGroupService.create(menuGroup)).isEqualTo(menuGroup);
    }

    @Test
    @DisplayName("메뉴의 그룹을 조회할 수 있다")
    void list() {
        MenuGroup menuGroup1 = new MenuGroup();
        MenuGroup menuGroup2 = new MenuGroup();

        when(menuGroupDao.findAll()).thenReturn(Arrays.asList(menuGroup1, menuGroup2));

        assertThat(menuGroupService.list()).containsExactly(menuGroup1, menuGroup2);
    }
}