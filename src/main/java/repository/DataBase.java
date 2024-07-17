package repository;

import model.User;

public class DataBase {

    static private User[] users = new User[100];

    static private int userCount = 0;

    public static void addUser(User user) {
        users[userCount++] = user;
    }

}
