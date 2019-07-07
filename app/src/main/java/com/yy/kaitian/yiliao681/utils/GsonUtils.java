package com.yy.kaitian.yiliao681.utils;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;


/**
 * Created by Administrator on 2017/3/11.
 */
public enum GsonUtils {
    INSTANCE;
    private  Gson mGson;

    GsonUtils() {
        mGson = new Gson();
    }

    /**把一个Object类型的数据转换为 Json格式的字符串
     * @param object ：
     * @return ：
     */
    public  String toJsonString(Object object) {
        return mGson.toJson(object);
    }
//    public String toJsonStringArray(){
//        return mGson.toJson;
//    }


    public  <T> List<T> parseArray(String jsonArrayString, TypeToken<List<T>> type) {
        return mGson.fromJson(jsonArrayString,type.getType());
    }


    public  <T> T parseObj(String jsonStr, TypeToken<T> type) {
        return mGson.fromJson(jsonStr,type.getType());
    }
    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public  <T> T parseToBean(String gsonString, Class<T> cls) {
        try {
            AppLog.instance().d(gsonString);
            return mGson.fromJson(gsonString, cls);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
//    public JsonRoot parseToRoot(String gsonString) {
//        try {
//            return mGson.fromJson(gsonString, JsonRoot.class);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private  JsonRoot parseToRoot(String gsonString) {
//        return mGson.fromJson(gsonString, JsonRoot.class);
//    }
//    public  <T> T parseToBean(String gsonString, Class<T> cls) {
//        JsonRoot root = parseToRoot(gsonString);
//        if(root.isSuccess()){
//            return mGson.fromJson(gsonString, cls);
//        } else {
//            return null;
//        }
//    }

    /**
     * 转成list
     * 泛型在编译期类型被擦除导致报错
     * @param gsonString
     * @param cls
     * @return
     */
    public  <T> List<T> parseToList(String gsonString, Class<T> cls) {
             return mGson.fromJson(gsonString, new TypeToken<List<T>>() {}.getType());
    }


}
