package top.zhyee.simple_web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import top.zhyee.simple_web.controller.UserController;
import top.zhyee.simple_web.entity.User;
import top.zhyee.simple_web.service.UserService;

public class MainVerticle extends AbstractVerticle {

    private JDBCClient jdbcClient;
    private UserService userService;
    private UserController userController;

    public static void main(String[] args) {
        new Launcher().dispatch(args);
    }

    @Override
    public void start() {
        jdbcClient = initJdbc();
        userService = new UserService(jdbcClient);
        userController = new UserController(userService);

        Router router = Router.router(Vertx.vertx());
        router.route().handler(BodyHandler.create());
        router.post("/user/:id").handler(this::handlePostUser);
        router.get("/user/:id").handler(this::handleGetUser);
        Vertx.vertx().createHttpServer().requestHandler(router).listen(8089);
    }

    private void handlePostUser(RoutingContext routingContext) {
        rx.Single.just(routingContext)
            .map(r -> {
                User user = new User();
                user.setId(routingContext.request().getParam("id"));
                user.setName(routingContext.request().getParam("name"));
                user.setAge(Integer.valueOf(routingContext.request().getParam("age")));
                return user;
            })
            .flatMap(userController::addUser)
            .map(u -> {
                routingContext.response().end("OK");
                return null;
            })
            .subscribe();
    }

    private void handleGetUser(RoutingContext routingContext) {
        userController.getUser(routingContext.request().getParam("id"));
        rx.Single.just(routingContext)
            .map(r -> r.request().getParam("id"))
            .flatMap(userController::getUser)
            .map(user -> {
                routingContext.response().end(user.toString());
                return null;
            })
            .subscribe();
    }

    private JDBCClient initJdbc() {
        return JDBCClient.createShared(Vertx.vertx(), new JsonObject()
            .put("url", "jdbc:mysql://127.0.0.1:3306/test")
            .put("driver_class", "com.mysql.jdbc.Driver")
            .put("max_pool_size", 30)
            .put("user", "root")
            .put("password", "dangerous"));
    }
}
