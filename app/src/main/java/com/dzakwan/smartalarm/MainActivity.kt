package com.dzakwan.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dzakwan.smartalarm.adapter.AlarmAdapter
import com.dzakwan.smartalarm.data.Alarm
import com.dzakwan.smartalarm.data.local.AlarmDB
import com.dzakwan.smartalarm.data.local.AlarmDao
import com.dzakwan.smartalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding


    private var alarmDao: AlarmDao? = null

    private var alarmAdapter: AlarmAdapter? = null

    private var alarmService: AlarmService? = null

    override fun onResume() {
        super.onResume()
        alarmDao?.getAlarm()?.observe(this){ data ->
            alarmAdapter?.setData(data)
            Log.i("DeleteAlarm", "onSwiped: deleteAlarm $data")
        }
        /*super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = alarmDao?.getAlarm()
            withContext(Dispatchers.Main){
                alarm?.let { alarmAdapter?.setData(it) }
            }
            Log.i("Get Alarm", "getAlarm: alarm with $alarm")
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(applicationContext)
        alarmDao = db.alarmDao()
        alarmService = AlarmService()
        alarmAdapter = AlarmAdapter()

        initView()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvReminderAlarm.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    //untuk nge bind dari cardview yang dibikin
    private fun initView() {
        binding.apply {
            cvSetOneTime.setOnClickListener {
                startActivity(Intent(applicationContext, OneTimeAlarmActivity::class.java))
            }

            cvSetRepeatingTime.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }
        }

    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper( object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Todo 3 hapus yg sebaris notifyItemRemove DONE
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val deletedAlarm = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch{
                    deletedAlarm?.let { alarmDao?.deleteAlarm(it) }
                    Log.i("DeleteAlarm", "onSwiped: deleteAlarm $deletedAlarm")
                }
                val alarmType = deletedAlarm?.type
                alarmType?.let { alarmService?.cancelAlarm(baseContext, it) }
                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }

        }).attachToRecyclerView(recyclerView)
    }

}