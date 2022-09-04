package com.example.restapi;

import static com.example.restapi.service.RestApiWorker.SERVICE_EXCEPTION;
import static com.example.restapi.service.RestApiWorker.SERVICE_MESSAGE;
import static com.example.restapi.service.RestApiWorker.SERVICE_PAYLOAD;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.restapi.adapter.MyListAdapter;
import com.example.restapi.model.CityItem;
import com.example.restapi.model.RequestPackage;
import com.example.restapi.service.RestApiWorker;
import com.example.restapi.utils.Helper;
import com.example.restapi.utils.NetworkHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private static final String JSON_URL = "http://10.0.2.2/pakinfo/json/itemsfeed.php";
    public static final String SERVICE_REQUEST_PACKAGE = "serviceRequestPackage";
    private Boolean isNetworkOk;
    private MyListAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<CityItem> cityItems;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(SERVICE_PAYLOAD)) {
                cityItems = intent.getParcelableArrayListExtra(SERVICE_PAYLOAD);
                showRecycleList(cityItems);

            } else if (intent.hasExtra(SERVICE_EXCEPTION)) {
                String errorMessage = intent.getStringExtra(SERVICE_EXCEPTION);
                Toast.makeText(context, "" + errorMessage, Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void showRecycleList(ArrayList<CityItem> cityItems) {
        adapter = new MyListAdapter(this, cityItems);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        isNetworkOk = NetworkHelper.isNetworkAvailable(this);
        cityItems = new ArrayList<>();
        runService();

    }


    public static OneTimeWorkRequest create() {

        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setEndPoint(JSON_URL);
        requestPackage.setMethod("GET");

        //requestPackage.setMethod("POST");
       // requestPackage.setParams("province", "Sindh");

        String serializedRP = Helper.serializeToJson(requestPackage);

        Data inputData = new Data.Builder()
                //  .putString(URI_STRING_KEY, Uri.parse(JSON_URL).toString())
                .putString(SERVICE_REQUEST_PACKAGE, serializedRP)
                .build();
        return new OneTimeWorkRequest.Builder(RestApiWorker.class)
                .setInputData(inputData)
                .build();
    }

    private void runService() {
        if (isNetworkOk) {
            WorkManager.getInstance(getApplicationContext()).enqueue(create());

        } else {
            Toast.makeText(this, "Network not available...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(SERVICE_MESSAGE));
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

}
