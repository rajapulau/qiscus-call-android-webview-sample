package com.qiscus.rtc.webviewsample;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.qiscus.rtc.webviewsample.basic.IncomingActivity;
import com.qiscus.rtc.webviewsample.conference.ConfActivity;
import com.qiscus.rtc.webviewsample.utils.presenter.LoginPresenter;
import com.qiscus.sdk.Qiscus;

public class MainActivity extends AppCompatActivity implements LoginPresenter.View {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AlertDialog alertDialog;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginPresenter = new LoginPresenter(this, SampleApplication.getInstance().getComponent().getUserRepository());
        loginPresenter.start();

        startCall();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showHomePage() {
        //
    }

    @Override
    public void successLogin() {
        startActivity(new Intent(MainActivity.this, ContactActivity.class));
    }

    @Override
    public void showLoading() {
        //
    }

    @Override
    public void dismissLoading() {
        alertDialog.dismiss();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void startCall() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String callType = data.getLastPathSegment();

            if (callType.equals("room.html")) {
                String call_room_id = data.getQueryParameter("room");
                Intent i = new Intent(getApplicationContext(), ConfActivity.class);
                i.putExtra("call_room_id", call_room_id);
                startActivity(i);
            }
        } else {
            if (Qiscus.hasSetupUser()) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            } else {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View dialog = inflater.inflate(R.layout.dialog_login, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(dialog);
                alertDialogBuilder.setCancelable(false);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                dialog.findViewById(R.id.login_user1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginPresenter.login(
                                "User 1 Sample Call",
                                "user1_sample_call@example.com",
                                "123"
                        );
                    }
                });
                dialog.findViewById(R.id.login_user2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginPresenter.login(
                                "User 2 Sample Call",
                                "user2_sample_call@example.com",
                                "123"
                        );
                    }
                });
                dialog.findViewById(R.id.login_user4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginPresenter.login(
                                "User 4 Sample Call",
                                "user4_sample_call@example.com",
                                "123"
                        );
                    }
                });
                dialog.findViewById(R.id.login_user5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginPresenter.login(
                                "User 5 Sample Call",
                                "user5_sample_call@example.com",
                                "123"
                        );
                    }
                });
            }
        }
    }
}
