package org.fundacionjala.sevenwonders.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spring.SpringRouteBuilder;
import org.fundacionjala.sevenwonders.beans.GameRoomService;
import org.fundacionjala.sevenwonders.beans.LoginService;
import org.fundacionjala.sevenwonders.core.GameRoom;
import org.fundacionjala.sevenwonders.core.Player;
import org.fundacionjala.sevenwonders.core.rest.*;
import org.springframework.stereotype.Component;

/**
 * This registry the principal routes(GET, SET, PUT, DELETE) of game rooms
 *
 * @author Juan Barahona
 */
@Component
public class GameRoomRoute extends SpringRouteBuilder {

    @BeanInject("gameRoomService")
    GameRoomService gameRoomService;
    @BeanInject("loginService")
    LoginService loginService;

    @Override
    public void configure() throws Exception {

        restConfiguration().component("jetty")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .port(9999);

        rest("/gameRoom").description("Lobby rest service")
                .consumes("application/json").produces("application/json")
                .post("games/create").description("Create a new game room").type(GameRoomModel.class)
                .to("bean:gameRoomService?method=createGameRoom")
                .post("/player").description("Add Player to lobby game").type(PlayerModel.class)
                .to("bean:gameRoomService?method=addPlayer")
                .get("/players/{id}").description("Get list of players").outTypeList(Player.class)
                .to("bean:gameRoomService?method=getPlayers(${header.id})")
                .get("games/{id}").description("Get a game room").type(GameRoom.class)
                .to("bean:gameRoomService?method=getGameRoom(${header.id})")
                .post("/login").description("Login from server").type(LoginModel.class)
                .to("bean:loginService?method=isLogged")
                .get("/games").description("Login from server").outTypeList(GameRoomModel.class)
                .to("bean:gameRoomService?method=listGameRooms");

        rest("/gameRoom").id("rest-options")
                .verb("options").route()
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .setHeader("Access-Control-Allow-Methods", constant("GET, HEAD, POST, PUT, DELETE, OPTIONS"))
                .setHeader("Access-Control-Allow-Headers", constant("Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"))
                .setHeader("Allow", constant("GET, HEAD, POST, PUT, DELETE, OPTIONS"));

    }
}
