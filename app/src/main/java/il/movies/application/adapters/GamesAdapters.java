package il.movies.application.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import il.movies.application.R;
import il.movies.application.models.AllGamesExist;

public class GamesAdapters extends RecyclerView.Adapter<GamesAdapters.MyViewHolder> {
    public interface favoriteClicked{
        void onItemClicked(AllGamesExist element);
    }
    ArrayList<AllGamesExist> dataset;
    Context context;
    private static favoriteClicked callback;
    List<String> favorites;


    //Handling when user is clicking on the specific heart button






    public void setList(ArrayList<AllGamesExist> list){
        this.dataset = list;
        notifyDataSetChanged();
    }

    public GamesAdapters(ArrayList<AllGamesExist> list, Context context,List<String> favorites, favoriteClicked callback){
        this.dataset = list;
        this.context = context;
        this.callback = callback;
        this.favorites = favorites;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView release;
        TextView rating;
        TextView platforms;
        TextView genres;
        TextView stores;
        ImageView favoriteUnliked;
        ImageView favoriteLiked;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.all_games_image);
            name = itemView.findViewById(R.id.all_games_name);
            release = itemView.findViewById(R.id.all_games_release);
            rating = itemView.findViewById(R.id.all_games_rating);
            platforms = itemView.findViewById(R.id.all_games_platforms);
            genres = itemView.findViewById(R.id.all_games_genres);
            stores = itemView.findViewById(R.id.all_games_stores);
            favoriteLiked = itemView.findViewById(R.id.game_liked);
            favoriteUnliked = itemView.findViewById(R.id.game_favorite_click);
            favoriteUnliked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //hiding the outlined liked symbol
                    favoriteUnliked.setVisibility(View.INVISIBLE);

                    //showing the full liked symbol
                    favoriteLiked.setVisibility(View.VISIBLE);
                    //passing to the fragment the game that was clicked
                    callback.onItemClicked(dataset.get(getAdapterPosition()));
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_game_layout,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String gameName = dataset.get(position).getName().trim();
        holder.name.setText(gameName);

        // Check if the game is a favorite
        boolean isFavorite = favorites.contains(gameName);

        // if inside the favourites then the favourites icon the filled should be visible otherwise not
        holder.favoriteLiked.setVisibility(isFavorite ? View.VISIBLE : View.INVISIBLE);
        //if inside the favourites the game doesn't appear there then show the outlined icon otherwise don't show him
        holder.favoriteUnliked.setVisibility(isFavorite ? View.INVISIBLE : View.VISIBLE);

        Glide.with(context).load(dataset.get(position).getImage()).into(holder.image);
        holder.release.setText( context.getString(R.string.release_date) +dataset.get(position).getDateReleased());
        holder.rating.setText(context.getString(R.string.rating) + dataset.get(position).getRating());
        String all_platforms="";
        //applying the corrent , if thats the last element in the array then dont put , after the text since there is no platforms anymore.
        List<String> platforms = dataset.get(position).getPlatforms();
        for (int i = 0; i < platforms.size(); i++) {
            if (i + 1 < platforms.size()) {
                all_platforms += platforms.get(i) + ", ";
            } else {
                all_platforms += platforms.get(i);
            }
        }
        holder.platforms.setText(context.getString(R.string.available_platforms) + all_platforms);
        String all_genres="";
        List<String> genres = dataset.get(position).getGenres();
        for (int i = 0; i < genres.size(); i++) {
            if (i + 1 < genres.size()) {
                all_genres += genres.get(i) + ", ";
            } else {
                all_genres += genres.get(i);
            }
        }
        holder.genres.setText(context.getString(R.string.genres) + all_genres);
        String all_stores="";
        List<String> stores = dataset.get(position).getStores();
        for (int i = 0; i < stores.size(); i++) {
            if (i + 1 < stores.size()) {
                all_stores += stores.get(i) + ", ";
            } else {
                all_stores += stores.get(i);
            }
        }
        holder.stores.setText(context.getString(R.string.stores) + all_stores);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
