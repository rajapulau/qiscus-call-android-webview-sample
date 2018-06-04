package com.qiscus.rtc.webviewsample;

import android.content.Context;

import data.ChatRoomRepository;
import data.ChatRoomRepositoryImpl;
import data.UserRepository;
import data.UserRepositoryImpl;

/**
 * Created by fitra on 31/05/18.
 */

public class AppComponent {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    AppComponent(Context context){
        userRepository = new UserRepositoryImpl(context);
        chatRoomRepository = new ChatRoomRepositoryImpl();
    }

    public ChatRoomRepository getChatRoomRepository() {
        return chatRoomRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
