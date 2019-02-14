package com.qiscus.rtc.webviewsample;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.qiscus.rtc.webviewsample.basic.IncomingActivity;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusComment;
import com.qiscus.sdk.data.model.QiscusNotificationBuilderInterceptor;
import com.qiscus.sdk.event.QiscusCommentReceivedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fitra on 31/05/18.
 */

public class SampleApplication extends Application {
    private static SampleApplication instance;
    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        component = new AppComponent(this);
        Qiscus.init(this, Config.CHAT_APP_ID);
        Qiscus.getChatConfig().setNotificationBuilderInterceptor(new QiscusNotificationBuilderInterceptor() {
            @Override
            public boolean intercept(NotificationCompat.Builder notificationBuilder, QiscusComment qiscusComment) {
                if (qiscusComment.getType() == QiscusComment.Type.SYSTEM_EVENT) {
                    return false;
                }

                return true;
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public static SampleApplication getInstance() {
        return instance;
    }

    public AppComponent getComponent() {
        return component;
    }

    @Subscribe
    public void onReceivedComment(QiscusCommentReceivedEvent event) {
        if (event.getQiscusComment().getExtraPayload() != null && !event.getQiscusComment().getExtraPayload().equals("null")) {
            handleCallPn(event.getQiscusComment());
        }
    }

    private void handleCallPn(QiscusComment remoteMessage) {
        JSONObject json;

        try {
            json = new JSONObject(remoteMessage.getExtraPayload());
            JSONObject payload = json.getJSONObject("payload");
            Log.d("SINI", payload.toString());

            if (payload.get("type").equals("webview_call") || payload.get("type").equals("call")) {
                String event = payload.getString("call_event");
                String call_room_id = payload.getString("call_room_id");
                switch (event.toLowerCase()) {
                    case "incoming":
                        JSONObject caller = payload.getJSONObject("call_caller");
                        final String caller_email = caller.getString("username");
                        final String caller_name = caller.getString("name");
                        final String caller_avatar = caller.getString("avatar");
                        JSONObject callee = payload.getJSONObject("call_callee");
                        final String callee_email = callee.getString("username");
                        final String callee_name = callee.getString("name");
                        final String callee_avatar = callee.getString("avatar");

                        if (Qiscus.getQiscusAccount().getEmail().equals(callee_email)) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), IncomingActivity.class);
                                    intent.putExtra("call_room_id", call_room_id);
                                    intent.putExtra("caller_name", caller_name);
                                    intent.putExtra("caller_email", caller_email);
                                    intent.putExtra("caller_avatar", caller_avatar);
                                    startActivity(intent);
                                }
                            }, 2500);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
