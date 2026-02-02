package org.confluence.terraentity.runtime;

import org.confluence.terraentity.runtime.dev.DevOnlyInterceptor;
import org.confluence.terraentity.runtime.dev.IDevWrapper;

public class TERuntime {

    static volatile TERuntime instance;
    IDevWrapper devProxy;


    public static TERuntime getInstance() {
        if (instance == null) {
            synchronized (TERuntime.class) {
                if (instance == null) {
                    instance = new TERuntime();
                }
            }
        }
        return instance;
    }


    public TERuntime() {

        devProxy = DevOnlyInterceptor.createProxy(new IDevWrapper(){}, IDevWrapper.class);


    }

    public void start(){


    }

    public IDevWrapper getDevProxy(){
        return devProxy;
    }

    public static boolean isDevMode(){
        return getInstance().devProxy.isDevMode();
    }


}
