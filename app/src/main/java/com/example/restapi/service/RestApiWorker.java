package com.example.restapi.service;


import static com.example.restapi.MainActivity.SERVICE_REQUEST_PACKAGE;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.restapi.model.CityItem;
import com.example.restapi.model.RequestPackage;
import com.example.restapi.utils.Helper;
import com.example.restapi.utils.HttpHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class RestApiWorker extends Worker {

    public static final String SERVICE_MESSAGE = "serviceMessage";
    public static final String SERVICE_PAYLOAD = "servicePayload";
    public static final String SERVICE_EXCEPTION = "service_exception";
    public RestApiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {

           // String stringUrl = getInputData().getString(URI_STRING_KEY);
            String requestPackageString = getInputData().getString(SERVICE_REQUEST_PACKAGE);
            RequestPackage requestPackage = Helper.deserializeFromJson(requestPackageString);

            try {
                //String data = HttpHelper.downloadUrl(stringUrl);
                String data = HttpHelper.downloadUrl(requestPackage);
                Gson gson = new Gson();
                ArrayList<CityItem> cityItems1 = gson.fromJson(data, new TypeToken<ArrayList<CityItem>>() {}.getType());
                sendMessageToUI(cityItems1);

            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToUI(e);
            }
            return Result.success();

        } catch (Throwable t) {
            t.getLocalizedMessage();
            Log.d("tag", "doWork: " + t.toString());
            return Result.failure();
        }

    }

    private void sendMessageToUI(Exception e) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra(SERVICE_EXCEPTION, e.getMessage());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendMessageToUI(ArrayList<CityItem> cityItems) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putParcelableArrayListExtra(SERVICE_PAYLOAD, cityItems);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }




}