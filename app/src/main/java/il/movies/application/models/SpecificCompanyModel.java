package il.movies.application.models;

public class SpecificCompanyModel {

    private String name;
    private String image_background;
    private String description;

    private int game_count;

    public SpecificCompanyModel(String name, String image_background, String description,int game_count) {
        this.name = name;
        this.image_background = image_background;
        this.description = description;
        this.game_count = game_count;
    }

    public SpecificCompanyModel(){
        this.name="";
        this.image_background="";
        this.description="";
    }

    public int getGame_count() {
        return game_count;
    }

    public String getName() {
        return name;
    }

    public String getImage_background() {
        return image_background;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGame_count(int game_count) {
        this.game_count = game_count;
    }
}
