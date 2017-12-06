/**
 * <p>Copyright &copy; 2017 Gavin.</p>
 */
package indi.gavin.orm;

import indi.gavin.orm.helper.GenericDaoHelper;

import org.springframework.jdbc.core.JdbcTemplate;


/**
 * <b>Application name:</b><br>
 * <b>Application describing:</b>  DaoHelper工厂类 <br>
 * <b>Copyright:</b>Copyright &copy; 2017 Gavin.<br>
 * <b>Date:</b>2015年5月29日<br>
 * @author Gavin
 * @version $Revision: 2.0 $
 */
public final class GenericDaoHelperFactory {

    private GenericDaoHelperFactory() {
        //do nothing
    }

    public static GenericDaoHelper getHelper(JdbcTemplate db, Class<?> poClass) {
        return new GenericDaoHelper(db, poClass);
    }
}
