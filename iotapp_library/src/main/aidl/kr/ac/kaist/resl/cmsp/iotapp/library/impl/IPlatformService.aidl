// IPlatformService.aidl
package kr.ac.kaist.resl.cmsp.iotapp.library.impl;
import kr.ac.kaist.resl.cmsp.iotapp.library.impl.ILocalServiceObjectHandler;
import kr.ac.kaist.resl.cmsp.iotapp.library.IGetAvailableServicesCallback;
import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceInfo;

interface IPlatformService {
    void registerClient(); // Maybe would not be used
    void sendMessage(String invocation);
    String invokeMessage(String invocation);
    List<ThingServiceInfo> getAvailableServicesNoScan(in List<String> requiredServices);
    void getAvailableServices(in List<String> requiredServices, int scanPeriod, IGetAvailableServicesCallback callback);
    void registerLocalServiceObject(in ThingServiceInfo thingInfo, in ILocalServiceObjectHandler handler);
    void unregisterLocalServiceObject(in String thingId); // Maybe we can omit handler
    boolean openSessionAndStartAdvertise();
    boolean closeSessionAndStopAdvertise();
    String getDeviceId();
}
