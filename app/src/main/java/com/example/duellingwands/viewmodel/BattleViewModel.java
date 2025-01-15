package com.example.duellingwands.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.entities.User;
import com.example.duellingwands.utils.ApplicationStateHandler;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class BattleViewModel extends ViewModel {
    private static final String TAG = "BattleViewModel";
    private Socket socket;
    private int battleId;

    private final MutableLiveData<Boolean> battleStartEvent = new MutableLiveData<>(false);

    private User player;
    private User opponent;

    private final MutableLiveData<Integer> _playerHp = new MutableLiveData<>(100);
    public final LiveData<Integer> playerHp = _playerHp;
    private final MutableLiveData<Integer> _opponentHp = new MutableLiveData<>(100);
    public final LiveData<Integer> opponentHp = _opponentHp;

    public BattleViewModel() {
        // Initialisation du joueur pour les tests
        player = new User();
        player.setId(1);
        player.setFirstName("a");
        player.setLastName("b");
        player.setEmail("a.b@email.fr");
        player.setAccount(10);
        player.setHouse("GRYFFINDOR");

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
            options.query = "userId=" + player.getId()
                    + "&userLastName=" + player.getLastName()
                    + "&userFirstName=" + player.getFirstName()
                    + "&userHouse=" + player.getHouse();

            // Initialiser le socket avec l'URL ngrok et les options
            socket = IO.socket(serverUrl, options);

            // Définir les écouteurs d'événements
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on("BATTLE_START", onBattleStart); // Événement personnalisé
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

            // Connecter le socket
            socket.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Erreur de configuration du Socket.IO", e);
        }
    }

    // Écouteurs d'événements Socket.IO

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

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length > 0) {
                String message = (String) args[0];
                Log.d(TAG, "Message reçu : " + message);

                // Traitement des messages génériques si nécessaire
                // Par exemple, parser le JSON et mettre à jour les LiveData
                try {
                    JSONObject json = new JSONObject(message);
                    // Supposons que le JSON a un champ "action"
                    String action = json.getString("action");
                    if ("BATTLE_START".equals(action)) {
                        battleStartEvent.postValue(true);
                    } else if ("UPDATE_HP".equals(action)) {
                        JSONObject data = json.getJSONObject("data");
                        int playerHpValue = data.getInt("playerHp");
                        int opponentHpValue = data.getInt("opponentHp");
                        _playerHp.postValue(playerHpValue);
                        _opponentHp.postValue(opponentHpValue);
                    }
                    // Ajoutez d'autres actions selon vos besoins
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing du message JSON", e);
                }
            }
        }
    };

    private Emitter.Listener onBattleStart = args -> {
        Log.d(TAG, "Battle start event reçu");
        battleStartEvent.postValue(true);
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
                JSONObject message = new JSONObject();
                message.put("action", "BATTLE_WAITING");

                JSONObject data = new JSONObject();
                data.put("battleId", battleId);
                data.put("weather", 0); // Exemple de donnée, ajustez selon vos besoins

                message.put("data", data);

                //socket.emit("BATTLE_WAITING", data.toString());
                socket.emit("BATTLE_WAITING", data);
                Log.d(TAG, "Sent message to Socket.IO: " + data.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Failed to construct JSON message", e);
            }
        } else {
            Log.e(TAG, "Cannot send BATTLE_WAITING: socket or battleId is null");
        }
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
}
