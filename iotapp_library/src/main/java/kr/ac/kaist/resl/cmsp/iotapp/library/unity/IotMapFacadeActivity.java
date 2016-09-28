package kr.ac.kaist.resl.cmsp.iotapp.library.unity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import kr.ac.kaist.resl.cmsp.iotapp.library.IGetAvailableServicesCallback;
import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppService;
import kr.ac.kaist.resl.cmsp.iotapp.library.ThingServiceInfo;
import kr.ac.kaist.resl.cmsp.iotapp.library.impl.AndroidIoTAppService;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.MethodInvocationJsonObject;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ButtonService;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.GestureService;
import kr.ac.kaist.resl.cmsp.iotapp.library.service.general.ThingService;
import kr.ac.kaist.resl.cmsp.iotapp.library.utils.ServiceFinder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shheo on 15. 8. 19.
 */
public class IotMapFacadeActivity extends UnityPlayerActivity {
    private static final String TAG = "UnityMainActivity";
    public static ConcurrentHashMap<Integer, UnityServiceObjectDynamicHandler> unityProxyMap = new ConcurrentHashMap<>();
    IoTAppService service;
    ThingService unityServiceObject = null;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        service = new AndroidIoTAppService(this.getApplicationContext());
    }

    public void connectPlatform() {
        service.connectPlatform(new IoTAppService.IoTAppServiceCallback() {
            @Override
            public void onServiceConnected() {
                UnityPlayer.UnitySendMessage("IotMapFacade", "connectPlatformCallback", "onServiceConnected");
            }

            @Override
            public void onServiceConnectionFailed() {
                UnityPlayer.UnitySendMessage("IotMapFacade", "connectPlatformCallback", "onServiceConnectionFailed");
            }

            @Override
            public void onServiceDisconnected() {
                UnityPlayer.UnitySendMessage("IotMapFacade", "connectPlatformCallback", "onServiceDisconnected");
            }
        });
    }

    public void registerLocalServiceObject(final String gameObjectName, final String... serviceNames) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<Class<? extends ThingService>> interfaceSet = new HashSet<>();
                for (String serviceName : serviceNames) {
                    try {
                        interfaceSet.add(ServiceFinder.findService(serviceName));
                    } catch (ClassNotFoundException e) {
                        Log.d(TAG, "Service name " + serviceName + " does not exist.");
                    }
                }
                interfaceSet.add(ThingService.class);
                unityServiceObject = UnityServiceObjectFactory.getUnityServiceObject(interfaceSet, gameObjectName);
                try {
                    service.registerLocalServiceObject(interfaceSet, unityServiceObject);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void unregisterLocalServiceObject() {
        try {
            service.unregisterLocalServiceObject(unityServiceObject);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setReturnToServiceObject(int key, String toReturn) {
        unityProxyMap.get(key).setReturn(toReturn);
    }

    public void openSession() {
        service.openSession();
    }

    public String getAvailableServicesNoScan(String serviceName, int scanPeriod) {

        try {
            List<ThingServiceInfo> availableServices = service.getAvailableServicesNoScan(serviceName);
            JSONArray toReturn = new JSONArray();
            for (ThingServiceInfo info : availableServices) {
                toReturn.put(new JSONObject(info.toString()));
            }
            return toReturn.toString();
        } catch (RemoteException | JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            UnityPlayer.UnitySendMessage("IotMapFacade", "onScanCallback", "ERROR: No service named " + serviceName);
        }
        return null;
    }

    public void getAvailableServices(String serviceName, int scanPeriod, final String gameObjectCallbackName) {
        try {
            service.getAvailableServices(serviceName, scanPeriod, new IGetAvailableServicesCallback.Stub() {
                @Override
                public void onAvailableServiceFound(ThingServiceInfo info) throws RemoteException {
                    UnityPlayer.UnitySendMessage("IotMapFacade", "onScanCallback", info.toString());
                    UnityPlayer.UnitySendMessage(gameObjectCallbackName, "onScanCallback", info.toString());
                }

                @Override
                public void onAvailableServicesScanFinished() throws RemoteException {
                    UnityPlayer.UnitySendMessage("IotMapFacade", "scanFinishedCallback", "");
                    UnityPlayer.UnitySendMessage(gameObjectCallbackName, "scanFinishedCallback", "");
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            UnityPlayer.UnitySendMessage("IotMapFacade", "onScanCallback", "ERROR: No service named " + serviceName);
        }
    }

    public String invokeServiceObject(String[] services, String thingId, String methodName, boolean waitReturn, String... argsStr) {
        Log.d(TAG, "invokeServiceObject: " + thingId + ":" + methodName);
        try {
            Set<Class<? extends ThingService>> interfaceSet = new HashSet<>();
            for (String serviceName : services) {
                try {
                    interfaceSet.add(ServiceFinder.findService(serviceName));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            final ThingService serviceObj = service.getServiceObject(interfaceSet, thingId);
            Method[] methods = serviceObj.getClass().getMethods();
            Method targetMethod = null;
            for (Method m : methods) {
                if (m.getName().equals(methodName)) {
                    targetMethod = m;
                    break;
                }
            }
            if (targetMethod == null)
                return null;
            final Object[] args = new Object[argsStr.length];
            Class<?>[] argTypes = targetMethod.getParameterTypes();
            for (int i = 0;i < argsStr.length;i++) {
                args[i] = MethodInvocationJsonObject.cast(argTypes[i], argsStr[i]);
            }
            if (waitReturn) {
                return targetMethod.invoke(serviceObj, args).toString();
            } else {
                final Method finalTargetMethod = targetMethod;
                // Fire & Forget
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalTargetMethod.invoke(serviceObj, args);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return null;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "method not found: " + thingId + ":" + methodName);
        return null;
    }

    public void showSelectDialog() {
        final Activity a = UnityPlayer.currentActivity;
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String items[] = {"item1", "item2", "item3"};
                AlertDialog.Builder ab = new AlertDialog.Builder(IotMapFacadeActivity.this);
                ab.setTitle("Title");
                ab.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 각 리스트를 선택했을때
                            }
                        }).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Cancel 버튼 클릭시
                            }
                        });
                ab.show();
            }
        });
    }

    List<ThingServiceInfo> deviceList = new ArrayList<>();
    List<ThingService> deviceObjList = new ArrayList<>();
    List<Float> deviceDataList = new ArrayList<>(Arrays.asList(0.0f, 0.0f));
    List<ThingServiceInfo> justScannedDeviceList;
    int currentScanningDevice;

    public int getScannedDevicesCount() {
        return justScannedDeviceList.size();
    }

    public String getScannedDeviceName(int index) {
        return justScannedDeviceList.get(index).getThingName();
    }

    ButtonService buttonObj;
    GestureService gestureObj;

    public void selectDevice(final int index) {
        if (currentScanningDevice == 0) {
            //ButtonService targetObj = service.getServiceObject(ButtonService.class, "34:B1:F7:D5:0C:01");
            deviceList.set(0, justScannedDeviceList.get(index));
            buttonObj = service.getServiceObject(ButtonService.class, justScannedDeviceList.get(index).getThingId());
        } else {
            //GestureService targetObj = service.getServiceObject(GestureService.class, "D3:4D:45:50:C9:D1");
            deviceList.set(1, justScannedDeviceList.get(index));
            gestureObj = service.getServiceObject(GestureService.class, justScannedDeviceList.get(index).getThingId());
            GestureService targetObj = service.getServiceObject(GestureService.class, justScannedDeviceList.get(index).getThingId());
        }
    }

    boolean gatheringData = false;
    public void startGathering(boolean on) {
        gatheringData = on;
        if (on) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    // If there is some change, store it.. just hard coding
                    while(gatheringData) {
                        if (deviceDataList.get(0) == 0.0f) {
                            boolean buttonState = buttonObj.getButtonState(0);
                            deviceDataList.set(0, buttonState ? 1.0f : 0.0f);
                        }
                        if (deviceDataList.get(1) == 0.0f) {
                            int gestureState = gestureObj.getCurrentGesture();
                            deviceDataList.set(1, (float) gestureState);
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            })).start();
        } else {
            // DO_NOTHING;
        }
    }

    public float getData(int index) {
        float toReturn = deviceDataList.get(index);
        deviceDataList.set(index, 0.0f);
        return toReturn;
    }
}
