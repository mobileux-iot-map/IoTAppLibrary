package kr.ac.kaist.resl.cmsp.iotapp.library.impl;

import android.content.*;
import android.os.*;
import android.util.Log;
import kr.ac.kaist.resl.cmsp.iotapp.library.IGetAvailableServicesCallback;
import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppService;
import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceInfo;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.MethodInvocationJsonObject;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.ServiceObjectFactory;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;
import kr.ac.kaist.resl.cmsp.iotapp.library.utils.ServiceFinder;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by shheo on 15. 4. 17.
 */

public class AndroidIoTAppService implements IoTAppService {
    private static final String PLATFORM_SERVICE_PACKAGE = "kr.ac.kaist.resl.cmsp.iotapp.platform";
    private static final String PLATFORM_SERVICE_NAME = PLATFORM_SERVICE_PACKAGE + ".PlatformService";
    private boolean isBound = false;
    IoTAppServiceCallback boundCallback;
    private IPlatformService platformService;
    // APP_ID is randomly generated when this class is firstly instantiated
    protected static UUID APP_ID = null;
    protected String appName;
    protected Context appContext;
    private boolean sessionOpenedByThis = false;
    public final static Pattern URN_PATTERN = Pattern.compile(
            "^urn:[a-z0-9][a-z0-9-]{0,31}:([a-z0-9()+,\\-.:=@;$_!*']|%[0-9a-f]{2})+$",
            Pattern.CASE_INSENSITIVE);
    private static final String TAG = "AndroidIoTAppService";
    ServiceObjectFactory serviceObjectFactory;
    private static Map<String, ThingService> localAppServiceObjectMap;
    private static Map<String, ILocalServiceObjectHandler> localAppServiceObjectHandlerMap;

    public AndroidIoTAppService(Context context) {
        appContext = context;
        this.serviceObjectFactory = new ServiceObjectFactory(this);
        try {
            appName = context.getString(context.getApplicationInfo().labelRes);
        } catch (NullPointerException ex) {
            appName = "IoTAppTestProject";
        }

        SharedPreferences prefs = context.getSharedPreferences("AndroidIoTAppService", Context.MODE_PRIVATE);
        String uuidStr = prefs.getString("APP_ID", null);
        if (uuidStr == null) {
            APP_ID = UUID.randomUUID();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("APP_ID", APP_ID.toString());
            editor.apply();
        } else {
            APP_ID = UUID.fromString(uuidStr);
        }
        localAppServiceObjectMap = new HashMap<>();
        localAppServiceObjectHandlerMap = new HashMap<>();
    }

    @Override
    public void connectPlatform(IoTAppServiceCallback callback) {
        // The callback can be null
        boundCallback = callback;
        Log.d(TAG, "Connecting to PlatformService");
        // Should attempt connection in new thread, or it will hang forever
        new Thread(new Runnable() {
            @Override
            public void run() {
                // We bind to the service
                Intent intentForPlatformService = new Intent();
                intentForPlatformService.setComponent(new ComponentName(PLATFORM_SERVICE_PACKAGE, PLATFORM_SERVICE_NAME));
                if (!appContext.bindService(intentForPlatformService, sConn, Context.BIND_IMPORTANT)) {
                    Log.d(TAG, "Cannot bind to PlatformService");
                    if (boundCallback != null)
                        boundCallback.onServiceConnectionFailed();
                }
            }
        }).start();
    }

    @Override
    public void disconnectPlatform() throws RemoteException {
        for (Map.Entry<String, ThingService> entry : localAppServiceObjectMap.entrySet()) {
            ThingService serviceObj = entry.getValue();
            unregisterLocalServiceObject(serviceObj);
        }
        if (sessionOpenedByThis)
            closeSession();
        sessionOpenedByThis = false;
        if (isBound) {
            appContext.unbindService(sConn);
            Log.d(TAG, "Unbound to PlatformService");
            isBound = false;
        }
    }

    @Override
    public int openSession() {
        try {
            sessionOpenedByThis = true;
            platformService.openSessionAndStartAdvertise();
        } catch (RemoteException e) {
            sessionOpenedByThis = false;
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int closeSession() {
        try {
            sessionOpenedByThis = false;
            platformService.closeSessionAndStopAdvertise();
        } catch (RemoteException e) {
            sessionOpenedByThis = false;
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void registerLocalServiceObject(Set<Class<? extends ThingService>> interfaceSet, ThingService serviceObj) throws RemoteException {
        List<String> serviceList = new ArrayList<>();
        for (Class<? extends ThingService> iface : interfaceSet) {
            serviceList.add(iface.getSimpleName());
        };
        ThingServiceInfo info = new ThingServiceInfo(ThingServiceInfo.DEVICEFRAMEWORK_APP,
                platformService.getDeviceId(),
                serviceObj.getThingId(),
                serviceObj.getThingName(),
                serviceList);
        ILocalServiceObjectHandler.Stub handler = new ILocalServiceObjectHandlerImpl();
        platformService.registerLocalServiceObject(info, handler);
        localAppServiceObjectMap.put(serviceObj.getThingId(), serviceObj);
        localAppServiceObjectHandlerMap.put(serviceObj.getThingId(), handler);
    }

    @Override
    public void registerLocalServiceObject(Class<? extends ThingService> clazz, ThingService serviceObj) throws RemoteException {
        // TODO: need error handling when registering is failed
        Set<Class<? extends ThingService>> interfaceSet = ServiceObjectFactory.getThingInterfaceSet(clazz, true);
        registerLocalServiceObject(interfaceSet, serviceObj);
    }

    @Override
    public void unregisterLocalServiceObject(ThingService serviceObj) throws RemoteException {
        // TODO: need error handling when unregistering is failed
        platformService.unregisterLocalServiceObject(serviceObj.getThingId());
        localAppServiceObjectMap.remove(serviceObj.getThingId());
        localAppServiceObjectHandlerMap.remove(serviceObj.getThingId());
    }

    @Override
    public List<ThingServiceInfo> getAvailableServicesNoScan(String serviceName) throws ClassNotFoundException, RemoteException {
        return getAvailableServicesNoScan(ServiceFinder.findService(serviceName));
    }

    @Override
    public List<ThingServiceInfo> getAvailableServicesNoScan(Class<? extends ThingService> clazz) throws RemoteException {
        if (!clazz.isInterface() || !ThingService.class.isAssignableFrom(clazz)) {
            Log.e(TAG, "Class " + clazz.getSimpleName() + " is not interface or does not extend ThingService");
            return null;
        }
        Set<Class<? extends ThingService>> interfaceSet = ServiceObjectFactory.getThingInterfaceSet(clazz, false);
        List<String> serviceList = new ArrayList<>();
        for (Class<? extends ThingService> iface : interfaceSet) {
            serviceList.add(iface.getSimpleName());
        };
        return platformService.getAvailableServicesNoScan(serviceList);
    }

    @Override
    public void getAvailableServices(String serviceName, int scanPeriod, IGetAvailableServicesCallback callback) throws ClassNotFoundException, RemoteException {
        getAvailableServices(ServiceFinder.findService(serviceName), scanPeriod, callback);
    }

    @Override
    public void getAvailableServices(Class<? extends ThingService> clazz, int scanPeriod, IGetAvailableServicesCallback callback) throws RemoteException {
        if (!clazz.isInterface() || !ThingService.class.isAssignableFrom(clazz)) {
            Log.e(TAG, "Class " + clazz.getSimpleName() + " is not interface or does not extend ThingService");
            return;
        }
        Set<Class<? extends ThingService>> interfaceSet = ServiceObjectFactory.getThingInterfaceSet(clazz, false);
        List<String> serviceList = new ArrayList<>();
        for (Class<? extends ThingService> iface : interfaceSet) {
            serviceList.add(iface.getSimpleName());
        };
        platformService.getAvailableServices(serviceList, scanPeriod, callback);
    }

    @Override
    public <T extends ThingService> T getServiceObject(Class<T> clazz, String thingId) {
        return serviceObjectFactory.getServiceObject(clazz, thingId);
    }

    @Override
    public <T extends ThingService> T getServiceObject(Set<Class<? extends ThingService>> interfaceSet, String thingId) {
        return serviceObjectFactory.getServiceObject(interfaceSet, thingId);
    }

    @Override
    public void handleServiceInvocation(JSONObject object) {
        // FIXME: This method should not be visible out of the library. Move this somewhere.
        try {
            platformService.sendMessage(object.toString());
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to send message to platform");
            e.printStackTrace();
        }
    }

    @Override
    public Object handleServiceInvocationWithReturn(JSONObject object) {
        // FIXME: This method should not be visible out of the library. Move this somewhere.
        try {
            String returnString = platformService.invokeMessage(object.toString());
            String returnTypeClassName = object.getString(MethodInvocationJsonObject.JSON_KEY_RETURN_TYPE);
            Log.d(TAG, "Preparing return value: type: " + returnTypeClassName);
            return MethodInvocationJsonObject.cast(returnTypeClassName, returnString);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to send message to platform");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        disconnectPlatform();
        Thread.sleep(2000);
        super.finalize();
    }

    private ServiceConnection sConn = new ServiceConnection() {
        @Override
        public synchronized void onServiceConnected(ComponentName name, IBinder service) {
            platformService = IPlatformService.Stub.asInterface(service);
            Log.d(TAG, "Successfully bound to PlatformService");
            isBound = true;
            if (boundCallback != null)
                boundCallback.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            platformService = null;
            Log.d(TAG, "Disconnected from the PlatformService");
            isBound = false;
            if (boundCallback != null)
                boundCallback.onServiceDisconnected();
        }
    };

    public class ILocalServiceObjectHandlerImpl extends ILocalServiceObjectHandler.Stub {

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public String callback(String invocationStr, boolean isReturnable) throws RemoteException {
            Log.d(TAG, "Received callback from service!: " + invocationStr);
            Object toReturn = null;
            try {
                MethodInvocationJsonObject invocation = new MethodInvocationJsonObject(invocationStr);
                ThingService thingServiceObj = localAppServiceObjectMap.get(invocation.getThingId());
                Method method;
                String[] typeArray = invocation.getTypeArray();
                String[] paramArray = invocation.getParamArray();
                Object[] paramObjArray = new Object[paramArray.length];
                for (int i = 0; i < paramArray.length; i++) {
                    paramObjArray[i] = MethodInvocationJsonObject.cast(typeArray[i], paramArray[i]);
                }

                Log.d(TAG, "ParamCount: " + invocation.getParamCount() + ", ret: " + invocation.getReturnType());
                for (Object o : paramObjArray) {
                    Log.d(TAG, o.toString());
                }
                switch (invocation.getParamCount()) {
                    case 0:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName());
                        toReturn = method.invoke(thingServiceObj);
                        break;
                    case 1:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName(),
                                MethodInvocationJsonObject.getClass(typeArray[0]));
                        toReturn = method.invoke(thingServiceObj,
                                paramObjArray[0]);
                        break;
                    case 2:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName(),
                                MethodInvocationJsonObject.getClass(typeArray[0]), MethodInvocationJsonObject.getClass(typeArray[1]));
                        toReturn = method.invoke(thingServiceObj,
                                paramObjArray[0], paramObjArray[1]);
                        break;
                    case 3:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName(),
                                MethodInvocationJsonObject.getClass(typeArray[0]), MethodInvocationJsonObject.getClass(typeArray[1]),
                                MethodInvocationJsonObject.getClass(typeArray[2]));
                        toReturn = method.invoke(thingServiceObj,
                                paramObjArray[0], paramObjArray[1],
                                paramObjArray[2]);
                        break;
                    case 4:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName(),
                                MethodInvocationJsonObject.getClass(typeArray[0]), MethodInvocationJsonObject.getClass(typeArray[1]),
                                MethodInvocationJsonObject.getClass(typeArray[2]), MethodInvocationJsonObject.getClass(typeArray[3]));
                        toReturn = method.invoke(thingServiceObj,
                                paramObjArray[0], paramObjArray[1],
                                paramObjArray[2], paramObjArray[3]);
                        break;
                    case 5:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName(),
                                MethodInvocationJsonObject.getClass(typeArray[0]), MethodInvocationJsonObject.getClass(typeArray[1]),
                                MethodInvocationJsonObject.getClass(typeArray[2]), MethodInvocationJsonObject.getClass(typeArray[3]),
                                MethodInvocationJsonObject.getClass(typeArray[4]));
                        toReturn = method.invoke(thingServiceObj,
                                paramObjArray[0], paramObjArray[1],
                                paramObjArray[2], paramObjArray[3],
                                paramObjArray[4]);
                        break;
                    case 6:
                        method = thingServiceObj.getClass().getMethod(invocation.getMethodName(),
                                MethodInvocationJsonObject.getClass(typeArray[0]), MethodInvocationJsonObject.getClass(typeArray[1]),
                                MethodInvocationJsonObject.getClass(typeArray[2]), MethodInvocationJsonObject.getClass(typeArray[3]),
                                MethodInvocationJsonObject.getClass(typeArray[4]), MethodInvocationJsonObject.getClass(typeArray[5]));
                        toReturn = method.invoke(thingServiceObj,
                                paramObjArray[0], paramObjArray[1],
                                paramObjArray[2], paramObjArray[3],
                                paramObjArray[4], paramObjArray[5]);
                        break;
                    default:
                        Log.e(TAG, "Methods with " + invocation.getParamCount() + " parameters is not supported");
                }
                if (toReturn != null) {
                    Log.d(TAG, "1Return value is " + toReturn);
                    Log.d(TAG, "2Return value is " + toReturn.toString());
                    Log.d(TAG, "3Return value is " + MethodInvocationJsonObject.cast(MethodInvocationJsonObject.getClass(invocation.getReturnType()), toReturn));
                }
            } catch (JSONException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (isReturnable && toReturn != null) {
                return toReturn.toString();
            } else {
                return null;
            }
        }
    }
}
