package il.example.weatherapp.ui.History

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import il.example.weatherapp.data.models.LocalModels.History
import il.example.weatherapp.databinding.HistoryItemLayoutBinding

class HistoryAdapter(private val list: List<History>, private val callback:historyListener,private val itemSearch:searchListener) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {


    interface historyListener{
        fun onClickHistory(item:History)
    }

    interface searchListener{
        fun onClickItem(item:History)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =HistoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class HistoryViewHolder(private val binding: HistoryItemLayoutBinding) : RecyclerView.ViewHolder(binding.root),
    View.OnClickListener{

        //passing the current history item to the fragment
        init {
            binding.historySpecificIcon.setOnClickListener {
                callback.onClickHistory(list[adapterPosition])
            }
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemSearch.onClickItem(list[adapterPosition])
        }

        fun bind(item:History){
            binding.historyCityName.text = item.city
        }
    }
}