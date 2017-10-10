package com.kys26.webthings.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kys_26 on 2015/5/14.
 */

public class Sessionutil {
    @SuppressWarnings("unchecked")
    private Map _objectContainer;

    private static Sessionutil sessionUtil;
    private Sessionutil(){

        _objectContainer = new HashMap();
    }
    public static Sessionutil getSessionUtil(){

        if(sessionUtil == null){
            sessionUtil = new Sessionutil();
            return sessionUtil;
        }else{
            return sessionUtil;
        }
    }

    @SuppressWarnings("unchecked")
    public   void put(Object key, Object value){

        _objectContainer.put(key, value);
    }
    public Object get(Object key){

        return _objectContainer.get(key);
    }
    public void cleanUpSession(){
        _objectContainer.clear();
    }
    public void remove(Object key){
        _objectContainer.remove(key);
    }
}
