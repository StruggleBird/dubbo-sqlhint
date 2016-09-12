package com.yunnex.sqlhint.masterslave;

import com.dangdang.ddframe.rdb.sharding.api.HintManager;

/**
 * sjdbc的 thread hint
 * @author Ternence
 * @date 2016年9月2日
 */
public class ShardingJdbcHint extends MasterSlaveHint {

    
    
    @Override
    public String getRouteMasterHint() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        return null;
    }

    @Override
    public String getRouteSlaveHint() {
        return null;
    }

    @Override
    public String genRouteInfo(String dbRole, String sql) {
        if (MASTER.equals(dbRole)) {            
            getRouteMasterHint(); //基于 thread的hint
        }
        return sql;
    }

}
