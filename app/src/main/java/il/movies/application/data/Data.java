package il.movies.application.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import il.movies.application.R;

public class Data {
    public String id;
    public String email;
    public String pass;
    public String phone;

    public String name;
    public List<String> favorites;
    public String profilePicture;
    public boolean isPrivate;



    public Data(String id, String email, String pass, String phone, String name,String profile,boolean isPrivate) {
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.name = name;
        this.favorites = new ArrayList<String>();
        this.favorites.add("");
        //default profile picture
        this.profilePicture = profile;
        this.isPrivate = isPrivate;
    }

    //must be an empty Constructor for the firebase to store elements
    public Data(){}

}
