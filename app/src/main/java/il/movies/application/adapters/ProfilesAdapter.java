package il.movies.application.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import il.movies.application.R;
import il.movies.application.data.Data;

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.MyViewHolder> {

    List<Data> myList;
    Context context;

    userClicked myInterface;

    public interface userClicked{
        void user(Data data);
    }

    public ProfilesAdapter(List<Data> profiles, Context context, userClicked myInterface){
        this.myList = profiles;
        this.context = context;
        this.myInterface = myInterface;
    }





    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_profile_layout,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilesAdapter.MyViewHolder holder, int position) {
        Uri uri = Uri.parse(myList.get(position).profilePicture);
        Glide.with(context).load(uri).into(holder.imageProfile);
        holder.name.setText(myList.get(position).name);
        holder.phone.setText(myList.get(position).phone);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        ImageView imageProfile;
        TextView name;
        TextView phone;




        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            imageProfile = itemView.findViewById(R.id.profile_picture);
            name = itemView.findViewById(R.id.profile_name);
            phone = itemView.findViewById(R.id.profile_phone);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myInterface.user(myList.get(getAdapterPosition()));
                }
            });
        }
    }
}
