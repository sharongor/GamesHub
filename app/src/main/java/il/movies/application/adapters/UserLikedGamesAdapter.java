package il.movies.application.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import il.movies.application.R;

public class UserLikedGamesAdapter extends RecyclerView.Adapter<UserLikedGamesAdapter.MyViewHolder>{

    List<String> favorites;
    public UserLikedGamesAdapter(List<String> favorites){
        this.favorites= favorites;
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
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView title;
        ImageView favoriteUnliked;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.liked_title);
            favoriteUnliked = itemView.findViewById(R.id.game_favorite_click);
            favoriteUnliked.setVisibility(View.INVISIBLE);
        }
    }


}
