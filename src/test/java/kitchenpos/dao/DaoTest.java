package kitchenpos.dao;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql({"/truncate.sql", "/dummy.sql"})
public abstract class DaoTest {

}
