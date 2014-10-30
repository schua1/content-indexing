/*
 * Copyright 2014 Deshang group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.deshang.content.indexing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    private static final String APP_ROOT_PACKAGE_NAME = App.class.getPackage().getName();
    public static final String APP_CONFIG_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".config";
    public static final String APP_REPOSITORY_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".repository.impl";
    public static final String APP_SERVICE_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".service.impl";
    public static final String APP_MODEL_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".model";

    public static final String APP_ACTIVE_PROFILE_NAMES = "default";

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private AnnotationConfigApplicationContext context;

    private static class AppHolder {
        private final static App instance = new App();
    }

    private App() {
        context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(APP_ACTIVE_PROFILE_NAMES);
        context.scan(APP_CONFIG_PACKAGE_NAME, APP_SERVICE_PACKAGE_NAME, APP_REPOSITORY_PACKAGE_NAME);
        context.refresh();
    }

    public static ApplicationContext getContext() {
        return AppHolder.instance.context;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = App.getContext();
        
        if (ctx != null) {
            LOGGER.info("Appliction context is loaded!");
        }
    }
}
