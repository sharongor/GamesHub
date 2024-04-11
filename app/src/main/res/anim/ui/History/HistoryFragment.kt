package il.example.weatherapp.ui.History

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import il.example.weatherapp.R
import il.example.weatherapp.data.models.LocalModels.History
import il.example.weatherapp.databinding.HistoryFragmentBinding
import il.example.weatherapp.utils.autoCleared


@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var binding : HistoryFragmentBinding by autoCleared()

    private val viewModel : HistoryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HistoryFragmentBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.deleteAllHistory.setOnClickListener {
            showConfirmationDialog(requireContext(),getString(R.string.delete_all_message)) {
                viewModel.deleteHistory()
            }
        }


        viewModel.history.observe(viewLifecycleOwner){
            binding.historyRv.adapter = HistoryAdapter(it,object: HistoryAdapter.historyListener{
                override fun onClickHistory(item: History) {
                    viewModel.deleteSpecificHistory(item)
                }

            }, object :HistoryAdapter.searchListener{
                override fun onClickItem(item: History) {
                    findNavController().navigate(R.id.action_historyFragment_to_weatherScreenFragment,
                        bundleOf(getString(R.string.city_history) to item.city)
                    )
                }
            })
            binding.historyRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_menu,menu)
        val favouriteItem = menu.findItem(R.id.favourite_icon)
        val homeItem = menu.findItem(R.id.home_btn_icon)

        val colorControlNormal = ContextCompat.getColor(requireContext(), R.color.white)

        // Set tint for the icons
        favouriteItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        homeItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home_btn_icon){
            findNavController().navigate(R.id.action_historyFragment_to_homeScreenFragment)
        }
        else if(item.itemId== R.id.favourite_icon){
            findNavController().navigate(R.id.action_historyFragment_to_favouritesFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    fun showConfirmationDialog(context: Context, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                // Call the onConfirm callback when the user confirms
                onConfirm.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}