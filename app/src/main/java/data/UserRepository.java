package data;

import com.qiscus.rtc.webviewsample.utils.Action;
import com.qiscus.rtc.webviewsample.utils.model.User;

import java.util.List;

/**
 * Created by fitra on 31/05/18.
 */

public interface UserRepository {
    void login(String name, String email, String password, Action<User> onSuccess, Action<Throwable> onError);
    void getCurrentUser(Action<User> onSuccess, Action<Throwable> onError);
    void getUsers(Action<List<User>> onSuccess, Action<Throwable> onError);
    void updateProfile(String name, Action<User> onSuccess, Action<Throwable> onError);
    void logout();
}
