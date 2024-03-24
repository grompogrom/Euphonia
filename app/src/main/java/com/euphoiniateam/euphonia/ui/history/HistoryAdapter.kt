package com.euphoiniateam.euphonia.ui.history

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.euphoiniateam.euphonia.R
import java.io.File

class HistoryAdapter(private val context: Context, private val data: ArrayList<String>, private val navController: NavController) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var filteredDataList: ArrayList<String> = ArrayList(data)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredDataList[position]
        holder.textView.text = item
        holder.itemView.setOnClickListener {
            val action = HistoryFragmentDirections.actionNavigationDashboardToCreationFragment(item)
            navController.navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.compositionName)
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

}
