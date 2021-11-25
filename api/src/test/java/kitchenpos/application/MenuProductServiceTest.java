package kitchenpos.application;

import kitchenpos.application.menu.MenuProductService;
import kitchenpos.application.menu.MenuProductValidator;
import kitchenpos.domain.menu.Menu;
import kitchenpos.domain.menu.MenuGroup;
import kitchenpos.domain.menu.repository.MenuProductRepository;
import kitchenpos.domain.product.Product;
import kitchenpos.dto.menu.MenuRequest;
import kitchenpos.exception.NonExistentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static kitchenpos.application.ServiceTestFixture.DomainFactory.CREATE_MENU;
import static kitchenpos.application.ServiceTestFixture.DomainFactory.CREATE_PRODUCT;
import static kitchenpos.application.ServiceTestFixture.RequestFactory.CREATE_MENU_PRODUCT_REQUEST;
import static kitchenpos.application.ServiceTestFixture.RequestFactory.CREATE_MENU_REQUEST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

@ServiceTest
@DisplayName("MenuProduct 서비스 테스트")
class MenuProductServiceTest {
    @InjectMocks
    private MenuProductService menuProductService;

    @Mock
    private MenuProductRepository menuProductRepository;

    @Mock
    private MenuProductValidator menuProductValidator;

    @DisplayName("MenuProduct를 생성한다. - 실패, Product를 찾을 수 없음.")
    @Test
    void addMenuToMenuProductFailedProductNotFound() {
        // given
        MenuRequest menuRequest = CREATE_MENU_REQUEST("kitchenpos.ui.menu", BigDecimal.TEN, 1L,
                Collections.singletonList(CREATE_MENU_PRODUCT_REQUEST(-1L, 1L))
        );

        Menu menu = CREATE_MENU(1L, menuRequest.getName(), menuRequest.getPrice(), new MenuGroup("group"));

        doThrow(NonExistentException.class).when(menuProductValidator).validate(menuRequest.getMenuProducts(), menu);

        // when - then
        assertThatThrownBy(() -> menuProductService.addMenuToMenuProduct(menuRequest, menu))
                .isInstanceOf(NonExistentException.class);
        then(menuProductRepository).should(never())
                .save(any());
    }

    @DisplayName("MenuProduct를 생성한다. - 실패, 메뉴 금액이 단품 합산 금액보다 큼")
    @Test
    void addMenuToMenuProductFailedSumNotValid() {
        // given
        MenuRequest menuRequest = CREATE_MENU_REQUEST("kitchenpos.ui.menu", BigDecimal.valueOf(20000), 1L,
                Arrays.asList(CREATE_MENU_PRODUCT_REQUEST(1L, 1L))
        );
        Product product = CREATE_PRODUCT(null, "kitchenpos.ui.product", BigDecimal.TEN);
        Menu menu = CREATE_MENU(1L, menuRequest.getName(), menuRequest.getPrice(), new MenuGroup("group"));

        doThrow(IllegalArgumentException.class).when(menuProductValidator).validate(menuRequest.getMenuProducts(), menu);

        // when - then
        assertThatThrownBy(() -> menuProductService.addMenuToMenuProduct(menuRequest, menu))
                .isInstanceOf(IllegalArgumentException.class);
        then(menuProductRepository).should(never())
                .save(any());
    }
}