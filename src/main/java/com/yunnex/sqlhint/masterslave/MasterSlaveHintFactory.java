package com.yunnex.sqlhint.masterslave;

import org.springframework.beans.factory.FactoryBean;

public class MasterSlaveHintFactory implements FactoryBean<MasterSlaveHint>{

    private String instanceClass;
    
    @Override
    public MasterSlaveHint getObject() throws Exception {
        Class<?> instanceClazz =  Class.forName(instanceClass);
        
        return (MasterSlaveHint) instanceClazz.newInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return MasterSlaveHint.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setInstanceClass(String instanceClass) {
        this.instanceClass = instanceClass;
    }
    
    

}
