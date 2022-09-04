package com.example.restapi.utils;


import com.example.restapi.model.RequestPackage;

import java.io.BufferedOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {
    public static String downloadUrl(RequestPackage requestPackage) throws Exception {

        //Authorization: Basic {base64_encode(username:password)}
//
//        byte[] loginBytes = (userName+":"+password).getBytes();
//        StringBuilder stringBuilder = new StringBuilder()
//                .append("Basic ")
//                .append(Base64.encodeToString(loginBytes,Base64.DEFAULT));



        String address = requestPackage.getEndPoint();
        String encodedParams = requestPackage.getEncodedParams();
        if (requestPackage.getMethod().equals("GET")&& encodedParams.length()>0) {
            address = String.format("%s?%s",address,encodedParams);
        }

        InputStream inputStream = null;
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("Authorization",stringBuilder.toString());
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setRequestMethod(requestPackage.getMethod());

            //post Request
            if (requestPackage.getMethod().equals("POST")&& encodedParams.length()>0) {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(requestPackage.getEncodedParams());
                writer.flush();
                writer.close();
            }
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new Exception("Error: Got response code: " + responseCode);
            }

            inputStream = connection.getInputStream();
            return readStream(inputStream);

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static String readStream(InputStream inputStream) throws IOException {

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream out = null;
        try{
            int length = 0;
            out = new BufferedOutputStream(byteArray);
            while ((length = inputStream.read(buffer))>0){
                out.write(buffer,0,length);
            }
            out.flush();
            return byteArray.toString();
        }finally {
            if (out!= null){
                out.close();
            }
        }

       /* BufferedReader in = new BufferedReader(inputStream);
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();*/
    }

}
