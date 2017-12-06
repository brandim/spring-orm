package indi.gavin.orm.jdbc;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcWizard extends JdbcTemplate {
    public JdbcWizard(DataSource dataSource) {
        super(dataSource);
    }
}
