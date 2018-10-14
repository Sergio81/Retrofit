package com.androidtraining.retrofit;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.androidtraining.retrofit.api.RandomAPI;
import com.androidtraining.retrofit.modules.Location;
import com.androidtraining.retrofit.modules.RandomResponse;
import com.androidtraining.retrofit.modules.Result;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    //region fields
    private static final String BASE_URL = "https://randomuser.me/";
    private static final String TAG = "MainActivity_TAG";

    private Retrofit client;
    private RandomAPI randomAPI;
    private CircleImageView profileImage;
    private ImageButton updateBtn;

    private MapView mapView;
    private GoogleMap gmap;
    private LocationManager locationManager;
    MarkerOptions markerOptions = new MarkerOptions();

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyDV1UaYE4iBtJgyRqAsBptwXu9f0VIi-co";

    private TextView nameTV;
    private EditText genderTV;
    private EditText emailTV;
    private EditText phoneTV;
    private EditText nationalityTV;
    private EditText streetTV;
    private EditText cityTV;
    private EditText StateTV;
    private EditText postCode;

    private LatLng currentAddress;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = prepareRetrofitClient();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        initializeViews();


    }

    private void initializeViews() {
        randomAPI = client.create(RandomAPI.class);

        nameTV = findViewById(R.id.nameTV);
        profileImage = findViewById(R.id.profile_image);
        genderTV = findViewById(R.id.genderTV);
        emailTV = findViewById(R.id.emailTV);
        phoneTV = findViewById(R.id.phoneTV);
        nationalityTV = findViewById(R.id.nationalityTV);
        streetTV = findViewById(R.id.addressTV);
        cityTV = findViewById(R.id.cityTV);
        StateTV = findViewById(R.id.StateTV);
        postCode = findViewById(R.id.postCode);
    }

    private void GetUser() {
        randomAPI.getRandomUser().enqueue(new Callback<RandomResponse>() {
            @Override
            public void onResponse(Call<RandomResponse> call, Response<RandomResponse> response) {
                if(response.isSuccessful()){
                    RandomResponse randomUser = response.body();
                    if(randomUser != null){
                        Result userInfo = randomUser.getResults().get(0);

                        loadUserInfo(userInfo);
                    }
                }
            }

            @Override
            public void onFailure(Call<RandomResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void SetAnimation(){
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);

    }

    public String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private void loadUserInfo(Result userInfo){
        Location location = userInfo.getLocation();
        String strAddress = location.getStreet() + ", " + location.getCity() + ", " + location.getState() + ", " + location.getPostcode();
        Address address = getPositionfromAddress(strAddress);

        nameTV.setText(capitalize(userInfo.getName().getFirst()) + " " + capitalize(userInfo.getName().getLast()));


        genderTV.setText(userInfo.getGender());
        emailTV.setText(userInfo.getEmail());
        phoneTV.setText(userInfo.getPhone());
        nationalityTV.setText(userInfo.getNat());
        streetTV.setText(location.getStreet());
        cityTV.setText(location.getCity());
        StateTV.setText(location.getState());
        postCode.setText(location.getPostcode());

        if(address != null)
            currentAddress = new LatLng(address.getLatitude(), address.getLongitude());
        else
            currentAddress = new LatLng(40.7143528, -74.0059731);

        gmap.clear();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentAddress);
        gmap.animateCamera(cameraUpdate);
        markerOptions.position(currentAddress);
        gmap.addMarker(markerOptions);

        try {
            Picasso.get().load(userInfo
                    .getPicture()
                    .getLarge())
                    .into(profileImage);
        }catch (Exception ex){
            Log.e(TAG, "onResponse: ", ex);
        }
    }

    private Retrofit prepareRetrofitClient(){
        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return client;
    }

    public void updateBtn_onClick(View view) {
        GetUser();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        LatLng sydney = new LatLng(-33.852, 151.211);

        if(currentAddress == null)
            currentAddress = ny;

        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(currentAddress));

        markerOptions.position(currentAddress);
        gmap.addMarker(markerOptions);
        GetUser();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private Address getPositionfromAddress(String address){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> positions = geocoder.getFromLocationName(address, 1);

            if(positions.size() > 0)
                return positions.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
