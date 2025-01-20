package com.example.duellingwands.model.repository;

import android.util.Log;

import com.example.duellingwands.model.entities.User;
import com.example.duellingwands.utils.ApplicationStateHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;

public class UserRepository {
    private static final String API_URL = ApplicationStateHandler.SERVER_URL + "/users";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static UserRepository instance = null;

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Créez un gestionnaire de confiance qui ne valide aucune chaîne de certificats
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // Optionnel : augmenter les délais
            builder.connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS);
            builder.readTimeout(30, java.util.concurrent.TimeUnit.SECONDS);
            builder.writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private UserRepository(){

    }

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    public User getById(int userId) {
        String url = API_URL + "/" + userId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Log.d("UserRepository", "Request URL: " + url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if (response.body() == null) {
                return null;
            }
            String jsonResponse = response.body().string();
            Log.d("UserRepository", "JSON Response: " + jsonResponse);

            // Utilisation de Jackson pour parser le JSON en JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            // Exemples d'extraction des champs, adapter selon le format de votre API
            int id = rootNode.get("id").asInt();
            String firstName = rootNode.get("firstName").asText();
            String lastName = rootNode.get("lastName").asText();
            String email = rootNode.get("email").asText();
            float account = (float) rootNode.get("account").asDouble();
            // Pour l'attribut house, supposons qu'il s'agit d'une chaîne à convertir en enum ou objet House
            // Par exemple :
            String house = rootNode.get("house").asText().toUpperCase();

            // Création de l'instance User du modèle métier
            return new User(id, firstName, lastName, account, house, email);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
