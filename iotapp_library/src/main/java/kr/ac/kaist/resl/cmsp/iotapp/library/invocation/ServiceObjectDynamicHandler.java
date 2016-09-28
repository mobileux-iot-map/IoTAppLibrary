package kr.ac.kaist.resl.cmsp.iotapp.library.invocation;

import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by shheo on 15. 4. 17.
 */
public class ServiceObjectDynamicHandler implements InvocationHandler {
    private static final String TAG = ServiceObjectDynamicHandler.class.getSimpleName();
    private IoTAppService service;
    private String thingId;
    public ServiceObjectDynamicHandler (IoTAppService service, String thingId) {
        this.service = service;
        this.thingId = thingId;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        MethodInvocationJsonObject object = new MethodInvocationJsonObject();
        object.setData(thingId, method, args, method.getReturnType());
        // TODO: Send message to platform service
        if (method.getReturnType().equals(Void.TYPE)) {
            service.handleServiceInvocation(object);
            return null;
        } else {
            return service.handleServiceInvocationWithReturn(object);
        }
    }

}
