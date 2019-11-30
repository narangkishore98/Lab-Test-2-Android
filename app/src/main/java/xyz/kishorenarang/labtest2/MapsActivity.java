package xyz.kishorenarang.labtest2;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener , GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GestureDetector.OnDoubleTapListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private List<LatLng> ll = new ArrayList<LatLng>();


    double allDistance = 0;
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override




    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        // Add a marker in Sydney and move the camera
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 4.0f ) );
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnPolylineClickListener(this);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.setOnMarkerClickListener(this);
        //mMap.getUiSettings().ges
    }

    List<MarkerOptions> mol = new ArrayList<MarkerOptions>();



    private void drawDistance(MarkerOptions p1, MarkerOptions p2)
    {
        LatLng midPoint = midPoint(p1.getPosition().latitude, p1.getPosition().longitude, p2.getPosition().latitude, p2.getPosition().longitude);

        IconGenerator iconGen = new IconGenerator(this);
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon(distance(p1.getPosition().latitude, p1.getPosition().longitude, p2.getPosition().latitude, p2.getPosition().longitude)+" KMS"))).
                position(midPoint).anchor(iconGen.getAnchorU(), iconGen.getAnchorV());
        mMap.addMarker(markerOptions);
    }



    private LatLng getPolygonCenterPoint(ArrayList<MarkerOptions> polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i).getPosition());
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }



    private void reload()
    {
        if(mol.size() > 1)
        {
            addPolyLine();

            drawDistance(mol.get(mol.size()-2),mol.get(mol.size()-1));

        }
        if(mol.size()==5)
        {
            //ll.add(ll.get(0));

            mol.add(mol.get(0));
            addPolyLine();
            PolygonOptions po = new PolygonOptions();
            po.clickable(true);
            po.strokeColor(Color.BLACK);
            po.fillColor(Color.BLUE);
            Iterator i = mol.iterator();

            drawDistance(mol.get(mol.size()-2),mol.get(mol.size()-1));

            drawCenter(getPolygonCenterPoint(mol));

            while(i.hasNext())
            {
                po.add(((MarkerOptions) i.next()).getPosition());
            }
            Polygon p = mMap.addPolygon(po);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {

        if(mol.size()<5)
        {
            Log.e("LAT", latLng.latitude+"");
            MarkerOptions mo = new MarkerOptions();
            mo.position(latLng);
            mo.title("Marker "+mol.size());
            mMap.addMarker(mo);
            //ll.add(latLng);
            mol.add(mo);
            reload();

        }
        else
        {
            //ll.add(ll.get(0));
            addPolyLine();
        }






    }


    public static LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //print out in degrees
        System.out.println(Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));

        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));

    }


    private  String distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return "0";
        }
        else {
            String unit  = "K";
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            DecimalFormat df = new DecimalFormat("#.##");
            ;

            allDistance += dist;
            return (df.format(dist));
        }
    }



    private  void addPolyLine()
    {
        PolylineOptions po = new PolylineOptions();
        po.clickable(true);
        Iterator i = mol.iterator();
        while(i.hasNext())
        {
            while(i.hasNext())
            {
                po.add(((MarkerOptions) i.next()).getPosition());
            }
        }

        Polyline pl = mMap.addPolyline(po);
    }
    private LatLng[] getLatLng()
    {
        LatLng[] latLngs = new LatLng[ll.size()];
        for(int i=0;i<latLngs.length;i++)
        {
            latLngs[i] = ll.get(i);
        }
        return latLngs;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {



    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {

        ll.add(ll.get(0));
        addPolyLine();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        marker.remove();

        return false;
    }
}
