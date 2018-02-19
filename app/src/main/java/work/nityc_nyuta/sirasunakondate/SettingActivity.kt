package work.nityc_nyuta.sirasunakondate

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import java.sql.Time

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        listShow()
    }

    fun listShow(){
        val list = mutableListOf<SettingList>()

        //「明日の献立を表示する」項目
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val tomorrow_show_time = preference.getString("tomorrow_time", "00:00")
        val setting_list = SettingList()
        setting_list.icon = R.drawable.ic_access_time_black_24dp
        setting_list.Setting = "明日の献立を表示する"
        if (preference.getBoolean("tomorrow_bool", false)) {
            setting_list.Value = "有効  " + tomorrow_show_time + "から"
        } else {
            setting_list.Value = "無効"
        }
        list.add(setting_list)

        //AdapterSet
        val setting_listview = findViewById<ListView>(R.id.setting_listview)
        val adapter = SettingListAdapter(this, list.toList())
        setting_listview.adapter = adapter

        //ListView選択イベント
        setting_listview.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, pos, id ->
                    when(pos) {
                    //「明日の献立を表示する」項目
                        0 -> {
                            val time_set_dialog_builder = AlertDialog.Builder(this)
                            val layout = LayoutInflater.from(this).inflate(R.layout.tomorrow_time_setting_dialog, null)
                            val setted_time = preference.getString("tomorrow_time","00:00").split(":")
                            layout.findViewById<CheckBox>(R.id.checkBox).setChecked(preference.getBoolean("tomorrow_bool", false))
                            layout.findViewById<TimePicker>(R.id.timePicker).hour = setted_time[0].toInt()
                            layout.findViewById<TimePicker>(R.id.timePicker).minute = setted_time[1].toInt()
                            time_set_dialog_builder.setView(layout)
                                    .setNegativeButton("キャンセル", null)
                                    .setPositiveButton("セット", DialogInterface.OnClickListener { dialog, which ->
                                        val bool = layout.findViewById<CheckBox>(R.id.checkBox).isChecked
                                        val time_hh = layout.findViewById<TimePicker>(R.id.timePicker).hour.toString()
                                        val time_mm = layout.findViewById<TimePicker>(R.id.timePicker).minute.toString()
                                        val pre_editer = preference.edit()
                                        pre_editer.putBoolean("tomorrow_bool", bool)
                                        pre_editer.putString("tomorrow_time", time_hh + ":" + zero_pad(time_mm))
                                        pre_editer.commit()
                                        listShow()
                                    })
                            time_set_dialog_builder.create().show()
                        }
                    }
                }
    }

    fun zero_pad(base: String): String{
        var base = base
        if(base.length == 1){
            base = "0" + base
        }
        return  base
    }
}
