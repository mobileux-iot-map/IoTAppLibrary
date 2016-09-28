package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

public interface HeartRateService extends ThingService {
	
	String getHeartRateMeasurement();
	String getEnergyExpended();
	String getRrInterval();
	
	/** Body Sensor Location **/
	String getBodySensorLocation();
	
	/** Heart Rate Control Point **/
	void setEnergyExpendedResetted();
}
