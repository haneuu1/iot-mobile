package com.example.smartdoor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_range_date_picker.*
import java.util.*

class RangeDatePickerActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_date_picker)

        val i = intent ?: return

        val sSD = i.getStringExtra(VideoListActivity.SD)
        val sED = i.getStringExtra(VideoListActivity.ED)

        val startYear = sSD!!.substring(0,4).toInt()
        val startMonth = sSD!!.substring(6,8).toInt()
        val startDay = sSD!!.substring(10,12).toInt()

        val endYear = sSD!!.substring(0,4).toInt()
        val endMonth = sSD!!.substring(6,8).toInt()
        val endDay = sSD!!.substring(10,12).toInt()

//        Log.d(TAG, "sSD: ${sSD!!.substring(0,4).toInt()}")
//        Log.d(TAG, "sSD: ${sSD!!.substring(6,8).toInt()}")
//        Log.d(TAG, "sSD: ${sSD!!.substring(10,12).toInt()}")
//
//        Log.d(TAG, "sSD: ${sED!!.substring(0,4).toInt()}")
//        Log.d(TAG, "sSD: ${sED!!.substring(6,8).toInt()}")
//        Log.d(TAG, "sSD: ${sED!!.substring(10,12).toInt()}")

        val zeroCalendarDate = Calendar.getInstance()
        zeroCalendarDate.set(2021, endMonth-1, 1)

        val firstCalendarDate = Calendar.getInstance()
        firstCalendarDate.set(startYear, startMonth-1, startDay)

        val secondCalendarDate = Calendar.getInstance()
        secondCalendarDate.set(2021, 12, 31)

        val thirdCalendarDate = Calendar.getInstance()
        thirdCalendarDate.set(endYear, endMonth-1, endDay)


        calendar_calendar_view.setOnRangeSelectedListener { startDate, endDate, startLabel, endLabel ->
            val startYear = startLabel!!.substring(startLabel.length-4, startLabel.length) + "년 "

            var startMonth = startLabel.substring(startLabel.indexOf(" ")+1, startLabel.indexOf("월"))
            if(startMonth.toInt() < 10) { startMonth = "0$startMonth" + "월 "} else { startMonth += "월 " }

            var startDay = startLabel.substring(0, startLabel.indexOf(" "))
            if(startDay.toInt() < 10) { startDay = "0$startDay" + "일"} else { startDay += "일" }


            val endYear = endLabel!!.substring(startLabel.length-4, startLabel.length) + "년 "

            var endMonth = endLabel.substring(startLabel.indexOf(" ")+1, startLabel.indexOf("월"))
            if(endMonth.toInt() < 10) { endMonth = "0$endMonth" + "월 "} else { endMonth += "월 "}

            var endDay = endLabel.substring(0, startLabel.indexOf(" "))
            if(endDay.toInt() < 10) { endDay = "0$endDay" + "일"} else { endDay += "일" }

            calendar_departure_date.text = startYear + startMonth + startDay
            calendar_return_date.text = endYear + endMonth + endDay
        }

        calendar_calendar_view.setOnStartSelectedListener { startDate, label ->

            val labelYear = label!!.substring(label.length-4, label.length) + "년 "

            var labelMonth = label.substring(label.indexOf(" ")+1, label.indexOf("월"))
            if(labelMonth.toInt() < 10) { labelMonth = "0$labelMonth" + "월 "} else { labelMonth += "월 " }

            var labelDay = label.substring(0, label.indexOf(" "))
            if(labelDay.toInt() < 10) { labelDay = "0$labelDay" + "일"} else { labelDay += "일" }

            calendar_departure_date.text = labelYear + labelMonth + labelDay
            calendar_return_date.text = labelYear + labelMonth + labelDay
        }

        calendar_calendar_view.apply {
            setRangeDate(zeroCalendarDate.time, secondCalendarDate.time)
            setSelectionDate(firstCalendarDate.time, thirdCalendarDate.time)
        }

        calendar_btnSearch.setOnClickListener {
            i.putExtra(VideoListActivity.RSD, calendar_departure_date.text.toString())
            i.putExtra(VideoListActivity.RED, calendar_return_date.text.toString())

            setResult(VideoListActivity.REQUEST, i)

            finish()
        }
    }

}