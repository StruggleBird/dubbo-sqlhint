package org.zt.middleware;

/**
 * sqlHint 用来提供给数据库中间件或者数据库实例去解析，然后执行相应的动作
 * 
 * @author Ternence
 * @date 2016年7月20日
 */
public abstract class SqlHint {


    /**
     * 获取路由到Master的SQL hint
     * 
     * @return
     * @date 2016年7月20日
     * @author Ternence
     */
    public abstract String getRouteMasterHint();

    /**
     * 获取路由到Slave的SQL Hint
     * 
     * @return
     * @date 2016年7月20日
     * @author Ternence
     */
    public abstract String getRouteSlaveHint();
}
