package com.example.duellingwands.model.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.duellingwands.model.entities.POI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Repository for POIs fetching from Overpass API.
 * @apiNote Temporary until a proper database is implemented
 * @implNote Is a singleton to avoid multiple instances of the same repository
 */
public class POIRepository {

    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";
    private static final List<String> POI_TYPES = List.of("cafe", "restaurant", "bar");
    private final OkHttpClient client = new OkHttpClient();
    private static POIRepository instance = null;

    private POIRepository(){

    }

    public interface POICallback {
        void onSuccess(List<POI> poiList);
        void onFailure(Exception e);
    }

    public static POIRepository getInstance(){
        if(instance == null){
            instance = new POIRepository();
        }
        return instance;
    }

    public void getPOIs(double latitude, double longitude, int radius, POICallback callback){
        String query = "[out:json][timeout:25];" +'(';
        for (String type : POI_TYPES) {
            query += "node[" + type + "](around:" + radius + "," + latitude + "," + longitude + ");";
        }
        query += ");out body;";
        String url = OVERPASS_API_URL + "?data=" + Uri.encode(query);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Réponse inattendue : " + response));
                    return;
                }
                Log.d("POIRepository", "POIs fetched");
                String responseBody = response.body().string();
                List<POI> poiList = parsePOIFromJson(responseBody);
                callback.onSuccess(poiList);
            }
        });
    }

    private List<POI> parsePOIFromJson(String json) {
        List<POI> poiList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray elements = jsonObject.getJSONArray("elements");
            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                String type = element.getString("type");
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");
                JSONObject tags = element.optJSONObject("tags");
                String name = tags != null ? tags.optString("name", "Unknown") : "Unknown";
                poiList.add(new POI(name, lat, lon, type));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poiList;
    }

}
