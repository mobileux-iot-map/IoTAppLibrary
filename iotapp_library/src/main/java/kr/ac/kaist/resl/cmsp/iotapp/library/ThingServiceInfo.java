package kr.ac.kaist.resl.cmsp.iotapp.library;

import android.os.Parcel;
import android.os.Parcelable;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by shheo on 15. 4. 19.
 */
public class ThingServiceInfo implements Parcelable {
    public static final String JSON_KEY_DF = "device_framework";
    public static final String JSON_KEY_DEVICE_ID = "device_id";
    public static final String JSON_KEY_THING_ID = "thing_id";
    public static final String JSON_KEY_THING_NAME = "thing_name";
    public static final String JSON_KEY_SERVICES = "services";
    public static final String DEVICEFRAMEWORK_BLE = "ble";
    public static final String DEVICEFRAMEWORK_BT = "bluetooth";
    public static final String DEVICEFRAMEWORK_UPNP = "upnp";
    public static final String DEVICEFRAMEWORK_APP = "app";
    public static final String DEVICEFRAMEWORK_ANTPLUS = "ant+";

    protected String deviceFramework;
    protected String deviceId;
    protected String thingId;
    protected String thingName;
    private Set<String> services = null;

    public ThingServiceInfo(String deviceFramework, String deviceId, String thingId, String thingName, Set<String> thingServices) {
        this.deviceFramework = deviceFramework;
        this.deviceId = deviceId;
        this.thingId = thingId;
        this.thingName = thingName;
        this.services = new HashSet<>(thingServices);
    }

    public ThingServiceInfo(String deviceFramework, String deviceId, String thingId, String thingName, List<String> thingServices) {
        this.deviceFramework = deviceFramework;
        this.deviceId = deviceId;
        this.thingId = thingId;
        this.thingName = thingName;
        this.services = new HashSet<>(thingServices);
    }

    public ThingServiceInfo(String deviceFramework, String deviceId, String thingId, String thingName, JSONArray thingServices) throws JSONException {
        this.deviceFramework = deviceFramework;
        this.deviceId = deviceId;
        this.thingId = thingId;
        this.thingName = thingName;
        this.services = new HashSet<>();
        for (int i = 0;i < thingServices.length();i++) {
            services.add(thingServices.getString(i));
        }
    }

    public ThingServiceInfo(JSONObject info) throws JSONException {
        this.deviceFramework = info.getString(JSON_KEY_DF);
        this.deviceId = info.getString(JSON_KEY_DEVICE_ID);
        this.thingId = info.getString(JSON_KEY_THING_ID);
        this.thingName = info.getString(JSON_KEY_THING_NAME);
        JSONArray serviceArray = info.getJSONArray(JSON_KEY_SERVICES);
        this.services = new HashSet<>();
        for (int i = 0;i < serviceArray.length();i++) {
            services.add(serviceArray.getString(i));
        }
    }


    public String getDeviceFramework() {
        return deviceFramework;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public String  getThingId() {
        return thingId;
    }
    public String  getThingName() {
        return thingName;
    }

    @Override
    public String toString() {
        JSONObject info = new JSONObject();
        try {
            info.put(JSON_KEY_DF, deviceFramework);
            info.put(JSON_KEY_DEVICE_ID, deviceId);
            info.put(JSON_KEY_THING_ID, thingId);
            info.put(JSON_KEY_THING_NAME, thingName);
            JSONArray servicesArray = new JSONArray(services);
            info.put(JSON_KEY_SERVICES, servicesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info.toString();
    }

    public Set<String> getServices() {
        return services;
    }

    public void addService(String service) {
        services.add(service);
    }

    public void removeService(String service) {
        services.remove(service);
    }


    protected ThingServiceInfo(Parcel in) {
        deviceFramework = in.readString();
        deviceId = in.readString();
        thingId = in.readString();
        thingName = in.readString();
        List<String> serviceList = new ArrayList<>();
        in.readStringList(serviceList);
        services = new HashSet<>(serviceList);
    }

    public static final Creator<ThingServiceInfo> CREATOR = new Creator<ThingServiceInfo>() {
        @Override
        public ThingServiceInfo createFromParcel(Parcel in) {
            return new ThingServiceInfo(in);
        }

        @Override
        public ThingServiceInfo[] newArray(int size) {
            return new ThingServiceInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceFramework);
        dest.writeString(deviceId);
        dest.writeString(thingId);
        dest.writeString(thingName);
        dest.writeStringList(new ArrayList<>(services));
    }
}
