package il.example.weatherapp.ui.Favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import il.example.weatherapp.R
import il.example.weatherapp.data.models.LocalModels.Favourites
import il.example.weatherapp.databinding.FavouritesFragmentBinding
import il.example.weatherapp.utils.Error
import il.example.weatherapp.utils.Loading
import il.example.weatherapp.utils.Success
import il.example.weatherapp.utils.autoCleared


@AndroidEntryPoint
class FavouritesFragment: Fragment() {

    private var binding : FavouritesFragmentBinding by autoCleared()

    //The two fragments(also in WeatherScreenFragment) will use the same ViewModel of WeatherScreenViewModel
    //if declaring by viewModels() that means that each fragment will have its own instance of the viewModel and they won't share data among them
    private val viewModel : FavouritesViewModel by viewModels()
    private var undoCity=""



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FavouritesFragmentBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transformationFavourites.observe(viewLifecycleOwner){
            when(it.status){
                is Loading ->{
                    binding.progressBar.isVisible = true
                }

                is Success ->{
                    binding.progressBar.isVisible = false
                    binding.favouritesRv.adapter = FavouritesAdapter(it.status.data!!.favourites,it.status.data!!.celsiusList,requireContext(),object : FavouritesAdapter.FavouriteListener{
                        override fun onFavouriteClick(city: String) {
                            findNavController().navigate(R.id.action_favouritesFragment_to_weatherScreenFragment,bundleOf(
                                getString(
                                    R.string.fav_city
                                ) to city))
                        }
                    })

                    binding.favouritesRv.layoutManager = LinearLayoutManager(requireContext())
                }

                is Error -> {
                    binding.progressBar.isVisible = false
                    when(it.status.message){
                        1 ->{
                            Toast.makeText(requireContext(),getString(R.string.network_call_has_failed), Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            Toast.makeText(requireContext(),getString(R.string.exception_caught), Toast.LENGTH_SHORT).show()
                        }
                        3 ->{
                            Toast.makeText(requireContext(),getString(R.string.no_such_city), Toast.LENGTH_SHORT).show()
                        }
                        4 -> {
                            Toast.makeText(requireContext(),getString(R.string.no_cities_found), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        //For the recyclerview -> Touch helper -> Deleting items when swiping left or right.
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val fav =(binding.favouritesRv.adapter as FavouritesAdapter).favouriteAt(viewHolder.adapterPosition)
                undoCity = fav.city
                viewModel.deleteFavourite(fav)
                Snackbar.make(binding.root,getString(R.string.item_deleted),Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.undo)){
                    viewModel.insertCity(Favourites(undoCity))
                }.show()
            }
        }).attachToRecyclerView(binding.favouritesRv)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu,menu)
        val historyItem = menu.findItem(R.id.history_icon)
        val homeItem = menu.findItem(R.id.home_btn_icon)
        val colorControlNormal = ContextCompat.getColor(requireContext(), R.color.white)

        // Set tint for the icons
        historyItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        homeItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home_btn_icon){
            findNavController().navigate(R.id.action_favouritesFragment_to_homeScreenFragment)
        }
        else if(item.itemId== R.id.history_icon){
            findNavController().navigate(R.id.action_favouritesFragment_to_historyFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}