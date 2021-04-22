package com.example.smartdoor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mylib.openapi.smartdoorapi.data.MqttDataItem
import kotlinx.android.synthetic.main.item_door_access_record.view.*
import java.text.SimpleDateFormat

class DoorAccessRecordListAdapter (val mqttList: ArrayList<MqttDataItem>, val onItemClick: (Int)->Unit) : RecyclerView.Adapter<DoorAccessRecordListAdapter.DoorAccessRecordViewHolder>() {
    val TAG = javaClass.simpleName

    inner class DoorAccessRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTopic = itemView.tv_topic
        val tvMsg = itemView.tv_msg
        val tvTimestamp = itemView.tv_timestamp

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoorAccessRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_door_access_record, parent, false)
        return DoorAccessRecordViewHolder(view)
    }

    override fun getItemCount(): Int = mqttList.size

    override fun onBindViewHolder(holder: DoorAccessRecordViewHolder, position: Int) {
        mqttList[position].let { mqtt ->
            with(holder) {
                tvTopic.text = mqtt.topic
                tvMsg.text = mqtt.msg
                val simpleDate = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss").format(mqtt.timestamp)
                tvTimestamp.text = simpleDate
            }
        }
    }

}