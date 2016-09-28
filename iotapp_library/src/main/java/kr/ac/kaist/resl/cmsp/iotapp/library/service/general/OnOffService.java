package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppException;

public interface OnOffService extends ThingService {
	public int getOnOff() throws IoTAppException;

	public void on() throws IoTAppException;

	public void off() throws IoTAppException;

	public void toggle() throws IoTAppException;
}
