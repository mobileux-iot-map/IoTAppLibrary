package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

import java.util.List;


public interface DeviceBridgeService extends ThingService {
	List<String> getDevices();
	void setCurrentDevice(String identifier);
}
