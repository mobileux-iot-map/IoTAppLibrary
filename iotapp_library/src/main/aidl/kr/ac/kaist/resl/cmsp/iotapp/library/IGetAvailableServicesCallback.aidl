// IGetAvailableServicesCallback.aidl
package kr.ac.kaist.resl.cmsp.iotapp.library;
import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceInfo;

oneway interface IGetAvailableServicesCallback {
	void onAvailableServiceFound(in ThingServiceInfo info);
	void onAvailableServicesScanFinished();
}