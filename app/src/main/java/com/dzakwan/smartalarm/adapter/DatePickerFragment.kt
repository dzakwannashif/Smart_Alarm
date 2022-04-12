package com.dzakwan.smartalarm.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dialogListener: DateDialogListener? = null

    //buat inisialisasi date listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogListener = context as DateDialogListener
    }

    //mengantisipasi eror dan mencegah agar date picker tidak muncul 2 kali
    override fun onDetach() {
        super.onDetach()
        if (dialogListener != null) dialogListener = null
    }

    //untuk menentukan tanggalnya
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val date = calender.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity as Context, this, year, month, date)
    }

    //untuk mengetahui settingan kita
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dialogListener?.onDialogDateSet(tag, year, month, dayOfMonth)
        Log.i(tag, "onDateSet: $year $month $dayOfMonth")
    }

    //buat dipanggil di activity supaya dapat inputan yg sudah dipilih
    interface DateDialogListener {
        fun onDialogDateSet(tag: String?, year:Int, month: Int, dayOfMonth: Int)
    }
}