package team8.codepath.sightseeingapp.activities;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.PlaceAutocompleteAdapter;
import team8.codepath.sightseeingapp.adapters.PlaceListArrayAdapter;
import team8.codepath.sightseeingapp.models.PlaceModel;

public class CreateTripActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{


    public static final String TAG = "CreateTripActivity";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView actvPlaces;
    private TextView mPlaceDetailsText;
    private TextView mPlaceDetailsAttribution;
    EditText etTripName;
    ImageButton btnClear;

    private ArrayList<PlaceModel> places;
    private PlaceListArrayAdapter aPlaces;
    private ListView lvPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_create_trip);
        setupViews();
        setupPlacesAutoComplete();
    }

    private void setupViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etTripName = (EditText) findViewById(R.id.etTripName);
        btnClear = (ImageButton) findViewById(R.id.btnClear);
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // Setup list of Places within trip
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        places = new ArrayList<>();
        aPlaces = new PlaceListArrayAdapter(this, places);
        lvPlaces.setAdapter(aPlaces);

    }

    private void setupPlacesAutoComplete() {
        actvPlaces = (AutoCompleteTextView) findViewById(R.id.actv_places);
        // Register a listener that receives callbacks when a suggestion has been selected
        actvPlaces.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        ImageButton btnClear = (ImageButton) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvPlaces.setText("");
            }
        });

    }



    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each PlaceModel suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a PlaceModel object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get PlaceModel details for " + placeId);
        }
    };



    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "PlaceModel query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the PlaceModel object from the buffer.
            final Place place = places.get(0);
            PlaceModel newPlace = new PlaceModel();
            newPlace.placeId = place.getId();
            newPlace.name = place.getName().toString();
            aPlaces.add(newPlace);
            Log.i(TAG, "PlaceModel details received: " + place.getName());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
