package kr.ac.kaist.resl.cmsp.iotapp.library.service.general;

import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceEndpoint;
import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceInfo;

/**
 * Created by shheo on 15. 4. 17.
 */
public interface ThingService {
    String getThingId();
    String getThingName();
    void setEndpoint(ThingServiceEndpoint endpoint);
    void connect();
    void disconnect();
    Boolean isConnected();
}
