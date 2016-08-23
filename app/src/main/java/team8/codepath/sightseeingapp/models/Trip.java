package team8.codepath.sightseeingapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by floko_000 on 8/18/2016.
 */
@Parcel
public class Trip {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");

    public String id;
    public String name;
    public String totalLength;
    public String bannerPhoto;
    public ArrayList<String> places;
    public static JSONArray jsonArray;
    public String distance;

    public Trip(){}

    public Trip(String id, String name, String totalLength, String bannerPhoto, ArrayList<String> places){
        this.id = id;
        this.name = name;
        this.totalLength = totalLength;
        this.bannerPhoto = bannerPhoto;
        this.places = places;
    }
    public String getBannerPhoto() {
        return bannerPhoto;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTotalLength() {
        return totalLength+"";
    }

    public String getDistance() {
        return distance;
    }


    public ArrayList<String> getPlaces() {
        return places;
    }

    // Output list of tweets from jsonarray.
    //public static ArrayList<Place> fromJSONArray(JSONArray jsonArray){

    public static ArrayList<Trip> fromJSONArray(){
        // Mock Data
        jsonArray = new JSONArray();
        ArrayList<String> a = new ArrayList<String>();
        a.add("1");
        a.add("2");
        //            jsonArray.put(new JSONObject("{\"id\":\"1\", \"name\":\"Best of Manhattan Ice Cream\", \"distance\":\"11.1 miles \", \"totalLength\": 12, \"bannerPhoto\": \"http://feliciarogersauthor.weebly.com/uploads/1/2/6/7/12672742/9911388_orig.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
//            jsonArray.put(new JSONObject("{\"id\":\"2\", \"name\":\"Staten Island Treats\", \"distance\":\"6.5 miles \", \"totalLength\": 1.5, \"bannerPhoto\": \"http://67.media.tumblr.com/560d23f751255935a78173eb25941a5e/tumblr_nrp7ewTL1Z1ttdrv5o1_1280.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
//            jsonArray.put(new JSONObject("{\"id\":\"3\", \"name\":\"Alphabet City Sweets\", \"distance\":\"3.1 miles \", \"totalLength\": 21, \"bannerPhoto\": \"http://www.billsseafood.com/wp-content/uploads/2015/04/Bills-Seafood-Restaurant-Westbrook-CT-Ice-Cream-Shop-and-Gift-Shop.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
//            jsonArray.put(new JSONObject("{\"id\":\"4\", \"name\":\"Hell's Kitchen Delights\", \"distance\":\"2 miles \", \"totalLength\": 3, \"bannerPhoto\": \"http://www.cafeinteriordesign.com/gallery/ice-cream-shop/ice-cream-shop-20.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
        Trip t = new Trip("1", "Best of Manhattan Ice Cream", "11.1 miles", "http://feliciarogersauthor.weebly.com/uploads/1/2/6/7/12672742/9911388_orig.jpg", a);
        mDatabase.child("1").setValue(t);
        ArrayList<Trip> trips = new ArrayList<>();

        return trips;
    }

    // Deserialize the JSON and build Tweet Objects
    // public static Place fromJSON(JSONObject jsonObject){
    public static Trip fromJSON(JSONObject jsonObject){
        Trip trip = new Trip();

        try {
            trip.id = jsonObject.getString("id");
            trip.name = jsonObject.getString("name");
            trip.totalLength = jsonObject.getString("totalLength");
            trip.bannerPhoto = jsonObject.getString("bannerPhoto");
            trip.distance = jsonObject.getString("distance");
            ArrayList<String> listdata = new ArrayList<String>();
            JSONArray jArray = jsonObject.getJSONArray("places");
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    Place p = new Place();
                    p.name = jArray.get(i).toString();
                    p.hours = "M-F 9-5";
                    listdata.add(String.valueOf(p));
                }
            }
            trip.places = listdata;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trip;
    }




}
