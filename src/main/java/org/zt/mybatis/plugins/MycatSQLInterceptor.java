package org.zt.mybatis.plugins;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.zt.utils.ReflectionUtil;

import com.alibaba.dubbo.rpc.RpcContext;

/**
 * 利用mybatis拦截器实现 @readonly 的sql路由到slave的功能
 * 
 * @author Ternence
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class MycatSQLInterceptor implements Interceptor {

    private static final String MYCAT_DB_MASTER_HINT = "/*#mycat:db_type=master*/";
    private static final String MYCAT_DB_SLAVE_HINT = "/*#mycat:db_type=slave*/";
    private static final String ROUTER_KEY = "_mysqlrouter";

    public Object intercept(Invocation invocation) throws Throwable {
        Object result = null;

        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
            StatementHandler delegate = (StatementHandler) ReflectionUtil.getFieldValue(statementHandler, "delegate");
            BoundSql boundSql = delegate.getBoundSql();

            // 拦截到的prepare方法参数是一个Connection对象
            Connection connection = (Connection) invocation.getArgs()[0];
            String sql = boundSql.getSql();
            Map<String, String> attachments = RpcContext.getContext().getAttachments();

            if (attachments.containsKey(ROUTER_KEY) && !hasAnnotation(sql)) {
                sql = new StringBuilder(attachments.get(ROUTER_KEY)).append(sql).toString();

                // 利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
                ReflectionUtil.setFieldValue(boundSql, "sql", sql);
            }

            // 针对只读事物添加读取从库的路由
            if (!hasAnnotation(sql) && connection.isReadOnly()) {

                // 获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
                sql = new StringBuilder(MYCAT_DB_SLAVE_HINT).append(sql).toString();

                // 利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
                ReflectionUtil.setFieldValue(boundSql, "sql", sql);
            }


            result = invocation.proceed();

            if (isDML(sql) && !attachments.containsKey(ROUTER_KEY)) {
                // 如果更改执行的是DML语句，则设置后续的CRUD路由到master
                attachments.put(ROUTER_KEY, MYCAT_DB_MASTER_HINT);

            }

        } else {
            result = invocation.proceed();
        }



        return result;
    }


    /**
     * 判断是否是DML语句
     * 
     * @param sql
     * @return
     * @date 2016年7月14日
     * @author Ternence
     */
    private boolean isDML(String sql) {
        if (hasAnnotation(sql)) {
            sql = sql.replaceFirst("/\\*.+\\*/", "").trim();
        }
        sql = sql.toLowerCase();
        if (sql.startsWith("update") || sql.startsWith("insert")) {
            return true;
        }
        return false;
    }

    private boolean hasAnnotation(String sql) {
        if (sql.startsWith("/*")) {
            return true;
        }
        return false;
    }


    /**
     * 拦截器对应的封装原始对象的方法
     */
    public Object plugin(Object arg0) {
        if (arg0 instanceof StatementHandler) {
            return Plugin.wrap(arg0, this);
        } else {
            return arg0;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
