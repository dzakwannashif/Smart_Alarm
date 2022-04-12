package com.dzakwan.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dzakwan.smartalarm.adapter.DatePickerFragment
import com.dzakwan.smartalarm.adapter.TimePickerFragment
import com.dzakwan.smartalarm.data.Alarm
import com.dzakwan.smartalarm.data.local.AlarmDB
import com.dzakwan.smartalarm.data.local.AlarmDao
import com.dzakwan.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.dzakwan.smartalarm.helper.TAG_TIME_PICKER
import com.dzakwan.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DatePickerFragment.DateDialogListener, TimePickerFragment.TimeDialogListener{

    private var _binding : ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private var alarmDao: AlarmDao? = null

    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(applicationContext)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()
    }

    private fun initView() {
        binding.apply {

            btnSetDate.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TAG_TIME_PICKER)
            }

            btnAddSetOneTimeSetTime.setOnClickListener{
                val date = tvOneDate.text.toString()
                val time = tvOneTime.text.toString()
                val note = editNoteOneTime.text.toString()

                if(date != "Date" && time != "Time"){
                    alarmService.setOneTimeAlarm(applicationContext,1,date, time, note)
                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                note,
                                AlarmService.TYPE_ONE_TIME
                            )
                        )
                        Log.i("addAlarm", "Succes at alarm on $date $time with message $note")
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "Set Your Date & Time.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        //mengatur tanggal supaya sama dengan yang sudah di pilih di date picker dialog
        calendar.set(year,month,dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOneDate.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvOneTime.text = timeFormatter(hourOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}