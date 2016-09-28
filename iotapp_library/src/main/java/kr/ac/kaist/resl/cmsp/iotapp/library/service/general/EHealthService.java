package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

import java.util.List;


public interface EHealthService extends ThingService {
	public List<String> takeEcgValues();
	public List<String> takeEmgValues();
	public List<String> takeGsrValues();
}
