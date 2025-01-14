package com.example.duellingwands.viewmodel;

import static com.example.duellingwands.utils.ApplicationStateHandler.SERVER_URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.ai.SpellRecognition;
import com.example.duellingwands.utils.ApplicationStateHandler;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class BattleViewModel extends ViewModel {

    private SpellRecognition spellRecognition;

    private final MutableLiveData<Integer> playerHp = new MutableLiveData<>(100);
    private final MutableLiveData<Integer> opponentHp = new MutableLiveData<>(100);

    private WebSocket socket;
    private final WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            Log.d("WebSocket", "Connection opened");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("WebSocket", "Received message: " + text);
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.d("WebSocket", "Closing connection");
        }
        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d("WebSocket", "Connection closed");
        }
    };

    private int weather = 0;

    public BattleViewModel() {
        // this.socket = SocketManager.initializeSocket();
        // handshake : je transmes weather + userId
    }

//    public void processSpell(){
//
//    }

    /**
     * Initialize a socket for communication with the backend for a battle.
     * @return
     */
    public WebSocket initializeSocket(){
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("wss") // Secure WebSocket
                .host(ApplicationStateHandler.SERVER_URL)
                //.addPathSegment("socket")
                .addQueryParameter("userId", "123")
                .addQueryParameter("token", "abcdef")
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                //.addHeader("Authorization", "Bearer your_token_here")
                .build();
        WebSocket webSocket = client.newWebSocket(request, this.webSocketListener);
        ApplicationStateHandler.setSocket(webSocket);
        return webSocket;
    }

    // -----------------------------------------

    // Getters et Setters
    public LiveData<Integer> getPlayerHp() {
        return playerHp;
    }

    public LiveData<Integer> getOpponentHp() {
        return opponentHp;
    }

    public void updatePlayerHp(int hp) {
        playerHp.setValue(hp);
    }

    public void updateOpponentHp(int hp) {
        opponentHp.setValue(hp);
    }
}
