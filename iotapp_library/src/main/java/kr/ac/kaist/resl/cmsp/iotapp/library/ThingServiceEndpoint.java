package kr.ac.kaist.resl.cmsp.iotapp.library;

import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by shheo on 15. 4. 19.
 */
public class ThingServiceEndpoint extends ThingServiceInfo {
    private static final String JSON_KEY_ENDPOINT = "endpoint";
    private String endpoint = null;

    public ThingServiceEndpoint(ThingServiceInfo info) throws JSONException {
        super(info.deviceFramework, info.deviceId, info.thingId, info.thingName, info.getServices());
    }

    public ThingServiceEndpoint(String deviceFramework, String deviceId, String thingId, String thingName, JSONArray thingServices) throws JSONException {
        super(deviceFramework, deviceId, thingId, thingName, thingServices);
    }

    public ThingServiceEndpoint(String deviceFramework, String deviceId, String thingId, String thingName, List<String> thingServices) {
        super(deviceFramework, deviceId, thingId, thingName, thingServices);
    }

    @Override
    public String toString() {
        try {
            JSONObject info = new JSONObject(super.toString());
            if (endpoint != null) {
                info.put(JSON_KEY_ENDPOINT, endpoint);
            }
            return info.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
