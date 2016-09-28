package kr.ac.kaist.resl.cmsp.iotapp.library.unity;

import android.util.Log;
import com.unity3d.player.UnityPlayer;
import kr.ac.kaist.resl.cmsp.iotapp.library.IoTAppService;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.MethodInvocationJsonObject;
import kr.ac.kaist.resl.cmsp.iotapp.library.invocation.ServiceObjectDynamicHandler;
import kr.ac.kaist.resl.cmsp.iotapp.library.utils.RandomInt;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by shheo on 16. 3. 18.
 */
public class UnityServiceObjectDynamicHandler implements InvocationHandler {
    public static final String TAG = "UnitySODHandler";
    CountDownLatch latch;
    String gameObjectName;
    Object toReturn = null;
    public UnityServiceObjectDynamicHandler(String gameObjectName) {
        this.gameObjectName = gameObjectName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.latch = new CountDownLatch(1);
        JSONObject invocation = new JSONObject();
        invocation.put("method", method.getName());
        JSONArray argsArray = new JSONArray();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                argsArray.put(args[i]);
            }
        }
        invocation.put("args", argsArray);
        int key = RandomInt.getNextInt();
        invocation.put("key", key);
        IotMapFacadeActivity.unityProxyMap.put(key, this);
        Log.d(TAG, "Invoking: " + invocation.toString());
        UnityPlayer.UnitySendMessage(gameObjectName, "invoke", invocation.toString());
        if (method.getReturnType().getSimpleName().equalsIgnoreCase("void")) {
            Log.e(TAG, "Return type is void. Returning null.");
            return null;
        }
        if (latch.await(2000, TimeUnit.MILLISECONDS)) {
            Log.d(TAG, "Got return value from Unity: " + toReturn + " as return type " + method.getReturnType().getSimpleName());
            return MethodInvocationJsonObject.cast(method.getReturnType().getSimpleName(), toReturn.toString());
        } else {
            Log.e(TAG, "Failed to retrieve return data from Unity!");
            return null;
        }
    }

    public void setReturn(Object toReturn) {
        this.toReturn = toReturn;
        latch.countDown();
    }
}
