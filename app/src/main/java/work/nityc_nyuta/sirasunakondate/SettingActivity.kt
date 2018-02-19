package work.nityc_nyuta.sirasunakondate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val list = mutableListOf<SettingList>()

        //「明日の献立を表示する」項目
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val tomorrow_show_time = preference.getString("tomorrow_show_time", "00:00")
        val setting_list = SettingList()
        setting_list.icon = R.drawable.ic_access_time_black_24dp
        setting_list.Setting = "明日の献立を表示する"
        if (preference.getBoolean("tomorrow_bool", false)) {
            setting_list.Value = "有効  " + tomorrow_show_time
        } else {
            setting_list.Value = "無効"
        }
        list.add(setting_list)

        val setting_listview = findViewById<ListView>(R.id.setting_listview)
        val adapter = SettingListAdapter(this, list.toList())
        setting_listview.adapter = adapter

        //ListView選択イベント
        setting_listview.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, pos, id ->
                    Toast.makeText(this, pos.toString(), Toast.LENGTH_SHORT).show()
                }
    }
}
