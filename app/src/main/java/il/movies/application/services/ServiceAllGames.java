package il.movies.application.services;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import il.movies.application.models.AllGamesExist;
import il.movies.application.models.State;

public class ServiceAllGames {



    public interface OnAllGamesLoadedListener {
        void onAllGamesLoaded(ArrayList<AllGamesExist> allGames);
    }

    public static void getAllGames(OnAllGamesLoadedListener listener) {

        //makes a single thread to work on the background -> async task
        //when the fetcthing is over we are passing the Array to the fragment that implements that interface and overrides the onAllGamesLoaded
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //fetching the data from the api
                ArrayList<AllGamesExist> allGames = fetchDataFromApi();
                if (listener != null) {
                    //passing the data fetched to the listener, in this case our fragment.
                    listener.onAllGamesLoaded(allGames);
                }
            }
        });
    }
    private static ArrayList<AllGamesExist> arrState = new ArrayList<>();

    private static ArrayList<AllGamesExist> fetchDataFromApi()  {
        arrState.clear();


        String sURL = "https://api.rawg.io/api/games?key=90bb480b90644cf89ad130ca6a4ee42c";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        int i=0;
        try {
            while (i <= 3) {
                //converting the string to URL
                URL url = new URL(sURL);

                //connecting to the url we described above
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                //converting the long string from the website to json
                JsonParser jp = new JsonParser();

                //getting the content from (the json) and parsing it to java objects
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonElement resultsElement = root.getAsJsonObject().get("results");
                String next = root.getAsJsonObject().get("next").toString().replace("\"","");
                JsonArray results = resultsElement.getAsJsonArray();

                for(JsonElement je:results){
                    JsonObject obj = je.getAsJsonObject();
                    int id = obj.get("id").getAsInt();
                    JsonElement nameE = obj.get("name");
                    JsonElement releaseE = obj.get("released");
                    JsonElement imageE = obj.get("background_image");
                    double rating = obj.get("rating").getAsDouble();
                    JsonArray platformsE = obj.get("platforms").getAsJsonArray();
                    ArrayList<String> platfromArr = new ArrayList<>();
                    for(JsonElement platformJe : platformsE){
                        JsonObject platformObject = platformJe.getAsJsonObject();
                        JsonObject platformObject2 = platformObject.get("platform").getAsJsonObject();
                        JsonElement platformE = platformObject2.get("name");
                        String platformName = platformE.getAsString().replace("\"","");
                        platfromArr.add(platformName);
                    }
                    JsonArray genresE = obj.get("genres").getAsJsonArray();
                    ArrayList<String> genresArr = new ArrayList<>();
                    for(JsonElement genresJe : genresE){
                        JsonObject genresObject = genresJe.getAsJsonObject();
                        genresArr.add(genresObject.get("name").getAsString().replace("\"",""));
                    }
                    ArrayList<String> storesArr = new ArrayList<>();
                    JsonArray storesObj = obj.get("stores").getAsJsonArray();

                    for(JsonElement storesJe : storesObj){
                        JsonObject storesObject = storesJe.getAsJsonObject();
                        JsonObject storesE = storesObject.get("store").getAsJsonObject();
                        storesArr.add(storesE.get("name").toString().replace("\"",""));
                    }
                    String name = nameE.toString().replace("\"","");
                    String release = releaseE.toString().replace("\"","");
                    String image = imageE.toString().replace("\"","");
                    arrState.add(new AllGamesExist(id,name,release,image,rating,platfromArr,genresArr,storesArr));
                }
                if(next.equals("null")){
                    return arrState;
                }

                sURL = next;
                i++;
            }

        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return arrState;
    }

}
