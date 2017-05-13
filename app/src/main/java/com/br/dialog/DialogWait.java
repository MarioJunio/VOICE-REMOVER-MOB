package com.br.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.br.model.ListenerSuccess;
import com.br.primeirabuscavoiceremover.R;
import com.br.utils.PhoneFormatTextWatcher;
import com.br.utils.Phones;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MarioJ on 13/04/15.
 */
public class DialogWait extends AlertDialog {

    public static final String TAG = "WaitSmsDialog";

    // resources to handler logic
    private Handler handler;
    private Timer timer;
    private ListenerSuccess listenerSuccess;

    // Android widgets
    private TextView titleText, messageText, phoneText, timeText;
    private ProgressBar progressTime;

    // framelayout components
    private View layoutProgress;
    private Button btBack;

    // 600s equivale a 10m
    private final int MAX_TIME = 15;
    private int progress = 0;
    private int currentTime = MAX_TIME;

    // Phone
    private String ddi, ddd, phone;

    public static DialogWait newInstance(Context context, String ddi, String ddd, String phone) {
        return new DialogWait(context, ddi, ddd, phone);
    }

    public DialogWait(Context context, String ddi, String ddd, String phone) {
        super(context);

        handler = new Handler();
        timer = new Timer();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.ddi = ddi;
        this.ddd = ddd;
        this.phone = phone;

        initWidgets(inflater);

        startTimer();
    }

    private void initWidgets(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.dialog_wait, null);

        titleText = (TextView) view.findViewById(R.id.title);
        messageText = (TextView) view.findViewById(R.id.message);
        phoneText = (TextView) view.findViewById(R.id.phone);
        progressTime = (ProgressBar) view.findViewById(R.id.progressTime);
        timeText = (TextView) view.findViewById(R.id.time);
        layoutProgress = view.findViewById(R.id.progress_layout);
        btBack = (Button) view.findViewById(R.id.back);

        phoneText.setText(Phones.INTERNATIONAL_IDENTIFIER + ddi + " " + PhoneFormatTextWatcher.formatNumber(ddd + phone, Phones.getCountryISO(getContext(), "55")));

        setView(view);

        progressTime.setProgress(0);
        progressTime.setMax(MAX_TIME);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void showProgressLayout() {
        layoutProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressLayout() {
        layoutProgress.setVisibility(View.GONE);
    }

    public void showButtonBack() {
        btBack.setVisibility(View.VISIBLE);
    }

    public void hideButtonBack() {
        btBack.setVisibility(View.GONE);
    }

    private void startTimer() {
        timer.schedule(initializeTimerTask(), 0, 1000);
    }

    private void stopTimer() {
        timer.cancel();
    }

    public void setListenerOnAuthSuccess(ListenerSuccess listenerSuccess) {
        this.listenerSuccess = listenerSuccess;
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setMessage(String message) {
        messageText.setText(message);
    }

    public void setPhone(String ddi, String ddd, String phone) {
        this.ddi = ddi;
        this.ddd = ddd;
        this.phone = phone;

        phoneText.setText(Phones.INTERNATIONAL_IDENTIFIER + ddi + " " + PhoneFormatTextWatcher.formatNumber(ddd + phone, Phones.getCountryISO(getContext(), "55")));
    }

    private TimerTask initializeTimerTask() {

        return new TimerTask() {

            @Override
            public void run() {

                currentTime--;
                progress++;

                if (currentTime == 0) {

                    stopTimer();
                    dismiss();

                    if (listenerSuccess != null)
                        listenerSuccess.execute();

                }

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        timeText.setText(String.format("%d:%02d", currentTime / 60, currentTime % 60));
                        progressTime.setProgress(progress);
                    }
                });


            }
        };

    }
}
