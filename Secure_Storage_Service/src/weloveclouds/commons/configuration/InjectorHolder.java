package weloveclouds.commons.configuration;

import com.google.inject.Injector;

/**
 * Created by Benoit on 2017-01-23.
 */
public class InjectorHolder {
    private Injector injector;
    private static InjectorHolder INSTANCE;

    public void hold(Injector injector) {
        this.injector = injector;
    }

    public Injector getInjector() {
        return this.injector;
    }

    public static InjectorHolder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InjectorHolder();
        }
        return INSTANCE;
    }
}
