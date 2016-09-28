package kr.ac.kaist.resl.cmsp.iotapp.library;

import android.os.RemoteException;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Created by shheo on 15. 4. 18.
 */
public interface IoTAppService {
    /**
     * Connect to IoT-App Platform Service
     */
    void connectPlatform(IoTAppServiceCallback callback);

    /**
     * Disconnect from IoT-App Platform Service
     */
    void disconnectPlatform() throws RemoteException;

    /**
     * Open a cluster session (i.e. Become a central server of cluster)
     * @return If the session is successfully opened, returns 0. Otherwise, -1.
     */
    int openSession();

    /**
     * Close a cluster session (i.e. Get out of the cluster. Other devices are still connected, but new device cannot join)
     * @return If the session is successfully closed, returns 0. Otherwise, -1.
     */
    int closeSession();

    /**
     * Register local object which implements ThingService to the IoT-App Platform Service.
     * @param interfaceSet Set of interfaces (extends ThingService) which the local object implement
     * @param serviceObj Instantiated implementation object
     */
    void registerLocalServiceObject(Set<Class<? extends ThingService>> interfaceSet, ThingService serviceObj) throws RemoteException;

    /**
     * Register local object which implements ThingService to the IoT-App Platform Service.
     * @param clazz Interface (extends ThingService) which the local object implements
     * @param serviceObj Instantiated implementation object
     */
    void registerLocalServiceObject(Class<? extends ThingService> clazz, ThingService serviceObj) throws RemoteException;

    /**
     * Unregister local object from the IoT-App Platform Service.
     * @param serviceObj Instantiated implementation object
     */
    void unregisterLocalServiceObject(ThingService serviceObj) throws RemoteException;

    /**
     * Get available thing services throughout the whole cluster
     * @param serviceName Simple name of the service interface to search
     * @return List of ThingServiceInfo objects which contains service information.
     */
    List<ThingServiceInfo> getAvailableServicesNoScan(String serviceName) throws ClassNotFoundException, RemoteException;

    /**
     * Get available thing services throughout the whole cluster
     * @param clazz Service interface to search
     * @return List of ThingServiceInfo objects which contains service information.
     */
    List<ThingServiceInfo> getAvailableServicesNoScan(Class<? extends ThingService> clazz) throws RemoteException;

    /**
     * Get available thing services throughout the whole cluster
     * @param serviceName Simple name of the service interface to search
     * @param scanPeriod If >0, each devices in the cluster scans available devices. Otherwise, just return current list.
     */
    void getAvailableServices(String serviceName, int scanPeriod, IGetAvailableServicesCallback callback) throws ClassNotFoundException, RemoteException;

    /**
     * Get available thing services throughout the whole cluster
     * @param clazz Service interface to search
     * @param scanPeriod If >0, each devices in the cluster scans available devices. Otherwise, just return current list.
     */
    void getAvailableServices(Class<? extends ThingService> clazz, int scanPeriod, IGetAvailableServicesCallback callback) throws RemoteException;

    /**
     * Retrieve service object using thing id. That service object can be used as if it is actual thing.
     * @param clazz Service interface of the service to request.
     * @param thingId Thing id of the service to request.
     * @return Service object which is connected to actual thing device/impl.
     */
    <T extends ThingService> T getServiceObject(Class<T> clazz, String thingId);
    /**
     * Retrieve service object using thing id. That service object can be used as if it is actual thing.
     * @param interfaceSet Set of service interfaces of the service to request.
     * @param thingId Thing id of the service to request.
     * @return Service object which is connected to actual thing device/impl.
     */
    <T extends ThingService> T getServiceObject(Set<Class<? extends ThingService>> interfaceSet, String thingId);

    // Not used. Just for test.
    void handleServiceInvocation(JSONObject object);
    // Not used. Just for test.
    Object handleServiceInvocationWithReturn(JSONObject object);


    interface IoTAppServiceCallback {
        void onServiceConnected();
        void onServiceConnectionFailed();
        void onServiceDisconnected();
    }
}
