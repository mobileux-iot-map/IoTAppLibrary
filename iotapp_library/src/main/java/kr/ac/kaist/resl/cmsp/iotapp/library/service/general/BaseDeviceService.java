package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;


public interface BaseDeviceService {
	//public void setScannedDevice(ScannedDevice device);
	
	//public ScannedDevice getScannedDevice();
	
	public void connect();

	public void disconnect();
	
	public boolean isConnected();
	
	public void setProxyInstance(Object proxy);
}
