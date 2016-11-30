
# dubbo-sqlhint
dubbo-sqlhint是一个基于dubbo+sqlhint来实现的定制化数据库操作（比如：SQL语句路由、SQL语句改写等）


##### SQL语句路由说明：
>SQL语句路由基于数据库主从分离的场景，适用于在复杂业务情况时以业务方法粒度路由SQL到主库或者从库。
>目前内置了mycat、Atlas、ShardingJdbc数据库中间件的sql hint语法，默认为mycat 1.6.0以后的hint语法。

代码示例：
``` java
   //强制让方法内的所有SQL走主库
	@MasterOnly
    public List<String> getFromMasterDB(String id){
        //具体实现...
        return XXX
    }

   //强制让方法内的所有SQL走从库
	@SlaveOnly
    public List<String> getFromSlaveDB(String id){
        //具体实现...
        return XXX
    }

	//强制让当前链路的后续SQL走主库
	@MasterOnly(scope=Scope.REMOTE)
    public List<String> getFromSlaveDB2(String id){
        //具体实现...
        return XXX
    }
```