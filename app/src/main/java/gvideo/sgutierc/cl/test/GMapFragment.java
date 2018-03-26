package gvideo.sgutierc.cl.test;

/**
 * Created by sgutierc on 23-03-2018.
 */

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import gvideo.sgutierc.cl.videorecorder.R;

public class GMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationHandler locationHandler;
    @Override
    public void onStart() {
        super.onStart();
        locationHandler=new LocationHandler(this.getActivity());
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    private LatLng myLocation;
    private Marker thisIsMe;

    public void setMyLocation(LatLng location) {
        if (thisIsMe == null) {
            MarkerOptions markerOpt = new MarkerOptions().position(location).title("Marker in Sydney");
            thisIsMe = mMap.addMarker(markerOpt);
        }
        else
            thisIsMe.setPosition(location);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationHandler.startListening(this);
    }

    public static GMapFragment newInstance() {
        return new GMapFragment();
    }
}
