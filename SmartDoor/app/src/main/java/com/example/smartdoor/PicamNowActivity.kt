package com.example.smartdoor

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.webkit.WebViewClient
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mylib.Notification
import com.example.mylib.net.Mqtt
import com.example.smartdoor.databinding.ActivityPicamNowBinding
import com.example.smartdoor.databinding.InputNewVoiceBinding
import kotlinx.android.synthetic.main.activity_picam_now.*
import kotlinx.android.synthetic.main.input_new_voice.*
import org.eclipse.paho.client.mqttv3.MqttMessage


const val SUB_TOPIC = "iot/monitor/pir"
const val PUB_TOPIC = "iot/control/camera/servo"
const val SERVER_URI = "tcp://192.168.35.227:1883" // 본인의 피시 주소

class PicamNowActivity : AppCompatActivity() {

    lateinit var mqttClient: Mqtt
    lateinit var binding : ActivityPicamNowBinding


    // notification channel 설정
    companion object {
        const val CHANNEL_ID = "com.example.myproject"
        const val CHANNEL_NAME = "My Channel"
        const val CHANNEL_DESCRIPTION = "Channel Test"
        const val NOTIFICATION_REQUEST = 0
        const val NOTIFICATION_ID = 100
    }

    // 음성 데이터 리스트 만듬
//    val voices = mutableListOf<VoiceData>()
//    init {
//        voices += VoiceData("누구세요?")
//        voices += VoiceData("가세요")
//        voices += VoiceData("안사요")
//        voices += VoiceData("없어요")
//        voices += VoiceData("괜찮아요")
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_picam_now)

        binding = ActivityPicamNowBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // 주소에 본인의 라즈베리파이 주소로 바주세요
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

        // 보낼 음성 메세지 선택
        binding.btnSendSound.setOnClickListener {
            val items = arrayOf("누구세요", "놓고 가세요", "없어요", "안사요", "직접 입력하기")
            var selectedItem: String? = null
            val builder = AlertDialog.Builder(this)

            builder.setTitle("보낼 음성 메세지를 선택하세요")

            builder.setSingleChoiceItems(items, -1) { dialog, which ->
                    selectedItem = items[which]
                }

            builder.setPositiveButton("확인") { dialog, which ->
                    if (selectedItem.toString() == "직접 입력하기") {

                        val textBuilder = AlertDialog.Builder(this)
                        val builderItem = InputNewVoiceBinding.inflate(layoutInflater)
                        val input = builderItem.inputNewVoice

                        with(textBuilder) {
                            setTitle("음성 메세지")
                            setMessage("보내실 음성 메세지를 입력하세요")
                            setView(builderItem.root)
                            setPositiveButton("확인") { dialog: DialogInterface?, which: Int ->
                                if (input != null) {
//                                    toast("${input.text.toString()}")
                                    mqttClient.publish("iot/control/voice", input.text.toString())
                                }
                            }
                            show()
                        }

                    } else {
                        mqttClient.publish("iot/control/voice", selectedItem.toString())
                    }
                }
                .show()

        }

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

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}