package com.example.restapi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.restapi.model.CityItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheImageManager {

    public static Bitmap getImage(Context context, CityItem cityItem){

        String fileName = context.getCacheDir()+"/"+cityItem.getImage();
        File file = new File(fileName);

        Bitmap bitmap = null;

        try{
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }


    public static void putImage(Context context, CityItem cityItem,Bitmap bitmap){
        String fileName = context.getCacheDir()+"/"+cityItem.getImage();
        File file = new File(fileName);
        FileOutputStream  fileOutputStream = null;

        try{
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,fileOutputStream);

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
