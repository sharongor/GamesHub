package il.movies.application.models;

import java.util.List;

public class State {

    private String name;
    private int id;
    private String image;
    private List<GamesArray> list = null;

    private int games_count;

    public State(String name,int guid,String image,List<GamesArray> list,int games_count) {
        this.name = name;
        this.id = guid;
        this.image = image;
        this.list=list;
        this.games_count = games_count;
    }

    public int getGames_count() {
        return games_count;
    }

    public List<GamesArray> getList() {
        return list;
    }

    public String getName() {
        return name;
    }

    public int getId(){
        return id;
    }
    public String getImage(){
        return image;
    }
}
