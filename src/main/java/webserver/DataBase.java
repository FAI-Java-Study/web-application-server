package webserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
public final class DataBase {

	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final Set<User> users = new HashSet<>();

	public static void init() {
		log.debug("init");
	}

	public static boolean addUser(User user) {
		return users.add(user);
	}

	public static Optional<User> findUserByIdAndPassword(String userId, String password) {
		return users.stream()
			.filter(user -> Objects.equals(user.getUserId(), userId)
				&& Objects.equals(user.getPassword(), password))
			.findFirst();
	}
}
