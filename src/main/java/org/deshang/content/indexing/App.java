package org.deshang.content.indexing;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    private static final String APP_ROOT_PACKAGE_NAME = App.class.getPackage().getName();
    public static final String APP_CONFIG_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".config";
    public static final String APP_REPOSITORY_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".repository.impl";
    public static final String APP_SERVICE_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".service.impl";
    public static final String APP_MODEL_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".model";

    public static final String APP_ACTIVE_PROFILE_NAMES = "default";

    private AnnotationConfigApplicationContext context;

    private static class AppHolder {
        private final static App instance = new App();
    }

    private App() {
        context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("APP_ACTIVE_PROFILE_NAMES");
        context.scan(APP_CONFIG_PACKAGE_NAME, APP_SERVICE_PACKAGE_NAME, APP_REPOSITORY_PACKAGE_NAME);
        context.refresh();
    }

    public static ApplicationContext getContext() {
        return AppHolder.instance.context;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = App.getContext();
        
        if (ctx != null) {
            System.out.println("Appliction context is loaded!");
        }
    }
}
