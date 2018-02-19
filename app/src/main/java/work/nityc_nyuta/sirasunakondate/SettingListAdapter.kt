package work.nityc_nyuta.sirasunakondate

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by student on 2018/02/19.
 */

class SettingListAdapter(context: Context, settings: List<SettingList>) : ArrayAdapter<SettingList>(context, 0, settings) {
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val setting = getItem(position) as SettingList
        if(view == null){
            view = layoutInflater.inflate(R.layout.setting_list,parent,false)
        }

        (view!!.findViewById<ImageView>(R.id.setting_icon) as ImageView).setImageResource(setting.icon)
        (view.findViewById<TextView>(R.id.Setting) as TextView).text = setting.Setting
        (view.findViewById<TextView>(R.id.Value) as TextView).text = setting.Value

        return view
    }
}