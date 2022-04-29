package com.dzakwan.smartalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzakwan.smartalarm.data.Alarm
import com.dzakwan.smartalarm.databinding.RowItemAlarmOneTimeBinding

class AlarmAdapter():
    RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {

    val listAlarm: ArrayList<Alarm> = arrayListOf()

    inner class MyViewHolder(val binding: RowItemAlarmOneTimeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder (
        RowItemAlarmOneTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alarm = listAlarm[position]
        holder.binding.apply{
            itemDateAlarm.text = alarm.date
            itemTimeAlarm.text = alarm.time
            itemNoteAlarm.text = alarm.note
        }
    }

    override fun getItemCount() = listAlarm.size

    // Todo 2 perbarui code
    fun setData(list: List<Alarm>) {
        val alarmDiffUtil = AlarmDiffUtil(listAlarm, list)
        val alarmDiffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        listAlarm.clear()
        listAlarm.addAll(list)
        alarmDiffUtilResult.dispatchUpdatesTo(this)
    }
}