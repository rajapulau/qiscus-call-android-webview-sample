package com.qiscus.rtc.webviewsample;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.qiscus.rtc.webviewsample.utils.adapter.ChatRoomAdapter;
import com.qiscus.rtc.webviewsample.utils.adapter.OnItemClickListener;
import com.qiscus.rtc.webviewsample.utils.presenter.HomePresenter;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.ui.QiscusChatActivity;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements HomePresenter.View, OnItemClickListener {
    private static final String TAG = ContactActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        chatRoomAdapter = new ChatRoomAdapter(this);
        chatRoomAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(chatRoomAdapter);

        homePresenter = new HomePresenter(this,
                SampleApplication.getInstance().getComponent().getChatRoomRepository(),
                SampleApplication.getInstance().getComponent().getUserRepository());
    }

    @Override
    protected void onResume() {
        super.onResume();
        homePresenter.loadChatRooms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_chat) {
            homePresenter.createChatRoom();
        } else if (item.getItemId() == R.id.logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure wants to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> homePresenter.logout())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        homePresenter.openChatRoom(chatRoomAdapter.getData().get(position));
    }

    @Override
    public void showChatRooms(List<QiscusChatRoom> chatRooms) {
        chatRoomAdapter.addOrUpdate(chatRooms);
    }

    @Override
    public void showChatRoomPage(QiscusChatRoom chatRoom) {
//        startActivity(QiscusChatActivity.generateIntent(this, chatRoom));
        startActivity(CustomChatActivity.generateIntent(this, chatRoom));
    }

    @Override
    public void showContactPage() {
        startActivity(new Intent(this, ContactListActivity.class));
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMainPage() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
