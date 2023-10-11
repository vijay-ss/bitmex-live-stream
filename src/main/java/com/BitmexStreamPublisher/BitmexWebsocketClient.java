package com.BitmexStreamPublisher;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class BitmexWebsocketClient extends WebSocketClient {

    public BitmexWebsocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public BitmexWebsocketClient(URI serverURI) {
        super(serverURI);
    }

    public BitmexWebsocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshake_data) {
        System.out.println("Opened connection");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
        Publisher bp = new Publisher();
        try {
            Publisher.PublishToGCP(System.getProperty("projectId"), System.getProperty("topicId"), message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "### Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason + " ###");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public static void main(String[] args) throws URISyntaxException {
        Utils.readConfig();
        BitmexWebsocketClient ws = new BitmexWebsocketClient(new URI(
                "wss://ws.bitmex.com/realtime?subscribe=instrument,orderBookL2_25,trade"));
        try {
            ws.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
