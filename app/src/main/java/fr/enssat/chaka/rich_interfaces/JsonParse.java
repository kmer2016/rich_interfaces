package fr.enssat.chaka.rich_interfaces;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by chaka on 11/01/18.
 */

public class JsonParse {
    private InputStream inputStream;
    private ByteArrayOutputStream byteArrayOutputStream;

    private Film film;
    private ArrayList<Chapter> chaptersList;
    private ArrayList<Keyword> keywordsList;
    private ArrayList<WayPoint> waypointsList;


    public JsonParse(InputStream ip){
        this.inputStream = ip;
        byteArrayOutputStream = new ByteArrayOutputStream();

        film = new Film();

        chaptersList = new ArrayList<Chapter>();
        keywordsList = new ArrayList<Keyword>();
        waypointsList = new ArrayList<WayPoint>();

        jsonToArrayObject();
    }

    private void setFilmFromJson(JSONObject filmJson){

        try{
            film.setFilm_url(filmJson.getString("file_url"));
            film.setSynopsis_url(filmJson.getString("synopsis_url"));
            film.setTitle(filmJson.getString("title"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setChaptersListFromJson(JSONArray chaptersListJson){
        try{
            for (int i = 0; i < chaptersListJson.length(); i++) {
                JSONObject chapter = chaptersListJson.getJSONObject(i);

                this.chaptersList.add(new Chapter(chapter.getString("title"), chapter.getInt("pos")*1000));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setWayPointsListFromJson(JSONArray waypointsListJson){
        try{
            for (int i = 0; i < waypointsListJson.length(); i++) {
                JSONObject waypointJson = waypointsListJson.getJSONObject(i);
                float lat = BigDecimal.valueOf(waypointJson.getDouble("lat")).floatValue();
                float lng = BigDecimal.valueOf(waypointJson.getDouble("lng")).floatValue();
                String label = waypointJson.getString("label");
                int timestamp = waypointJson.getInt("timestamp");

                this.waypointsList.add(new WayPoint (lat,lng,label,timestamp*1000));
            }
        }catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private void setKeywordsListFromJson(JSONArray keywordsListJson){
        try{
            for (int i = 0; i < keywordsListJson.length(); i++) {
                Keyword keyword = new Keyword();

                JSONObject keywordJson = keywordsListJson.getJSONObject(i);
                keyword.setPosition(keywordJson.getInt("pos")*1000);

                JSONArray dataJson = keywordJson.getJSONArray("data");

                for (int index = 0; index < dataJson.length(); index++ ){
                    DatumKeyword datum = new DatumKeyword();
                    JSONObject datumJson = dataJson.getJSONObject(index);

                    datum.setTitle(datumJson.getString("title"));
                    datum.setUrl(datumJson.getString("url"));

                    keyword.getData().add(datum);
                }
                this.keywordsList.add(keyword);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void jsonToArrayObject(){
        JSONObject json;

        try {
            int element = this.inputStream.read();
            while (element != -1){
                this.byteArrayOutputStream.write(element);
                element = this.inputStream.read();
            }
            this.inputStream.close();
            try {
                json = new JSONObject(this.byteArrayOutputStream.toString());

                this.setFilmFromJson(json.getJSONObject("Film"));
                this.setChaptersListFromJson(json.getJSONArray("Chapters"));
                this.setKeywordsListFromJson(json.getJSONArray("Keywords"));
                this.setWayPointsListFromJson(json.getJSONArray("Waypoints"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Chapter> getChaptersList() {
        return chaptersList;
    }

    public Film getFilm() {
        return film;
    }

    public ArrayList<Keyword> getKeywordsList() {
        return keywordsList;
    }

    public ArrayList<WayPoint> getWaypointsList() {
        return waypointsList;
    }
}
