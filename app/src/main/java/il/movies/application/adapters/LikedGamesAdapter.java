package il.movies.application.adapters;

import android.content.Context;
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

public class LikedGamesAdapter extends RecyclerView.Adapter<LikedGamesAdapter.MyViewHolder>{
    public interface favoriteClicked{
        void onItemClicked(String element);
    }
    private static favoriteClicked callback;
    List<String> favorites;

    // Method to update the userFavorites list
    public void setUserFavorites(List<String> userFavorites) {
        this.favorites = userFavorites;
        notifyDataSetChanged(); // Notify adapter about data change so the UI will be updated accordingly
    }


    public LikedGamesAdapter(List<String> favorites, favoriteClicked callback){
        this.favorites= favorites;
        this.callback = callback;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;

        ImageView favoriteUnliked;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.liked_title);
            favoriteUnliked = itemView.findViewById(R.id.game_favorite_click);
            favoriteUnliked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Showing the outlined liked symbol
                    favoriteUnliked.setVisibility(View.INVISIBLE);

                    //passing to the fragment the game that was clicked
                    callback.onItemClicked(favorites.get(getAdapterPosition()));
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_games_layout,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(holder.getAdapterPosition()+1+"."+" " +favorites.get(position));
        holder.favoriteUnliked.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return this.favorites.size();
    }
}
