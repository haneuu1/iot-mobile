package com.example.smartdoor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mylib.openapi.smartdoorapi.data.RecordingDataItem
import kotlinx.android.synthetic.main.item_video.view.*
import java.text.SimpleDateFormat

class VideoListAdapter (val videoList: ArrayList<RecordingDataItem>, val onItemClick: (Int)->Unit) : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>() {
    val TAG = javaClass.simpleName

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvId = itemView.tv_video_id
        val tvVideoLength = itemView.tv_video_length
        val tvVideoTimestamp = itemView.tv_video_timestamp

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if(pos != RecyclerView.NO_POSITION) {
                    Log.d(TAG, "Item $pos Clicked!")
                    onItemClick(pos)
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoListAdapter.VideoViewHolder, position: Int) {
        videoList[position].let { video ->
            with(holder) {
                val splitLength = video.video_length.split(":")
                tvVideoLength.text = "동영상 길이: " + splitLength[0] + "분 " + splitLength[1] + "초"
                val simpleDate = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss").format(video.video_timestamp)
                tvVideoTimestamp.text = simpleDate

//                tvVideoTimestamp.text = video.video_timestamp.toString()
            }
        }
    }
}