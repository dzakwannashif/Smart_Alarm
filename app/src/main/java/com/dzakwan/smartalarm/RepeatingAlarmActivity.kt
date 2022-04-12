package com.dzakwan.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dzakwan.smartalarm.adapter.TimePickerFragment
import com.dzakwan.smartalarm.data.Alarm
import com.dzakwan.smartalarm.data.local.AlarmDB
import com.dzakwan.smartalarm.data.local.AlarmDao
import com.dzakwan.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.dzakwan.smartalarm.helper.timeFormatter
import kotlinx.android.synthetic.main.activity_repeating_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimePickerFragment.TimeDialogListener {

    private var _binding : ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private var alarmDao : AlarmDao? = null

    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(this)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()
    }

    private fun initView(){
        binding.apply {
            btn_set_time_repeating.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAddSetRepeatingAlarm.setOnClickListener {
                val time = tvRepeatingTime.text.toString()
                val note = etNoteRepeating.text.toString()

                if (time != "Time"){
                    alarmService.setRepeatingAlarm(
                        applicationContext,
                        AlarmService.TYPE_REPEATING,
                        time,
                        note
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                note,
                                AlarmService.TYPE_REPEATING
                            )
                        )
                        finish()
                    }
                } else{
                    Toast.makeText(
                        this@RepeatingAlarmActivity,
                        "Please set time of alarm",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            btnCancelSetRepeatingAlarm.setOnClickListener {

            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvRepeatingTime.text = timeFormatter(hourOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

