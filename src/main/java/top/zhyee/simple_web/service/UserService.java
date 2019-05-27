package top.zhyee.simple_web.service;

import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import rx.Single;
import top.zhyee.simple_web.entity.User;

/**
 * @author zhyee
 */
public class UserService {

    private final JDBCClient jdbcClient;

    public UserService(JDBCClient client) {
        jdbcClient = client;
    }

    public Single<Boolean> addUser(User user) {
        String sql = "insert into test values (?, ?, ?)";

        return jdbcClient.rxQuerySingleWithParams(sql, new JsonArray().add(user.getId()).add(user.getName()).add(user.getAge()))
            .onErrorReturn(t -> new JsonArray())
            .map(it -> true);
    }

    public Single<User> getUser(String id) {
        String sql = "select id, name, age from test where id = ?";
        return jdbcClient.rxQuerySingleWithParams(sql, new JsonArray().add(id))
            .onErrorReturn(t -> new JsonArray())
            .map(it -> {
                User user = new User();
                user.setId(String.valueOf(it.getInteger(0)));
                user.setName(it.getString(1));
                user.setAge(it.getInteger(0));
                return user;
            });
    }
}
