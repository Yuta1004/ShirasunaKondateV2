package work.nityc_nyuta.sirasunakondate

import android.app.VoiceInteractor
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var plus_day = 0
    var connecting = false

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        setTitle("白砂寮献立")

        //メイン画面上部ボタンのリスナー (GetAPI -> KondateShow)
        findViewById<Button>(R.id.before).setOnClickListener{ GetAPI("all",DatePlusToString(plus_day-1)); plus_day-- }
        findViewById<Button>(R.id.next).setOnClickListener{ GetAPI("all",DatePlusToString(plus_day+1)); plus_day ++ }
    }

    //画面表示時
    override fun onResume() {
        super.onResume()

        //API接続 (GetAPI -> KondateShow)
        GetAPI("all",DatePlusToString(0));
    }

    //戻るボタン
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    //ナビゲーションドロワー
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_search -> {

            }
            R.id.nav_calendar_open -> {

            }
            R.id.nav_credit -> {

            }
            R.id.nav_setting -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //日時を指定日加算してStr型で返す
    fun DatePlusToString(delay: Int): String{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,delay)
        val date = listOf<Int>(calendar.get(Calendar.YEAR),
                                     calendar.get(Calendar.MONTH)+1,
                                     calendar.get(Calendar.DAY_OF_MONTH))
        return date[0].toString() + "," + date[1].toString() + "," + date[2].toString()
    }

    //献立表示
    fun KondateShow(response_json: JSONObject, date: String){
        //Listview設定
        val kondate_show_listview = findViewById<ListView>(R.id.kondate_show)
        val list = mutableListOf<KondateList>()
        val section_position = mutableListOf<Int>()

        //コードによって分岐
        when(response_json.getInt("code")){
            2,3 -> { //Error
                val error_messages = mutableMapOf<String,String>("2" to "データ取得時にエラーが発生しました", "3" to "献立データが登録されていません")
                Toast.makeText(this,error_messages[response_json.getString("code")], LENGTH_SHORT).show()

                //アダプター設定
                val adapter = KondateListAdapter(this, list.toList())
                kondate_show_listview.adapter = adapter
                val date_list = date.split(",")

                //日付表示
                val calender = Calendar.getInstance()
                findViewById<TextView>(R.id.date).setText(date_list[0] + "年" + date_list[1] + "月" + date_list[2] + "日")

                connecting = false
                return Unit
            }
        }

        //献立配列をそれぞれ変数に入れる
        val menu = response_json.getJSONObject("menu")
        val breakfast = menu.getJSONArray("breakfast")
        val lunch = menu.getJSONArray("lunch")
        val dinner = menu.getJSONArray("dinner")


        //献立をlistに追加
        for(box in arrayOf<JSONArray>(breakfast,lunch,dinner)){
            val kondate_type = KondateList()

            //擬似セクション
            when(box){
                breakfast -> kondate_type.name = "  朝食"
                lunch -> kondate_type.name = "  昼食"
                dinner -> kondate_type.name = "  夕食"
            }
            list!!.add(kondate_type)

            //献立追加
            for(idx in 0..box.length()-1){
                if(box.getString(idx) != ""){
                    val kondate_list_for = KondateList()
                    kondate_list_for.name = "  " + box.getString(idx)
                    list!!.add(kondate_list_for)
                }
            }

        }

        //アダプター設定
        val adapter = KondateListAdapter(this,list.toList())
        kondate_show_listview.adapter = adapter
        val date_list = date.split(",")
        findViewById<TextView>(R.id.date).setText(date_list[0] + "年" + date_list[1] + "月" + date_list[2] + "日")
        connecting = false
    }

    //API接続
    fun GetAPI(isbn: String, keys: String){
        //接続中か確認
        if(connecting){
            return Unit
        }else{
            connecting = true
        }

        //URL設定
        var API_URL = "http://nityc-nyuta.work/sirasuna_kondateAPI_prototype/"
        val key = keys.split(",")
        if(isbn == "all"){
            API_URL += "all?year=" + key[0] + "&month=" + key[1] + "&day=" + key[2]
        }

        //接続
        val queue = Volley.newRequestQueue(this)
        val params: JSONObject = JSONObject()
        val request = JsonObjectRequest(Request.Method.GET, API_URL, params,
                Response.Listener<JSONObject> { response ->
                    KondateShow(response,key[0] + "," + key[1] + "," + key[2])
                },
                Response.ErrorListener { volleyError ->
                    Toast.makeText(this, volleyError.toString(), LENGTH_SHORT).show()
                }
        )
        queue.add(request)
        queue.start()
    }
}
