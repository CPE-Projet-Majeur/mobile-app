package com.example.duellingwands.utils;

import android.util.Log;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketManager {

//    private static String SERVER_URL = "ws://example.com/socket";
//    private static WebSocketListener webSocketListener = new WebSocketListener() {
//        @Override
//        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
//            Log.d("WebSocket", "Connection opened");
//        }
//        @Override
//        public void onMessage(WebSocket webSocket, String text) {
//            Log.d("WebSocket", "Received message: " + text);
//        }
//        @Override
//        public void onClosing(WebSocket webSocket, int code, String reason) {
//            Log.d("WebSocket", "Closing connection");
//        }
//        @Override
//        public void onClosed(WebSocket webSocket, int code, String reason) {
//            Log.d("WebSocket", "Connection closed");
//        }
//    };

//    public static WebSocket initializeSocket(WebSocketListener webSocketListener){
//        OkHttpClient client = new OkHttpClient();
//        HttpUrl httpUrl = new HttpUrl.Builder()
//                .scheme("wss") // Secure WebSocket
//                .host(SERVER_URL)
//                //.addPathSegment("socket")
//                .addQueryParameter("userId", "123")
//                .addQueryParameter("token", "abcdef")
//                .build();
//        Request request = new Request.Builder()
//                .url(httpUrl)
//                //.addHeader("Authorization", "Bearer your_token_here")
//                .build();
//        return client.newWebSocket(request, webSocketListener);
//    }
}
