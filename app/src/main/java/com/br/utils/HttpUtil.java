package com.br.utils;

import android.util.Log;

import com.br.model.VoiceRemoveHolder;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MarioJ on 29/12/15.
 */
public class HttpUtil {

    private static final String TAG = "HttpUtil";
    public static final String URL_VOICE_REMOVER = "http://primeirabusca.com/app/voicemail/api.php?chave=J289H73sGF&numero=:phone";

    public static VoiceRemoveHolder getRemoveVoiceJSON(String phone) throws MalformedURLException {

        URL url = new URL(URL_VOICE_REMOVER.replace(":phone", phone));
        HttpURLConnection urlConnection = null;

        VoiceRemoveHolder voiceRemoverHolder = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int status = urlConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = readStream(in);

                voiceRemoverHolder = new Gson().fromJson(response, VoiceRemoveHolder.class);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return voiceRemoverHolder;
    }

    private static String readStream(InputStream in) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = "";

        while ((line = br.readLine()) != null)
            sb.append(line + System.lineSeparator());

        br.close();

        return sb.toString();
    }

}
