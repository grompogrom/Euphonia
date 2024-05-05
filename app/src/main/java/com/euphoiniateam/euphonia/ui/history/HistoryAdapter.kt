package com.euphoiniateam.euphonia.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.tools.playMusic

class HistoryAdapter(private val context: Context, private var data: ArrayList<String>, private val navController: NavController) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var filteredDataList: ArrayList<String> = ArrayList(data)
    private var isResume = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredDataList[position]
        holder.textView.text = item
        holder.button1.setOnClickListener {
            if (!isResume) {
                isResume = true
                holder.button1.setImageDrawable(context.resources.getDrawable(R.drawable.baseline_pause_24, null))
            } else {
                isResume = false
                holder.button1.setImageDrawable(context.resources.getDrawable(R.drawable.outline_play_arrow_24, null))
            }
            playMusic(context,item)
        }

        holder.button2.setOnClickListener {
            MusicData.songName = item
            val action = HistoryFragmentDirections.actionNavigationDashboardToCreationFragment()
            navController.navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.compositionName)
        val button1: ImageButton = itemView.findViewById(R.id.button1)
        val button2: ImageButton = itemView.findViewById(R.id.button2)
    }

    fun filter(query: String?) {
        filteredDataList.clear()
        if (query.isNullOrBlank()) {
            filteredDataList.addAll(data)
        } else {
            for (item in data) {
                if (item.contains(query, ignoreCase = true)) {
                    filteredDataList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun setData(newData: ArrayList<String>) {
        data = ArrayList(newData)
        filteredDataList = ArrayList(newData)
        notifyDataSetChanged()
    }

}

