package repository;

import java.util.Optional;
import model.User;

public class DataBase {

    static private User[] users = new User[100];

    static private int userCount = 0;

    public static void addUser(User user) {
        users[userCount++] = user;
    }

    public static Optional<User> findUserById(String userId) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUserId().equals(userId)) {
                return Optional.of(users[i]);
            }
        }
        return Optional.empty();
    }

}
