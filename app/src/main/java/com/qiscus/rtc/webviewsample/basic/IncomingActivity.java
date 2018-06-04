package com.qiscus.rtc.webviewsample.basic;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qiscus.rtc.webviewsample.MainActivity;
import com.qiscus.rtc.webviewsample.R;
import com.qiscus.sdk.Qiscus;

import java.io.IOException;

public class IncomingActivity extends AppCompatActivity {
    private static final String TAG = IncomingActivity.class.getSimpleName();

    private MediaPlayer ringtone;
    private TextView callerName;
    private Button acceptCallButton;
    private Button rejectCallButton;
    private Boolean isRinging = false;
    private Boolean ringtonePlayable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);

        Bundle extras = getIntent().getExtras();
        String call_room_id = extras.getString("call_room_id");
        String caller_name = extras.getString("caller_name");
        String caller_email = extras.getString("caller_email");
        String caller_avatar = extras.getString("caller_avatar");

        callerName = (TextView) findViewById(R.id.caller_name);
        callerName.setText(caller_name);
        acceptCallButton = (Button) findViewById(R.id.accept_call_button);
        rejectCallButton = (Button) findViewById(R.id.reject_call_button);

        try {
            ringtone = new MediaPlayer();
            ringtone.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            ringtonePlayable = true;
        } catch (IOException e) {
            ringtonePlayable = false;
            e.printStackTrace();
        }

        acceptCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopSound();
                isRinging = false;

                Intent intent = new Intent(IncomingActivity.this, CallActivity.class);
                intent.putExtra("call_room_id", call_room_id);
                intent.putExtra("caller_name", caller_name);
                intent.putExtra("caller_email", caller_email);
                intent.putExtra("caller_avatar", caller_avatar);
                intent.putExtra("callee_email", Qiscus.getQiscusAccount().getEmail());
                startActivity(intent);
                finish();
            }
        });

        rejectCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopSound();
                isRinging = false;

                Intent intent = new Intent(IncomingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        playSound();
    }

    @Override
    public void onStop() {
        stopSound();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopSound();
        super.onDestroy();
    }

    private void playSound() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            if (ringtonePlayable && audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                ringtone.setAudioStreamType(AudioManager.STREAM_RING);
                ringtone.prepare();
                ringtone.setLooping(true);
                ringtone.start();
                isRinging = true;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopSound() {
        if (ringtonePlayable && isRinging) {
            ringtone.stop();
            ringtone.release();
        }

        ringtonePlayable = true;
        isRinging = false;
        ringtone = null;
    }
}
