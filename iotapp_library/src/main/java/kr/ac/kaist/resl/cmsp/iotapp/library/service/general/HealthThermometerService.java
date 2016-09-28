package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

public interface HealthThermometerService {
	public String getTemperatureMeasurement();
	public String getTemperatureType();
	public String getIntermediateTemperature();
	public String getMeasurementInterval();
}
