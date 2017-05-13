package com.br.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by MarioJ on 01/01/16.
 */
public class Utils {

    private static String FILENAME = "primeirabusca.phone";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void save(Context context, String phone) throws IOException {

        FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
        fos.write(phone.getBytes());
        fos.close();

    }

    public static String getSavedPhone(Context context) throws IOException {

        byte buffer[] = new byte[1024];

        FileInputStream fis = context.openFileInput(FILENAME);

        int i = fis.read(buffer);

        if (i != -1)
            return new String(buffer).substring(0, i);
        else
            return null;

    }

}
