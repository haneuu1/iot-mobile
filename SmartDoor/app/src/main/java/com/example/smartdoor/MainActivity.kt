package com.example.smartdoor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mylib.Notification
import com.example.mylib.net.Mqtt
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.MqttMessage

const val SUB_TOPIC = "iot/monitor/pir"
const val PUB_TOPIC = "iot/control/camera/servo/vertical"
const val MQTT_SERVER_URI = "tcp://192.168.35.100:1883" // 본인의 피시 주소
const val DJANGO_SERVER_URI = "http://192.168.35.225:8000" // 라즈베리파이 주소

class MainActivity : AppCompatActivity() {

    lateinit var mqttClient: Mqtt

    companion object {
        const val CHANNEL_ID = "com.example.smartdoor"
        const val CHANNEL_NAME = "My Channel"
        const val CHANNEL_DESCRIPTION = "Channel Test"
        const val NOTIFICATION_REQUEST = 0
        const val NOTIFICATION_ID = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mqttClient = Mqtt(this, MQTT_SERVER_URI)
        try {
            mqttClient.setCallback(::onReceived)
            mqttClient.connect(arrayOf<String>(SUB_TOPIC))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        card1.setOnClickListener {
            val i = Intent(this, PicamNowActivity::class.java)
            startActivity(i)
        }

        card2.setOnClickListener {
            val i = Intent(this, VideoListActivity::class.java)
            startActivity(i)
        }

        card3.setOnClickListener {
            val i = Intent(this, DoorAccessRecordListActivity::class.java)
            startActivity(i)
        }

        card4.setOnClickListener {
            showSettingPopup()
        }

        card5.setOnClickListener {
            mqttClient.publish("iot/control/key/temp", "1234_20")
        }

    }

    private fun showSettingPopup() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup, null)
        val textView: TextView = view.findViewById(R.id.textView)
        textView.text = "현관문을 여시겠습니까?"
        
        val alertDialog = AlertDialog.Builder(this)
                .setTitle("스마트도어")
                .setPositiveButton("확인") { dialog, which ->
                    toast("문이 열렸습니다.")
                    mqttClient.publish("iot/control/key", "app_on")
                }
                .setNeutralButton("취소", null)
                .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

    fun onReceived(topic: String, message: MqttMessage) {
        // 토픽 수신 처리
        val msg = String(message.payload)
        if (msg == "on") { // pir 센서가 움직임을 감지하였을 때
            // Notification 발생 시키기
            val noti = Notification(this)
            noti.createNotificationChannel(PicamNowActivity.CHANNEL_ID, PicamNowActivity.CHANNEL_NAME, PicamNowActivity.CHANNEL_DESCRIPTION)
            val pendingIntent = noti.getPendingIntent(this::class.java, PicamNowActivity.NOTIFICATION_REQUEST)
            noti.notifyBasic(PicamNowActivity.CHANNEL_ID, PicamNowActivity.NOTIFICATION_ID, "움직임 발생!", "파이카메라를 확인하세요.", R.drawable.ic_launcher_foreground, pendingIntent)
        }
    }

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}