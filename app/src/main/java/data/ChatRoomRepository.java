package data;

import com.qiscus.rtc.webviewsample.utils.Action;
import com.qiscus.rtc.webviewsample.utils.model.User;
import com.qiscus.sdk.data.model.QiscusChatRoom;

import java.util.List;

/**
 * Created by fitra on 31/05/18.
 */

public interface ChatRoomRepository {
    void getChatRooms(Action<List<QiscusChatRoom>> onSuccess, Action<Throwable> onError);
    void createChatRoom(User user, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError);
}
