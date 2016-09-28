package kr.ac.kaist.resl.cmsp.iotapp.library.unity;

import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppService;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.ServiceObjectDynamicHandler;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.ServiceObjectFactory;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;

import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * Created by shheo on 16. 3. 18.
 */
public class UnityServiceObjectFactory {
    private static final String TAG = UnityServiceObjectFactory.class.getSimpleName();

    public static <T extends ThingService> T getUnityServiceObject(Set<Class<? extends ThingService>> interfaceSet, String gameObjectName) {
        UnityServiceObjectDynamicHandler handler = new UnityServiceObjectDynamicHandler(gameObjectName);

        return (T) Proxy.newProxyInstance(
                ThingService.class.getClassLoader(),
                interfaceSet.toArray(new Class<?>[interfaceSet.size()]),
                handler);
    }
}
