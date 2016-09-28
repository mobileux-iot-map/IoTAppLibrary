package kr.ac.kaist.resl.cmsp.iotapp.library.invocation;

import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppService;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shheo on 15. 4. 17.
 */
public class ServiceObjectFactory {
    private static final String TAG = ServiceObjectFactory.class.getSimpleName();
    private static final String THING_INTERFACE_PACKAGE = "kr.ac.kaist.resl.cmsp.iotapp.library.service";
    private IoTAppService service;

    public ServiceObjectFactory(IoTAppService service) {
        this.service = service;
    }

    public <T extends ThingService> T getServiceObject(Class<T> serviceInterface, String thingId) {
        Set<Class<? extends ThingService>> interfaceSet = getThingInterfaceSet(serviceInterface, true);
        if (serviceInterface.isInterface()) {
            interfaceSet.add(serviceInterface);
        }
        return getServiceObject(interfaceSet, thingId);
    }

    public <T extends ThingService> T getServiceObject(Set<Class<? extends ThingService>> interfaceSet, String thingId) {
        ServiceObjectDynamicHandler handler = new ServiceObjectDynamicHandler(service, thingId);

        return (T) Proxy.newProxyInstance(
                ThingService.class.getClassLoader(),
                interfaceSet.toArray(new Class<?>[interfaceSet.size()]),
                handler);
    }

    public static Set<Class<? extends ThingService>> getThingInterfaceSet(Class<? extends ThingService> serviceInterface, boolean addThingSerivceClass) {
        Set<Class<? extends ThingService>> interfaceSet = new HashSet<>();
        for (Class<?> interfaceClazz : serviceInterface.getInterfaces()) {
            if (ThingService.class.isAssignableFrom(interfaceClazz)) {
                interfaceSet.add((Class<? extends ThingService>) interfaceClazz);
            }
        }
        if (serviceInterface.isInterface() && serviceInterface.getPackage().getName().startsWith(THING_INTERFACE_PACKAGE)) {
            interfaceSet.add(serviceInterface);
        }
        // Usually add ThingService to interface set when registering service.
        // If it is used in searching service, it will just search all services.
        if (addThingSerivceClass)
            interfaceSet.add(ThingService.class);
        return interfaceSet;
    }
}
