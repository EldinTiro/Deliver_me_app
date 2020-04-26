package com.StoreApp.WS;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationsContext implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.CONTEXT = context;
    }

    public static Object getBean(String beanName) {
        return CONTEXT.getBean(beanName);
    }
}
