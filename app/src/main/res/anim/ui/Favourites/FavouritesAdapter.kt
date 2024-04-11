package il.example.weatherapp.ui.Favourites

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import il.example.weatherapp.R
import il.example.weatherapp.data.models.LocalModels.Favourites
import il.example.weatherapp.databinding.FavouriteItemBinding


class FavouritesAdapter(private val list: List<Favourites>,
                        private val tempList:Map<String,String>,
                        private val context: Context,
                        private val listener : FavouriteListener)
    :RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>(){

        fun favouriteAt(index: Int) = list[index]


    interface FavouriteListener{
        fun onFavouriteClick(city:String)
    }


    inner class FavouritesViewHolder(private val favouriteBinding: FavouriteItemBinding)
        : RecyclerView.ViewHolder(favouriteBinding.root), View.OnClickListener{

            private lateinit var favourite : Favourites

            init{
                favouriteBinding.root.setOnClickListener(this)
            }


        fun bind(favourite: Favourites){
            this.favourite = favourite
            favouriteBinding.textCity.text = favourite.city

            val tempAndTime = tempList[favourite.city]
            val splittedString = tempAndTime?.split(":",limit = 2)
            val currentTemp = splittedString?.get(0)
            val currentTime = splittedString?.get(1)
            favouriteBinding.favoriteCelius.text = "$currentTemp°"
            favouriteBinding.favoriteCurrentTime.text = context.getString(R.string.last_updated) + " " + currentTime

        }
        //passing to the callback the name of the city
        override fun onClick(v: View?) {
            listener.onFavouriteClick(favourite.city)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val binding =FavouriteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FavouritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}



