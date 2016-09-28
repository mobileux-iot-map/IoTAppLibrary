package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

/**
 * Created by shheo on 15. 4. 17.
 */
public interface StringProcessor extends ThingService {
    void processStringNoReturn(String str);
    String processStringWithReturn(String str);
}
