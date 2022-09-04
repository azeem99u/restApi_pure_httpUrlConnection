package com.example.restapi.model;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestPackage {

    private String endPoint;
    private String method;
    private Map<String,String> params = new HashMap<>();

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(String key,String value) {

        this.params.put(key,value);
    }

    public String getEncodedParams(){
        StringBuilder stringBuilder = new StringBuilder();

        for (String key:params.keySet()) {

            String value = null;
            try{
                value = URLEncoder.encode(params.get(key),"UTF-8");

            }catch (Exception e){
                e.printStackTrace();
            }

            if (stringBuilder.length()>0){
                stringBuilder.append("&");
            }
            stringBuilder.append(key).append("=").append(value);
        }
        return stringBuilder.toString();
    }


}
