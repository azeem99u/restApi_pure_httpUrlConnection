package com.example.restapi.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restapi.utils.CacheImageManager;
import com.example.restapi.model.CityItem;
import com.example.restapi.MainActivity;
import com.example.restapi.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {

    private Context mContext;
    private List<CityItem> cityDataItems;
    private static final String PHOTO_BASE_URL = "http://10.0.2.2/pakinfo/images/";
    Random random = new Random();


    public MyListAdapter(Context mContext, List<CityItem> cityDataItems) {
        this.mContext = mContext;
        this.cityDataItems = cityDataItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false));
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CityItem cityItem = cityDataItems.get(position);
        holder.cityName.setText(cityItem.getCityname());

        Bitmap bitmapImage = CacheImageManager.getImage(mContext, cityItem);
        if (bitmapImage == null) {
            downloadImage(holder, cityItem);
        }else {
            holder.cityImage.setImageBitmap(bitmapImage);
        }

    }

    private void downloadImage(@NonNull MyViewHolder holder, CityItem cityItem) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            Bitmap bitmap = null;

            @Override
            public void run() {


                ((MainActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }
                });


                String imageurl = PHOTO_BASE_URL + cityItem.getImage();

                InputStream inputStream = null;

                try {
                    URL imageUrl = new URL(imageurl);
                    inputStream = (InputStream) imageUrl.getContent();
                    bitmap = BitmapFactory.decodeStream(inputStream);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                int timeS = random.nextInt(2000);
                try {
                    Thread.sleep(timeS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                ((MainActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.progressBar.setVisibility(View.GONE);
                       // bitmapImage.put(cityItem.getCityname(), bitmap);
                        holder.cityImage.setImageBitmap(bitmap);
                        CacheImageManager.putImage(mContext,cityItem,bitmap);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityDataItems.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        ImageView cityImage;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            cityName = itemView.findViewById(R.id.cityName);
            cityImage = itemView.findViewById(R.id.cityImage);
        }
    }

    private void getCityImage(ImageView imageView, CityItem cityDataItem) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(cityDataItem.getImage());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            Log.d("meratag", "getView: Image Downloaded: " + cityDataItem.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
