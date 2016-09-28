package kr.ac.kaist.resl.cmsp.iotapp.library.invocation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shheo on 15. 4. 17.
 */
public class MethodInvocationParcel implements Parcelable {
    ArrayList<Object> params;
    public MethodInvocationParcel() {
        params = new ArrayList<>();
    }

    public MethodInvocationParcel(Parcel parcel) {
        int available = parcel.dataAvail();
        params = new ArrayList<>(available);
        ClassLoader myClassLoader = getClass().getClassLoader();
        for (int i = 0;i < available;i++) {
            params.add(i, parcel.readValue(myClassLoader));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (Object o : params)
            dest.writeValue(o); // TODO: If it is too slow, use commented code below
    }

    public void addArg(Object param) {
        params.add(param);
    }

    public class MethodInvocationParcelCreator implements Parcelable.Creator<MethodInvocationParcel> {
        @Override
        public MethodInvocationParcel createFromParcel(Parcel source) {
            return new MethodInvocationParcel(source);
        }

        @Override
        public MethodInvocationParcel[] newArray(int size) {
            return new MethodInvocationParcel[0];
        }
    }

    /*
        switch (paramTypeClazz.getSimpleName()) {
            case TYPENAME_BOOLEAN1: invocationBundle.putBoolean(TYPENAME_BOOLEAN1, (boolean) param); break;
            case TYPENAME_BOOLEAN2: invocationBundle.putBoolean(TYPENAME_BOOLEAN2, (boolean) param); break;
            case TYPENAME_BOOLEANARRAY1: invocationBundle.putBooleanArray(TYPENAME_BOOLEANARRAY1, (boolean[]) param); break;
            case TYPENAME_BOOLEANARRAY2: invocationBundle.putBooleanArray(TYPENAME_BOOLEANARRAY2, (boolean[]) param); break;
            case TYPENAME_BYTE1: invocationBundle.putByte(TYPENAME_BYTE1, (byte) param); break;
            case TYPENAME_BYTE2: invocationBundle.putByte(TYPENAME_BYTE2, (byte) param); break;
            case TYPENAME_BYTEARRAY1: invocationBundle.putByteArray(TYPENAME_BYTEARRAY1, (byte[]) param); break;
            case TYPENAME_BYTEARRAY2: invocationBundle.putByteArray(TYPENAME_BYTEARRAY2, (byte[]) param); break;
            case TYPENAME_CHAR1: invocationBundle.putChar(TYPENAME_CHAR1, (char) param); break;
            case TYPENAME_CHAR2: invocationBundle.putChar(TYPENAME_CHAR2, (char) param); break;
            case TYPENAME_CHARARRAY1: invocationBundle.putCharArray(TYPENAME_CHARARRAY1, (char[]) param); break;
            case TYPENAME_CHARARRAY2: invocationBundle.putCharArray(TYPENAME_CHARARRAY2, (char[]) param); break;
            case TYPENAME_CHARSEQUENCE: invocationBundle.putCharSequence(TYPENAME_CHARSEQUENCE, (CharSequence) param); break;
            case TYPENAME_CHARSEQUENCEARRAY: invocationBundle.putCharSequenceArray(TYPENAME_CHARSEQUENCEARRAY, (CharSequence[]) param); break;
            case TYPENAME_DOUBLE1: invocationBundle.putDouble(TYPENAME_DOUBLE1, (double) param); break;
            case TYPENAME_DOUBLE2: invocationBundle.putDouble(TYPENAME_DOUBLE2, (double) param); break;
            case TYPENAME_DOUBLEARRAY1: invocationBundle.putDoubleArray(TYPENAME_DOUBLEARRAY1, (double[]) param); break;
            case TYPENAME_DOUBLEARRAY2: invocationBundle.putDoubleArray(TYPENAME_DOUBLEARRAY2, (double[]) param); break;
            case TYPENAME_FLOAT1: invocationBundle.putFloat(TYPENAME_FLOAT1, (float) param); break;
            case TYPENAME_FLOAT2: invocationBundle.putFloat(TYPENAME_FLOAT2, (float) param); break;
            case TYPENAME_FLOATARRAY1: invocationBundle.putFloatArray(TYPENAME_FLOATARRAY1, (float[]) param); break;
            case TYPENAME_FLOATARRAY2: invocationBundle.putFloatArray(TYPENAME_FLOATARRAY2, (float[]) param); break;
            case TYPENAME_INTEGER1: invocationBundle.putInt(TYPENAME_INTEGER1, (int) param); break;
            case TYPENAME_INTEGER2: invocationBundle.putInt(TYPENAME_INTEGER2, (int) param); break;
            case TYPENAME_INTEGERARRAY1: invocationBundle.putIntArray(TYPENAME_INTEGERARRAY1, (int[]) param); break;
            case TYPENAME_INTEGERARRAY2: invocationBundle.putIntArray(TYPENAME_INTEGERARRAY2, (int[]) param); break;
            case TYPENAME_LONG1: invocationBundle.putLong(TYPENAME_LONG1, (long) param); break;
            case TYPENAME_LONG2: invocationBundle.putLong(TYPENAME_LONG2, (long) param); break;
            case TYPENAME_LONGARRAY1: invocationBundle.putLongArray(TYPENAME_LONGARRAY1, (long[]) param); break;
            case TYPENAME_LONGARRAY2: invocationBundle.putLongArray(TYPENAME_LONGARRAY2, (long[]) param); break;
            case TYPENAME_PARCELABLE: invocationBundle.putParcelable(TYPENAME_PARCELABLE, (Parcelable) param); break;
            case TYPENAME_PARCELABLEARRAY: invocationBundle.putParcelableArray(TYPENAME_PARCELABLEARRAY, (Parcelable[]) param); break;
            case TYPENAME_SERIALIZABLE: invocationBundle.putParcelable(TYPENAME_SERIALIZABLE, (Parcelable) param); break;
            case TYPENAME_SHORT1: invocationBundle.putShort(TYPENAME_SHORT1, (short) param); break;
            case TYPENAME_SHORT2: invocationBundle.putShort(TYPENAME_SHORT2, (short) param); break;
            case TYPENAME_SHORTARRAY1: invocationBundle.putShortArray(TYPENAME_SHORTARRAY1, (short[]) param); break;
            case TYPENAME_SHORTARRAY2: invocationBundle.putShortArray(TYPENAME_SHORTARRAY2, (short[]) param); break;
            case TYPENAME_STRING: invocationBundle.putString(TYPENAME_STRING, (String) param); break;
            case TYPENAME_STRINGARRAY: invocationBundle.putStringArray(TYPENAME_STRINGARRAY, (String[]) param); break;
            case TYPENAME_SPARCEARRAY:
                //invocationBundle.putSparseParcelableArray(TYPENAME_SPARCEARRAY, (SparseArray) param);
                Log.e(TAG, "putSparseParcelableArray is not implemented");
                break;
            case TYPENAME_ARRAYLIST:
                //invocationBundle.putCharSequenceArrayList(TYPENAME_ARRAYLIST, (ArrayList) param);
                Log.e(TAG, "put***ArrayList is not implemented");
                break;
            default:
                Log.e(TAG, "Parameter type " + paramTypeClazz.getSimpleName() + " is not supported");
                return null;
        }
        */
    /*
    // Most of types supported by Android Bundle
    private static final String TYPENAME_BOOLEAN1 = "boolean";
    private static final String TYPENAME_BOOLEAN2 = "Boolean";
    private static final String TYPENAME_BOOLEANARRAY1 = "boolean[]";
    private static final String TYPENAME_BOOLEANARRAY2 = "Boolean[]";
    private static final String TYPENAME_BYTE1 = "byte";
    private static final String TYPENAME_BYTE2 = "Byte";
    private static final String TYPENAME_BYTEARRAY1 = "byte[]";
    private static final String TYPENAME_BYTEARRAY2 = "Byte[]";
    private static final String TYPENAME_CHAR1 = "char";
    private static final String TYPENAME_CHAR2 = "Character";
    private static final String TYPENAME_CHARARRAY1 = "char[]";
    private static final String TYPENAME_CHARARRAY2 = "Character[]";
    private static final String TYPENAME_CHARSEQUENCE = "CharSequence";
    private static final String TYPENAME_CHARSEQUENCEARRAY = "CharSequence[]";
    private static final String TYPENAME_DOUBLE1 = "double";
    private static final String TYPENAME_DOUBLE2 = "Double";
    private static final String TYPENAME_DOUBLEARRAY1 = "double[]";
    private static final String TYPENAME_DOUBLEARRAY2 = "Double[]";
    private static final String TYPENAME_FLOAT1 = "float";
    private static final String TYPENAME_FLOAT2 = "Float";
    private static final String TYPENAME_FLOATARRAY1 = "float[]";
    private static final String TYPENAME_FLOATARRAY2 = "Float[]";
    private static final String TYPENAME_INTEGER1 = "int";
    private static final String TYPENAME_INTEGER2 = "Integer";
    private static final String TYPENAME_INTEGERARRAY1 = "int[]";
    private static final String TYPENAME_INTEGERARRAY2 = "Integer[]";
    private static final String TYPENAME_LONG1 = "long";
    private static final String TYPENAME_LONG2 = "Long";
    private static final String TYPENAME_LONGARRAY1 = "long[]";
    private static final String TYPENAME_LONGARRAY2 = "Long[]";
    private static final String TYPENAME_PARCELABLE = "Parcelable";
    private static final String TYPENAME_PARCELABLEARRAY = "Parcelable[]";
    private static final String TYPENAME_SERIALIZABLE = "Serializable";
    private static final String TYPENAME_SHORT1 = "short";
    private static final String TYPENAME_SHORT2 = "Short";
    private static final String TYPENAME_SHORTARRAY1 = "short[]";
    private static final String TYPENAME_SHORTARRAY2 = "Short[]";
    private static final String TYPENAME_STRING = "String";
    private static final String TYPENAME_STRINGARRAY = "String[]";
    private static final String TYPENAME_ARRAYLIST = "ArrayList";
    private static final String TYPENAME_SPARCEARRAY = "SparseArray";
    */
}
