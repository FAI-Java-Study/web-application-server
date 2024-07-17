package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.User;

public class DataBase {

    static private List<User> users = new ArrayList<>();

    static private int userCount = 0;

    public static void addUser(User user) {
        users.add(user);
        userCount++;
    }

    public static Optional<User> findUserById(String userId) {
        for (int i = 0; i < userCount; i++) {
            if (users.get(i).getUserId().equals(userId)) {
                return Optional.of(users.get(i));
            }
        }
        return Optional.empty();
    }

    public static List<User> findAll() {
        return users;
    }

}
