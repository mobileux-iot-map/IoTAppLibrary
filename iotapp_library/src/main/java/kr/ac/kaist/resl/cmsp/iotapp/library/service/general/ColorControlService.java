package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppException;

public interface ColorControlService extends ThingService {
	public void change_color(int i, int j, int k) throws IoTAppException;
}