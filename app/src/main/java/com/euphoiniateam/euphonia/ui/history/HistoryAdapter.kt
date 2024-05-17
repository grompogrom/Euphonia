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
import com.euphoiniateam.euphonia.tools.getUriForFileNameFromPiano
import com.euphoiniateam.euphonia.tools.getUriForFileNameFromResults
import com.euphoiniateam.euphonia.ui.MidiPlayer

class HistoryAdapter(
    private val context: Context,
    private var data: ArrayList<String>,
    private val navController: NavController,
    private var currentTab: CurrentTab,
    private val midiPlayer: MidiPlayer
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var filteredDataList: ArrayList<String> = ArrayList(data)
    private var isResume = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = filteredDataList[position]
        holder.textView.text = music
        holder.button1.setOnClickListener {
            if (!isResume) {
                isResume = true
                holder.button1.setImageDrawable(
                    context.resources.getDrawable(
                        R.drawable.baseline_pause_24,
                        null
                    )
                )
                val uri = if (currentTab == CurrentTab.Piano) {
                    getUriForFileNameFromPiano(music)
                } else {
                    getUriForFileNameFromResults(music)
                }
                uri?.let { midiPlayer.play(context, it) }
            } else {
                isResume = false
                holder.button1.setImageDrawable(
                    context.resources.getDrawable(
                        R.drawable.outline_play_arrow_24,
                        null
                    )
                )
                midiPlayer.stop()
            }
        }

        holder.button2.setOnClickListener {
            MusicData.songName = music
            val action = HistoryFragmentDirections.actionNavigationDashboardToCreationFragment()
            navController.navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.compositionName)
        val button1: ImageButton = itemView.findViewById(R.id.btn_show_played)
        val button2: ImageButton = itemView.findViewById(R.id.btn_show_generated)
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

    fun setData(newData: ArrayList<String>, currentTab: CurrentTab) {
        data = ArrayList(newData)
        filteredDataList = ArrayList(newData)
        this.currentTab = currentTab
        notifyDataSetChanged()
    }
}
