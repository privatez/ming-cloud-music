package ming.cloudmusic.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Lhy on 2016/3/17.
 */
public class SharedPrefsUtil {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    /**
     * 构造函数
     *
     * @param context    context对象
     * @param spFileName SharedPreferences的文件名
     */
    public SharedPrefsUtil(Context context, String spFileName) {
        mContext = context;
        mSharedPreferences = context.getApplicationContext().getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    /**
     * 存int值
     *
     * @param key
     * @param value
     */
    public void setIntSP(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    /**
     * 存int值
     *
     * @param key
     * @param value
     */
    public void setIntSP(int key, int value) {
        mEditor.putInt(mContext.getResources().getString(key), value);
        mEditor.commit();
    }

    /**
     * 取int值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getIntSP(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 取int值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getIntSP(int key, int defaultValue) {
        return mSharedPreferences.getInt(mContext.getResources().getString(key), defaultValue);
    }


    /**
     * 存string值
     *
     * @param key
     * @param value
     */
    public void setStringSP(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * 存string值
     *
     * @param key
     * @param value
     */
    public void setStringSP(int key, String value) {
        mEditor.putString(mContext.getResources().getString(key), value);
        mEditor.commit();
    }

    /**
     * 取string值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getStringSP(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * 取string值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getStringSP(int key, String defaultValue) {
        return mSharedPreferences.getString(mContext.getResources().getString(key), defaultValue);
    }


    /**
     * 存boolean值
     *
     * @param key
     * @param value
     */
    public void setBooleanSP(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    /**
     * 存boolean值
     *
     * @param key
     * @param value
     */
    public void setBooleanSP(int key, boolean value) {
        mEditor.putBoolean(mContext.getResources().getString(key), value);
        mEditor.commit();
    }

    /**
     * 取boolean值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBooleanSP(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 取boolean值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBooleanSP(int key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(mContext.getResources().getString(key), defaultValue);
    }

    /**
     * 存float值
     *
     * @param key
     * @param value
     */
    public void setFloatSP(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.commit();
    }

    /**
     * 存float值
     *
     * @param key
     * @param value
     */
    public void setFloatSP(int key, float value) {
        mEditor.putFloat(mContext.getResources().getString(key), value);
        mEditor.commit();
    }

    /**
     * 取float值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloatSP(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * 取float值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloatSP(int key, float defaultValue) {
        return mSharedPreferences.getFloat(mContext.getResources().getString(key), defaultValue);
    }

    /**
     * 存long值
     *
     * @param key
     * @param value
     */
    public void setLongSP(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    /**
     * 存long值
     *
     * @param key
     * @param value
     */
    public void setLongSP(int key, long value) {
        mEditor.putLong(mContext.getResources().getString(key), value);
        mEditor.commit();
    }

    /**
     * 取long值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLongSP(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * 取long值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLongSP(int key, long defaultValue) {
        return mSharedPreferences.getLong(mContext.getResources().getString(key), defaultValue);
    }

    /**
     * 存double值
     *
     * @param key
     * @param value
     */
    public void setDoubleSP(String key, double value) {
        setStringSP(key, String.valueOf(value));
    }

    /**
     * 存double值
     *
     * @param key
     * @param value
     */
    public void setDoubleSP(int key, double value) {
        setStringSP(mContext.getResources().getString(key), String.valueOf(value));
    }

    /**
     * 取double值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDoubleSP(String key, double defaultValue) {
        return Double.valueOf(getStringSP(key, String.valueOf(defaultValue)));
    }

    /**
     * 取double值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDoubleSP(int key, double defaultValue) {
        return Double.valueOf(getStringSP(mContext.getResources().getString(key), String.valueOf(defaultValue)));
    }

    /**
     * 清空所有数据
     *
     * @return
     */
    public boolean clearAll() {
        mEditor.clear();
        return mEditor.commit();
    }
}
