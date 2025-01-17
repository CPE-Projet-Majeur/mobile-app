package com.example.duellingwands.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.ai.SpellRecognition;
import com.example.duellingwands.model.entities.User;
import com.example.duellingwands.utils.ApplicationStateHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class BattleViewModel extends ViewModel {
    private static final String TAG = "BattleViewModel";
    private Socket socket;
    private int battleId = -1;

    private static float THRESHOLD = 0.7f;
    private SpellRecognition spellRecognition;

    private final MutableLiveData<Boolean> battleStartEvent = new MutableLiveData<>(false);
    private boolean awaitingResponse = false;

    private User player;
    private User opponent;

    private final MutableLiveData<Integer> _playerHp = new MutableLiveData<>(100);
    public final LiveData<Integer> playerHp = _playerHp;
    private final MutableLiveData<Integer> _opponentHp = new MutableLiveData<>(100);
    public final LiveData<Integer> opponentHp = _opponentHp;

    public BattleViewModel() {
        // Initialisation du joueur pour les tests (temporaire)
        player = ApplicationStateHandler.getCurrentUser();

        // Initialiser le Socket.IO
        initializeSocket();
    }

    /**
     * Initialize a Socket.IO connection for communication with the backend for a battle.
     */
    private void initializeSocket() {
        String serverUrl = ApplicationStateHandler.SERVER_URL;

        if (!serverUrl.startsWith("https://") && !serverUrl.startsWith("http://")) {
            Log.e(TAG, "L'URL doit commencer par https:// ou http://");
            return;
        }

        try {
            // Configurer les options avec les paramètres de requête
            IO.Options options = new IO.Options();
            // Ajouter les paramètres de requête sous forme de chaîne
            // TODO : use from shared preferences for user info
            options.query = "userId=" + player.getId()
                    + "&userLastName=" + player.getLastName()
                    + "&userFirstName=" + player.getFirstName()
                    + "&userHouse=" + player.getHouse();

            // Initialiser le socket avec l'URL ngrok et les options
            socket = IO.socket(serverUrl, options);

            // Définir les écouteurs d'événements
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on("BATTLE_START", onBattleStart);
            socket.on("BATTLE_SEND_ACTION", onBattleSendAction);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

            // Connecter le socket
            socket.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Erreur de configuration du Socket.IO", e);
        }
    }

    ///////////////////// SOCKET IO LISTENERS ////////////////////

    private Emitter.Listener onConnect = args -> {
        Log.d(TAG, "Socket.IO connecté");
        // Vous pouvez émettre un événement initial ici si nécessaire
        // par exemple, socket.emit("event_name", data);
        sendBattleWaiting();
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Socket.IO déconnecté");
            battleStartEvent.postValue(false);
        }
    };

    private Emitter.Listener onBattleStart = args -> {
        Log.d(TAG, "Battle start event reçu");
        JSONObject message = (JSONObject) args[0];
        try {
            this.setBattleId(message.getInt("battleId"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        battleStartEvent.postValue(true);
    };

    private Emitter.Listener onBattleSendAction = args -> {
        if (args.length > 0) {
            try {
                JSONArray Array = (JSONArray) args[0];
                for (int i = 0; i < Array.length(); i++) {
                    JSONObject message = Array.getJSONObject(i);
                    int targetId = message.getInt("targetId");
                    double damage = message.getDouble("damage");
                    double remainingHp = message.getDouble("remainingHp");
                    if (targetId == player.getId()) {
                        Log.d(TAG, "Player hit! Damage: " + damage + ", Remaining HP: " + remainingHp);
                        _playerHp.postValue((int) remainingHp);
                    } else {
                        Log.d(TAG, "Opponent hit! Damage: " + damage + ", Target ID: " + targetId + ", Remaining HP: " + remainingHp);
                        _opponentHp.postValue((int) remainingHp);
                    }
                }
                setAwaitingResponse(false);

            } catch (JSONException e) {
                Log.e(TAG, "Erreur lors du parsing des messages BATTLE_SEND_ACTION", e);
            }
        } else {
            Log.e(TAG, "BATTLE_SEND_ACTION reçu sans données");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "Erreur de connexion Socket.IO");
            battleStartEvent.postValue(false);
        }
    };

    /**
     * Get the LiveData event for battle start.
     */
    public LiveData<Boolean> getBattleStartEvent() {
        return battleStartEvent;
    }

    /**
     * Send a battle waiting message to the backend.
     */
    public void sendBattleWaiting() {
        if (socket != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("weather", 0);
                if(this.battleId != -1) {
                    data.put("battleId", this.battleId);
                }
                socket.emit("BATTLE_WAITING", data);
                Log.d(TAG, "Sent message to Socket.IO: " + data.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Failed to construct JSON message", e);
            }
        } else {
            Log.e(TAG, "Cannot send BATTLE_WAITING: socket or battleId is null");
        }
    }

    public void castSpell(Integer spellId, Float confidence) {
        if (socket == null) {
            Log.e("BattleViewModel", "Socket is null, cannot send BATTLE_RECEIVE_ACTION");
            return;
        }
        try {
            JSONObject data = new JSONObject();
            data.put("spellId", spellId);
            data.put("accuracy", confidence);
            data.put("battleId", battleId);
            socket.emit("BATTLE_RECEIVE_ACTION", data);
            setAwaitingResponse(true);
            Log.d("BattleViewModel", "Sent BATTLE_RECEIVE_ACTION to Socket.IO: " + data.toString());
        } catch (JSONException e) {
            Log.e("BattleViewModel", "Failed to construct JSON message for BATTLE_RECEIVE_ACTION", e);
        }
        Log.d("BattleViewModel", "Sort casté : " + spellId + " avec confiance : " + (confidence * 100) + "%");
    }

    /**
     * Close the Socket.IO connection.
     */
    public void closeSocket() {
        if (socket != null) {
            socket.disconnect();
            socket.off(); // Supprime tous les écouteurs
            socket = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        closeSocket();
    }

    /**
     * Set the battle ID and send a waiting message.
     * @param battleId
     */
    public void setBattleId(int battleId) {
        this.battleId = battleId;
        Log.d(TAG, "Battle ID set in ViewModel: " + battleId);
    }

    public void setAwaitingResponse(boolean bool) {
        awaitingResponse = bool;
    }

    public boolean getAwaitingResponse() {
        return awaitingResponse;
    }
}
