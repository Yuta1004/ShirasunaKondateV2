package work.nityc_nyuta.sirasunakondate

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.kondate_list.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

/**
 * Created by student on 2018/01/13.
 */

class KondateListAdapter(context: Context, kondates: List<KondateList>) : ArrayAdapter<KondateList>(context, 0, kondates) {
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val kondate = getItem(position) as KondateList
        if(view == null){
            view = layoutInflater.inflate(R.layout.kondate_list,parent,false)
        }

        //文字に色つけたり
        when(kondate.name){
            "  朝食", "  昼食", "  夕食" ->{
                view!!.setBackgroundColor(Color.rgb(230, 230, 230))
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70)
                view.setLayoutParams(params)
                val text_view = (view.findViewById<TextView>(R.id.kondate_item) as TextView)
                text_view.setTextSize(16.0F)
                text_view.setTypeface(Typeface.DEFAULT_BOLD)
            }
            else ->{
                view!!.setBackgroundColor(DEFAULT_BUFFER_SIZE)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120)
                view.setLayoutParams(params)
                val text_view = (view.findViewById<TextView>(R.id.kondate_item) as TextView)
                text_view.setTextSize(18.0F)
                text_view.setTypeface(Typeface.DEFAULT)
            }
        }

        (view.findViewById<TextView>(R.id.kondate_item) as TextView).text = kondate.name

        return view
    }
}