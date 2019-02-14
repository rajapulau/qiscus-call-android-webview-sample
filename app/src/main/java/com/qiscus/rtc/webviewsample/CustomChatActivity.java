package com.qiscus.rtc.webviewsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qiscus.nirmana.Nirmana;
import com.qiscus.rtc.webviewsample.basic.CallActivity;
import com.qiscus.rtc.webviewsample.utils.AsyncHttpUrlConnection;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.model.QiscusComment;
import com.qiscus.sdk.data.model.QiscusRoomMember;
import com.qiscus.sdk.ui.QiscusBaseChatActivity;
import com.qiscus.sdk.ui.QiscusChannelActivity;
import com.qiscus.sdk.ui.QiscusChatActivity;
import com.qiscus.sdk.ui.fragment.QiscusBaseChatFragment;
import com.qiscus.sdk.ui.fragment.QiscusChatFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CustomChatActivity extends QiscusBaseChatActivity {
    private static final String TAG = CustomChatActivity.class.getSimpleName();

    private AsyncHttpUrlConnection httpConnection;
    private TextView title;
    private ImageView back;
    private ImageView voiceCall;
    private ImageView videoCall;

    private TextView mTitle;
    private TextView sTitle;
    private TextView txtPlatform;
    private Toolbar toolbar;
    private ImageView avatar;
    private String subtitle;

//    public static Intent generateIntent(Context context, QiscusChatRoom qiscusChatRoom) {
//        Intent intent = new Intent(context, CustomChatActivity.class);
//        intent.putExtra(CHAT_ROOM_DATA, qiscusChatRoom);
//        return intent;
//    }

    public static Intent generateIntent(Context context, QiscusChatRoom qiscusChatRoom) {
        return generateIntent(context, qiscusChatRoom, null, null,
                false, null, null);
    }

    public static Intent generateIntent(Context context, QiscusChatRoom qiscusChatRoom,
                                        String startingMessage, List<File> shareFiles,
                                        boolean autoSendExtra, List<QiscusComment> comments,
                                        QiscusComment scrollToComment) {
//        if (!qiscusChatRoom.isGroup()) {
//            return CustomChatActivity.generateIntent(context, qiscusChatRoom, startingMessage,
//                    shareFiles, autoSendExtra, comments, scrollToComment);
//        }

        if (qiscusChatRoom.isChannel()) {
            return QiscusChannelActivity.generateIntent(context, qiscusChatRoom, startingMessage,
                    shareFiles, autoSendExtra, comments, scrollToComment);
        }

        Intent intent = new Intent(context, CustomChatActivity.class);
        intent.putExtra(CHAT_ROOM_DATA, qiscusChatRoom);
        intent.putExtra(EXTRA_STARTING_MESSAGE, startingMessage);
        intent.putExtra(EXTRA_SHARING_FILES, (Serializable) shareFiles);
        intent.putExtra(EXTRA_AUTO_SEND, autoSendExtra);
        intent.putParcelableArrayListExtra(EXTRA_FORWARD_COMMENTS, (ArrayList<QiscusComment>) comments);
        intent.putExtra(EXTRA_SCROLL_TO_COMMENT, scrollToComment);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_custom_chat;
    }

    @Override
    protected void onLoadView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.back);
        videoCall = (ImageView) findViewById(R.id.video_call);
        avatar = findViewById(R.id.profile_picture);
        mTitle = findViewById(R.id.tv_title);
        sTitle = findViewById(R.id.tv_subtitle);
        sTitle = findViewById(R.id.tv_subtitle);
    }

    @Override
    protected void binRoomData() {
        super.binRoomData();
        generateSubtitle();
        mTitle.setText(qiscusChatRoom.getName());
        sTitle.setVisibility(View.VISIBLE);
        sTitle.setText(subtitle);

        Nirmana.getInstance().get()
                .load(qiscusChatRoom.getAvatarUrl())
                .placeholder(R.drawable.ic_qiscus_avatar)
                .error(R.drawable.ic_qiscus_avatar)
                .dontAnimate()
                .into(avatar);

    }

    @Override
    protected QiscusBaseChatFragment onCreateChatFragment() {
        return QiscusChatFragment.newInstance(qiscusChatRoom);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);
        mTitle.setText(qiscusChatRoom.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (QiscusRoomMember member : qiscusChatRoom.getMember()) {
                    if (!member.getEmail().equalsIgnoreCase(Qiscus.getQiscusAccount().getEmail())) {
                        startVideoCall(member);
                    }
                }
            }
        });
    }

    @Override
    public void onUserStatusChanged(String user, boolean online, Date lastActive) {
        //
    }

    @Override
    public void onUserTyping(String user, boolean typing) {
        //
    }

    protected void generateSubtitle() {
        subtitle = "";
        int count = 0;
        Collections.sort(qiscusChatRoom.getMember(), (o1, o2) -> o1.getUsername().compareTo(o2.getUsername()));
        for (QiscusRoomMember member : qiscusChatRoom.getMember()) {
            if (!member.getEmail().equalsIgnoreCase(Qiscus.getQiscusAccount().getEmail())) {
                count++;
                subtitle += member.getUsername().split(" ")[0];
                if (count < qiscusChatRoom.getMember().size() - 1) {
                    subtitle += ", ";
                }
            }
            if (count >= 10) {
                break;
            }
        }
        subtitle += " " + getString(R.string.qiscus_group_member_closing);
        if (count == 0) subtitle = getString(R.string.qiscus_group_member_only_you);
    }

    private void startVideoCall(final QiscusRoomMember target) {
        JSONObject request = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject caller = new JSONObject();
        JSONObject callee = new JSONObject();
        final String roomId = Config.CHAT_APP_ID + "_" + String.valueOf(System.currentTimeMillis());

        try {
            request.put("system_event_type", "custom");
            request.put("room_id",String.valueOf(qiscusChatRoom.getId()));
            request.put("subject_email", target.getEmail());
            request.put("message", Qiscus.getQiscusAccount().getUsername() + " call " + target.getUsername());
            payload.put("type", "webview_call");
            payload.put("call_event", "incoming");
            payload.put("call_room_id", roomId);
            caller.put("username", Qiscus.getQiscusAccount().getEmail());
            caller.put("name", Qiscus.getQiscusAccount().getUsername());
            caller.put("avatar", Qiscus.getQiscusAccount().getAvatar());
            callee.put("username", target.getEmail());
            callee.put("name", target.getUsername());
            callee.put("avatar", target.getAvatar());
            payload.put("call_caller", caller);
            payload.put("call_callee", callee);
            request.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpConnection = new AsyncHttpUrlConnection("POST", "/api/v2/rest/post_system_event_message", request.toString(), new AsyncHttpUrlConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "API connection error: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d(TAG, "API connection success: " + response);
                try {
                    JSONObject objStream = new JSONObject(response);
                    if (objStream.getInt("status") == 200) {
                        Intent intent = new Intent(CustomChatActivity.this, CallActivity.class);
                        intent.putExtra("call_room_id", roomId);
                        intent.putExtra("callee_name", target.getUsername());
                        intent.putExtra("callee_email", target.getEmail());
                        intent.putExtra("callee_avatar", target.getAvatar());
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        httpConnection.setContentType("application/json");
        httpConnection.send();
    }
}
