package kitchenpos.dao;

import java.util.List;
import kitchenpos.domain.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    long countByIdIn(List<Long> ids);
}
