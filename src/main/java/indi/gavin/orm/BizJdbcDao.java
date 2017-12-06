package indi.gavin.orm;

import indi.gavin.orm.jdbc.JdbcWizard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * 提供BizJdbc数据库连接的共同类
 * 
 * @author Gavin
 *
 */
public class BizJdbcDao implements JdbcWired {
    @Autowired
    private JdbcWizard db;

    @Override
    public JdbcTemplate getJdbcTemplate() {
       return db;
    }
}
