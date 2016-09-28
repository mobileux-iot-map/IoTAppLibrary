package kr.ac.kaist.resl.cmsp.iotapp.library.invocation;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by shheo on 15. 4. 19.
 */
public class MethodInvocationJsonObject extends JSONObject {
    public static final String JSON_KEY_THING_ID = "thing_id";
    public static final String JSON_KEY_METHOD_NAME = "method_name";
    public static final String JSON_KEY_PARAM_COUNT = "param_count";
    public static final String JSON_KEY_PARAM_ARRAY = "param_array";
    public static final String JSON_KEY_TYPE_ARRAY = "type_array";
    public static final String JSON_KEY_RETURN_TYPE = "return_type";
    public static final String JSON_KEY_RETURN_DATA = "return_data";
    public MethodInvocationJsonObject () {
        super();
    }
    public MethodInvocationJsonObject (String jsonString) throws JSONException { super(jsonString); }
    public void setData(String thingId, Method method, Object[] args, Class<?> returnType) throws JSONException {
        Class<?>[] paramTypes = method.getParameterTypes();
        this.put(JSON_KEY_THING_ID, thingId);
        this.put(JSON_KEY_METHOD_NAME, method.getName());
        this.put(JSON_KEY_PARAM_COUNT, paramTypes.length);
        JSONArray typeArray = new JSONArray();
        JSONArray paramArray = new JSONArray();
        for (int paramIndex = 0;paramIndex < paramTypes.length;paramIndex++) {
            Class<?> paramTypeClazz = paramTypes[paramIndex];
            Object param = args[paramIndex];

            typeArray.put(paramTypeClazz.getName());
            paramArray.put(param);
            //paramArray.put(cast(paramTypeClazz, param));

        }
        this.put(JSON_KEY_PARAM_ARRAY, paramArray);
        this.put(JSON_KEY_TYPE_ARRAY, typeArray);
        this.put(JSON_KEY_RETURN_TYPE, returnType.getName());
        Log.d(this.getClass().getSimpleName(), "ThingId: " + thingId + ", Method " + method.getName() + " invocation is initialized: " + this.toString());
    }
    public String getThingId() throws JSONException {
        return this.getString(JSON_KEY_THING_ID);
    }
    public String getMethodName() throws JSONException {
        return this.getString(JSON_KEY_METHOD_NAME);
    }
    public int getParamCount() throws JSONException {
        return Integer.parseInt(this.getString(JSON_KEY_PARAM_COUNT));
    }
    public String[] getParamArray() throws JSONException {
        JSONArray array = this.getJSONArray(JSON_KEY_PARAM_ARRAY);
        String[] strArray = new String[array.length()];
        for (int i = 0;i < array.length();i++) {
            strArray[i] = array.getString(i);
        }
        return strArray;
    }
    public String[] getTypeArray() throws JSONException {
        JSONArray array = this.getJSONArray(JSON_KEY_TYPE_ARRAY);
        String[] strArray = new String[array.length()];
        for (int i = 0;i < array.length();i++) {
            strArray[i] = array.getString(i);
        }
        return strArray;
    }
    public String getReturnType() throws JSONException {
        return this.getString(JSON_KEY_RETURN_TYPE);
    }
    public String getReturnData() throws JSONException {
        return this.getString(JSON_KEY_RETURN_DATA);
    }


    public static <T> T cast (Class<T> type, Object object) {
        return (T) cast(type.getSimpleName(), object.toString());
    }

    public static Object cast(String type, String data) {
        Log.d("MethodInvocation", "Casting data " + data + " to type " + type);
        String typeSimple = type.substring(type.lastIndexOf('.') + 1);
        Object toReturn = null;

        switch (typeSimple) {
            case "String":
                toReturn = data;
                break;
            case "Boolean":
                toReturn = Boolean.parseBoolean(data);
                break;
            case "boolean":
                toReturn = Boolean.parseBoolean(data);
                break;
            case "Byte":
                toReturn = Byte.parseByte(data);
                break;
            case "byte":
                toReturn = Byte.parseByte(data);
                break;
            case "Char":
                toReturn = data.charAt(0);
                break;
            case "char":
                toReturn = data.charAt(0);
                break;
            case "Double":
                toReturn = Double.parseDouble(data);
                break;
            case "double":
                toReturn = Double.parseDouble(data);
                break;
            case "Integer":
                toReturn = Integer.parseInt(data);
                break;
            case "int":
                toReturn = Integer.parseInt(data);
                break;
            case "Long":
                toReturn = Long.parseLong(data);
                break;
            case "long":
                toReturn = Long.parseLong(data);
                break;
            case "Float":
                toReturn = Float.parseFloat(data);
                break;
            case "float":
                toReturn = Float.parseFloat(data);
                break;
            case "Short":
                toReturn = Short.parseShort(data);
                break;
            case "short":
                toReturn = Short.parseShort(data);
                break;
            default:
                if (type.equals("void"))
                    toReturn = null;
                else
                    Log.e("MethodInvocation", "Return type " + type + " is not supported");
        }
        return toReturn;
    }

    public static String convertPrimitive(String type) {
        if (Character.isUpperCase(type.charAt(0))) {
            return type;
        }
        String toReturn;
        switch (type) {
            case "boolean":
                toReturn = "Boolean";
                break;
            case "byte":
                toReturn = "Byte";
                break;
            case "char":
                toReturn = "Char";
                break;
            case "double":
                toReturn = "Double";
                break;
            case "int":
                toReturn = "Integer";
                break;
            case "long":
                toReturn = "Long";
                break;
            case "float":
                toReturn = "Float";
                break;
            case "short":
                toReturn = "Short";
                break;
            default:
                if (type.equals("void"))
                    toReturn = null;
                else
                    Log.e("MethodInvocation", "Return type " + type + " is not supported");
                toReturn = null;
        }
        return toReturn;
    }

    public static Class getClass(String className) throws ClassNotFoundException {
        switch(className) {
            case "Boolean":
            case "boolean":
                return boolean.class;
            case "Byte":
            case "byte":
                return byte.class;
            case "Char":
            case "char":
                return char.class;
            case "Double":
            case "double":
                return double.class;
            case "Integer":
            case "int":
                return int.class;
            case "Long":
            case "long":
                return long.class;
            case "Float":
            case "float":
                return float.class;
            case "Short":
            case "short":
                return short.class;
            default:
                if (className.equals("void") || className.equals("Void"))
                    return null;
                else
                    return Class.forName(className);
        }
    }
}
