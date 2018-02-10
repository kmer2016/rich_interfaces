package fr.enssat.chaka.rich_interfaces;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    //String urlVideo = "https://archive.org/download/Route_66_-_an_American_badDream/Route_66_-_an_American_badDream_512kb.mp4";

    MapView mapView;
    VideoView video;
    MediaController mc;
    TextView textView;
    WebView webView;
    Spinner spinner;

    ProgressBar progressBar;

    JsonParse jsonParse;
    InputStream inputStream;

    Film film;
    ArrayList<Chapter> chaptersArrayList;
    ArrayList<WayPoint> wayPointsArrayList;
    ArrayList<Keyword> keywordsArrayList;
    ArrayList<Marker> markerArrayList;

    Timer timer;
    int currentMarkerIndex = 0;
    BitmapDescriptor markerDefaultColor;
    BitmapDescriptor markerVideoPositionColor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate  (savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(getString(R.string.MAPVIEW_BUNDLE_KEY));
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        /*Binding des composants de la vue*/
        video = findViewById(R.id.videoView);
        textView = findViewById(R.id.title);
        webView = findViewById(R.id.webView);
        //filmLayout = findViewById(R.id.filmLayout);
        spinner = findViewById(R.id.spinner);

        inputStream = getResources().openRawResource(R.raw.chapters);
        jsonParse  = new JsonParse(inputStream);
        film = jsonParse.getFilm();


        textView.setText(film.getTitle());

        video.setVideoURI(Uri.parse(film.getFilm_url()));
        mc = new MediaController(MainActivity.this);
        video.setMediaController(mc);
        video.requestFocus();

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(film.getSynopsis_url());

        markerDefaultColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        markerVideoPositionColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        chaptersArrayList = jsonParse.getChaptersList();
        wayPointsArrayList = jsonParse.getWaypointsList();
        keywordsArrayList = jsonParse.getKeywordsList();


        ArrayAdapter<Chapter> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, chaptersArrayList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Chapter chapter = chaptersArrayList.get(i);
                video.seekTo(chapter.getPosition());
                //video.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long duration = video.getDuration();
                mc.setAnchorView(video);
                video.start();
                startTimer();
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer mp){
                timer.cancel();
            }
        });

        initMap();

    }

    private void initMap(){
        markerArrayList = new ArrayList<Marker>();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                for (WayPoint wp : wayPointsArrayList) {

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(wp.getLat(), wp.getLng()))
                            .title(wp.getLabel())
                            .icon(markerDefaultColor));
                    marker.setTag(wp.getTimestamp());

                    markerArrayList.add(marker);
                }
                markerArrayList.get(0).setIcon(markerVideoPositionColor);

                LatLng latLng = new LatLng(wayPointsArrayList.get(0).getLat(),wayPointsArrayList.get(0).getLng());
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int timestamp = (int)marker.getTag();
                        video.seekTo(timestamp);
                        return false;
                    }
                });
            }
        });
    }

    private void startTimer(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (video.isPlaying()){
                            trackVideoView();
                        }
                    }
                });
            }
        };
        timer.schedule(task,0,1000);
    }

    private void trackVideoView(){
        int currentTime = video.getCurrentPosition();

        int nextChapterIndex = getChapterIndexByTime(currentTime);
        int nextMarkerIndex  = getMarkerIndexByTime(currentTime);
        int nextKeyWordIndex = getKeyWordIndexByTime(currentTime);

        spinner.setSelection(nextChapterIndex,true);
        //textView.setText(keywordsArrayList.get(nextKeyWordIndex).getData().get(0).getTitle());
        //webView.loadUrl(keywordsArrayList.get(nextKeyWordIndex).getData().get(0).getUrl());
        if (nextMarkerIndex != currentMarkerIndex){
            markerArrayList.get(currentMarkerIndex).setIcon(markerDefaultColor);
            markerArrayList.get(nextMarkerIndex).setIcon(markerVideoPositionColor);
            currentMarkerIndex = nextMarkerIndex;
        }
    }

    private int getChapterIndexByTime(int time){
        int i = 0;
        int current = 0;

        while (i < chaptersArrayList.size() && chaptersArrayList.get(i).getPosition() <= time ){
            current = i;
            i++;
        }
        return current;
    }

    private int getMarkerIndexByTime(int time){
        int i = 0;
        int current = 0;

        while (i < markerArrayList.size() && (int)markerArrayList.get(i).getTag() <= time ){
            current = i;
            i++;
        }
        return current;
    }

    private int getKeyWordIndexByTime(int time){
        int i = 0;
        int current = 0;

        while (i < keywordsArrayList.size() && keywordsArrayList.get(i).getPosition() <= time ){
            current = i;
            i++;
        }
        return current;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(getString(R.string.MAPVIEW_BUNDLE_KEY));
        if(mapViewBundle == null){
            mapViewBundle = new Bundle();
            outState.putBundle(getString(R.string.MAPVIEW_BUNDLE_KEY),mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
