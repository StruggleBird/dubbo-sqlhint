package org.zt.middleware;

/**
 * Mycat 数据库中间件的SQL hint 实现
 * 
 * @author Ternence
 * @date 2016年7月20日
 */
public class MycatSqlHint extends SqlHint {


    private static final String MYCAT_DB_MASTER_HINT = "/*#mycat:db_type=master*/";

    private static final String MYCAT_DB_SLAVE_HINT = "/*#mycat:db_type=slave*/";

    @Override
    public String getRouteMasterHint() {
        return MYCAT_DB_MASTER_HINT;
    }

    @Override
    public String getRouteSlaveHint() {
        return MYCAT_DB_SLAVE_HINT;
    }

}
