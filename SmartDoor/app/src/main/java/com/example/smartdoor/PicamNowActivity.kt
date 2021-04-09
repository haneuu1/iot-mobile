package com.example.smartdoor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.SeekBar
import com.example.mylib.Notification
import com.example.mylib.net.Mqtt
import kotlinx.android.synthetic.main.activity_picam_now.*
import org.eclipse.paho.client.mqttv3.MqttMessage


const val SUB_TOPIC = "iot/monitor/pir"
const val PUB_TOPIC = "iot/control/camera/servo"
const val SERVER_URI = "tcp://192.168.35.226:1883"

class PicamNowActivity : AppCompatActivity() {

    lateinit var mqttClient: Mqtt

    // notification channel 설정
    companion object {
        const val CHANNEL_ID = "com.example.myproject"
        const val CHANNEL_NAME = "My Channel"
        const val CHANNEL_DESCRIPTION = "Channel Test"
        const val NOTIFICATION_REQUEST = 0
        const val NOTIFICATION_ID = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picam_now)

        mqttClient = Mqtt(this, SERVER_URI)
        try {
            mqttClient.setCallback(::onReceived)
            mqttClient.connect(arrayOf<String>(SUB_TOPIC))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        picamView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        picamView.loadData(
            "<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} div{overflow: hidden;} " +
                        "</style></head><body><div><img src='http://192.168.35.41:8000/mjpeg/stream'/></div></body></html>" ,
                "text/html",  "UTF-8"
        )

        btnReload.setOnClickListener {
            picamView.reload()
        }

        // seekbar 의 값을 publish
        seekAngle.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mqttClient.publish(PUB_TOPIC, progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    fun onReceived(topic: String, message: MqttMessage) {
        // 토픽 수신 처리
        val msg = String(message.payload)
        if (msg == "on") { // pir 센서가 움직임을 감지하였을 때
            // Notification 발생 시키기
            val noti = Notification(this)
            noti.createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION)
            val pendingIntent = noti.getPendingIntent(this::class.java, NOTIFICATION_REQUEST)
            noti.notifyBasic(CHANNEL_ID, NOTIFICATION_ID, "움직임 발생!", "파이카메라를 확인하세요.", R.drawable.ic_launcher_foreground, pendingIntent)
        }
    }


}