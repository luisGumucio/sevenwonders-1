package org.fundacionjala.sevenwonders.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.SpringRouteBuilder;
import org.fundacionjala.sevenwonders.beans.GameRoomService;
import org.fundacionjala.sevenwonders.core.rest.GameRoomModel;
import org.fundacionjala.sevenwonders.core.rest.PlayerModel;
import org.springframework.stereotype.Component;

/**
 * Created by Unkon on 8/29/2016.
 */
@Component
public class LobbyRoute extends SpringRouteBuilder {
    @BeanInject("gameRoomService")
    GameRoomService gameRoomService;
    @Override
    public void configure() throws Exception {
        from("websocket://localhost:9294/lobbyChannel")
                .log("${body}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(gameRoomService.listGameRooms(), GameRoomModel.class);
                    }
                })
                .to("websocket://localhost:9294/lobbyChannel?sendToAll=true");
    }
}
