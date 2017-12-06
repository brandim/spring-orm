package indi.gavin.orm;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 绑定JdbcTemplate。
 * 
 * 实现该接口表现可通过该类获取到JdbcTemplate实例。
 * 
 * @author gavin
 *
 */
public interface JdbcWired {
    
    JdbcTemplate getJdbcTemplate();
}
