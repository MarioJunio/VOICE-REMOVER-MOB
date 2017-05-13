package com.br.primeirabuscavoiceremover;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.br.dialog.DialogWait;
import com.br.model.VoiceRemoveHolder;
import com.br.utils.HttpUtil;
import com.br.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Main extends AppCompatActivity {

    private static final String TAG = "Main";
    private ImageView imgPhone;
    private EditText inDD, inPhone;
    private Button btSend;
    private InterstitialAd interstitialAd;

    private final TextWatcher dddWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkPhone();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher phoneWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkPhone();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void checkPhone() {

        String ddd = inDD.getText().toString();
        String phone = inPhone.getText().toString();

        if (ddd.length() == 2 && phone.length() >= 8)
            imgPhone.setColorFilter(Color.parseColor("#4CAF50"));
        else
            imgPhone.setColorFilter(Color.parseColor("#9E9E9E"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try {

            init();
            createBannerAd();
            createInterstitialAd();
            load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException {

        String phone = Utils.getSavedPhone(getApplicationContext());

        if (phone != null) {

            String ddd = phone.substring(0, 2);
            String number = phone.substring(2, phone.length());

            inDD.setText(ddd);
            inPhone.setText(number);
        }

    }

    private void init() {

        imgPhone = (ImageView) findViewById(R.id.ic_phone);
        inDD = (EditText) findViewById(R.id.in_ddd);
        inPhone = (EditText) findViewById(R.id.in_phone);
        btSend = (Button) findViewById(R.id.bt_send);

        inDD.addTextChangedListener(dddWatcher);
        inPhone.addTextChangedListener(phoneWatcher);

        inDD.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseDDDialog();
            }

        });

        inDD.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus)
                    chooseDDDialog();

            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                View view = getCurrentFocus();

                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                new AsyncVoiceRemoval().execute(new String[]{inDD.getText().toString() + inPhone.getText().toString()});
            }
        });

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_cloud);
    }

    private void createBannerAd() {

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();

        if (adView != null)
            adView.loadAd(adRequest);

        ((FrameLayout) findViewById(R.id.layout_ad)).addView(adView);
    }

    private void createInterstitialAd() {

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.intersticial_ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

    }

    private void chooseDDDialog() {

        final String colors[] = getResources().getStringArray(R.array.estados);

        Collections.sort(Arrays.asList(colors), new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.substring(0, 1).compareTo(rhs.substring(0, 1));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha seu estado");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String estado = colors[which];

                if (estado != null && !estado.isEmpty()) {
                    inDD.setText(estado.split("-")[0].trim());
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_state), Toast.LENGTH_LONG).show();

                if (interstitialAd.isLoaded())
                    interstitialAd.show();

            }
        });

        builder.show();

    }

    public void shareFacebook(View view) {
        share("com.facebook.katana");
    }

    public void shareWhatsapp(View view) {
        share("com.whatsapp");
    }

    public void share(String application) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Achei um app muito legal para remover aquelas notificações de mensagem de voz que ficam barra de status, da uma olhada aí." + System.lineSeparator() + "https://play.google.com/store/apps/details?id=com.br.primeirabuscavoiceremover");
        sendIntent.setType("text/plain");
        sendIntent.setPackage(application);

        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException ace) {
            Log.d(TAG, "App nao instalado");
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    public class AsyncVoiceRemoval extends AsyncTask<String, String, VoiceRemoveHolder> {

        DialogWait dialogWait = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!Utils.isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
                return;
            }

            if (inDD.getText().toString().isEmpty() || inPhone.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_phone), Toast.LENGTH_LONG).show();
                return;
            }

            dialogWait = new DialogWait(Main.this, "55", inDD.getText().toString(), inPhone.getText().toString());
            dialogWait.setCancelable(false);
            dialogWait.setListenerOnAuthSuccess(null);

            dialogWait.setTitle(getResources().getString(R.string.processing));
            dialogWait.setMessage(getResources().getString(R.string.wait_clear_voice_mail));
            dialogWait.show();

            try {
                Utils.save(getApplicationContext(), inDD.getText().toString().trim() + inPhone.getText().toString().trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected VoiceRemoveHolder doInBackground(String... params) {

            if (inDD.getText().toString().isEmpty() || inPhone.getText().toString().isEmpty())
                return null;

            try {
                return HttpUtil.getRemoveVoiceJSON(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(VoiceRemoveHolder voiceRemoveHolder) {

            if (voiceRemoveHolder != null) {

                dialogWait.setMessage(voiceRemoveHolder.getMensagem());

                if (voiceRemoveHolder.getStatus() == 0) {
                    dialogWait.setTitle("Falha");
                    dialogWait.hideProgressLayout();
                    dialogWait.showButtonBack();
                } else {
                    dialogWait.hideButtonBack();
                    dialogWait.showProgressLayout();
                }

                inDD.setText("");
                inPhone.setText("");

                inDD.clearFocus();
                inPhone.clearFocus();
            }
        }
    }
}
