package kitchenpos.application;

import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productDao);
    }

    @Test
    @DisplayName("정상적인 상품을 등록하는 테스트")
    void create() {
        Product product = new Product();
        product.setName("후라이드 치킨");
        product.setPrice(BigDecimal.valueOf(12000));

        given(productDao.save(any())).willReturn(product);

        assertThat(productService.create(product)).isNotNull();
    }

    @Test
    @DisplayName("가격이 0 미만인 상품을 등록하는 경우 예외 처리")
    void createWithPriceUnderZero() {
        Product product = new Product();
        product.setName("후라이드 치킨");
        product.setPrice(BigDecimal.valueOf(-10000));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 Null인 상품을 등록할 경우 예외 처리")
    void createWithNullPrice() {
        Product product = new Product();
        product.setName("후라이드 치킨");

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 목록 조회 테스트")
    void list() {
        Product product1 = new Product();
        Product product2 = new Product();
        Product product3 = new Product();
        Product product4 = new Product();

        given(productDao.findAll()).willReturn(Arrays.asList(product1, product2, product3, product4));
        List<Product> list = productService.list();
        assertThat(list.size()).isEqualTo(4);
    }
}