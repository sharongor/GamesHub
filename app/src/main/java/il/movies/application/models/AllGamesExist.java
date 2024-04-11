package il.movies.application.models;

import java.util.List;

public class AllGamesExist {

    private int id;
    private String name;
    private String dateReleased;
    private String image;
    double rating;
    private List<String> platforms = null;

    private List<String> genres = null;
    private List<String> stores = null;

    public AllGamesExist(int id, String name, String dateReleased, String image, double rating, List<String> platforms, List<String> genres, List<String> stores) {
        this.id = id;
        this.name = name;
        this.dateReleased = dateReleased;
        this.image = image;
        this.rating = rating;
        this.platforms = platforms;
        this.genres = genres;
        this.stores = stores;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateReleased() {
        return dateReleased;
    }

    public String getImage() {
        return image;
    }

    public double getRating() {
        return rating;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getStores() {
        return stores;
    }
}
