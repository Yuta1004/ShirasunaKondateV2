package work.nityc_nyuta.sirasunakondate

import android.app.DatePickerDialog
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
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var plus_day = 0
    var show_date = listOf<Int>(2001,10,4)
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

        //メイン画面上部ボタンのリスナー (GetAPI -> CreateKondateList -> AdapterDataSet)
        findViewById<Button>(R.id.before).setOnClickListener{
            val isSuccess = GetAPI("all",DatePlusToList(plus_day-1))
            if(isSuccess) plus_day--
        }
        findViewById<Button>(R.id.next).setOnClickListener{
            val isSuccess = GetAPI("all",DatePlusToList(plus_day+1))
            if(isSuccess) plus_day ++
        }
    }

    //画面表示時
    override fun onResume() {
        super.onResume()

        //API接続 (GetAPI -> CreateKondateList -> AdapterDataSet)
        plus_day = 0
        GetAPI("all",DatePlusToList(0))
    }

    //戻るボタン
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //オプションメニュー
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //オプションメニュー(アイテム選択イベント)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_today -> {
                plus_day = 0
                GetAPI("all",DatePlusToList(plus_day))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //ナビゲーションドロワー
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_search -> {

            }

            //日付選択
            R.id.nav_calendar_open -> {

                //カレンダーインスタンス作成-> DatePickerDialo生成
                val calendar = Calendar.getInstance()
                DatePickerDialog(this,

                        //リスナ
                        DatePickerDialog.OnDateSetListener { view, year, month, dayofMonth ->
                            GetAPI("all", listOf(year,month+1,dayofMonth))

                            //現在表示されている日付からセットされた日付を引いてplus_dayにセット
                            val calendar_today = Calendar.getInstance()
                            plus_day = DateDifference(listOf(calendar_today.get(Calendar.YEAR),calendar_today.get(Calendar.MONTH),calendar_today.get(Calendar.DAY_OF_MONTH)),
                                           listOf(year,month,dayofMonth))

                        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
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
    fun DatePlusToList(delay: Int): List<Int> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,delay)
        val date = listOf<Int>(calendar.get(Calendar.YEAR),
                                     calendar.get(Calendar.MONTH)+1,
                                     calendar.get(Calendar.DAY_OF_MONTH))
        return listOf<Int>(date[0], date[1], date[2])
    }

    //date_1とdate_2の日時の差を求めてInt型で返す
    //date_1 -> 基準
    fun DateDifference(date_1: List<Int>, date_2: List<Int>): Int{

        //カレンダーインスタンス生成->日時指定
        val calendar_1 = Calendar.getInstance()
        calendar_1.set(date_1[0],date_1[1],date_1[2])
        val calendar_2 = Calendar.getInstance()
        calendar_2.set(date_2[0],date_2[1],date_2[2])

        //差を求める(long)->Int型に変換して返す
        val minus_mill = calendar_2.timeInMillis - calendar_1.timeInMillis
        return (minus_mill / (1000 * 60 * 60 * 24)).toInt()
    }

    //献立表示
    fun CreateKondateList(response_json: JSONObject, date: List<Int>){
        val list = mutableListOf<KondateList>()

        //コードによって分岐
        when(response_json.getInt("code")){
            2,3 -> { //Error
                val error_messages = mutableMapOf<String,String>("2" to "データ取得時にエラーが発生しました", "3" to "献立データが登録されていません")
                Toast.makeText(this,error_messages[response_json.getString("code")], LENGTH_SHORT).show()
                AdapterDataSet(list.toList(),date)
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
            list.add(kondate_type)

            //献立追加
            for(idx in 0..box.length()-1){
                if(box.getString(idx) != ""){
                    val kondate_list_for = KondateList()
                    kondate_list_for.name = "  " + box.getString(idx)
                    list.add(kondate_list_for)
                }
            }
            AdapterDataSet(list.toList(),date)
        }
    }

    //アダプターにデータを登録
    fun AdapterDataSet(list: List<KondateList>, date: List<Int>){
        val kondate_show_listview = findViewById<ListView>(R.id.kondate_show)

        //アダプター設定
        val adapter = KondateListAdapter(this, list.toList())
        kondate_show_listview.adapter = adapter

        //日付表示
        val calendar = Calendar.getInstance()
        val calendar_tomorrow = Calendar.getInstance()
        calendar_tomorrow.add(Calendar.DAY_OF_MONTH,1)
        val date_text_view = findViewById<TextView>(R.id.date)

        //日付表示欄設定
        when(listOf<Int>(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)){
            listOf(date[0].toInt(),date[1].toInt()) -> { //今日 or 明日
                if(calendar.get(Calendar.DAY_OF_MONTH) == date[2].toInt()) { //今日
                    date_text_view.text = "今日の献立"
                }else if(calendar_tomorrow.get(Calendar.DAY_OF_MONTH) == date[2].toInt()){ //明日
                    date_text_view.text = "明日の献立"
                }else{
                    date_text_view.text = "${date[0]}年${date[1]}月${date[2]}日"
                }
            }
            else -> date_text_view.text = "${date[0]}年${date[1]}月${date[2]}日"
        }

        connecting = false
        setTitle("白砂寮献立")
        return Unit
    }

    //API接続
    fun GetAPI(isbn: String, keys: List<Int>): Boolean{

        //接続中か確認
        if(connecting){
            return false
        }else{
            if(isbn == "all") show_date = keys
            connecting = true
            setTitle("白砂寮献立 Loading...")
        }

        //接続設定
        val queue = Volley.newRequestQueue(this)
        val params: JSONObject = JSONObject()
        var request: JsonObjectRequest? = null

        //URL設定
        var API_URL = "http://nityc-nyuta.work/sirasuna_kondateAPI_prototype/"
        if(isbn == "all"){ //献立全取得
            API_URL += "all?year=${keys[0]}&month=${keys[1]}&day=${keys[2]}"
            request = JsonObjectRequest(Request.Method.GET, API_URL, params,
                    Response.Listener<JSONObject> { response ->
                        CreateKondateList(response,keys)
                    },
                    Response.ErrorListener { volleyError ->
                        Toast.makeText(this, volleyError.toString(), LENGTH_SHORT).show()
                    }
            )
        }else{
            return false
        }
        queue.add(request)
        queue.start()
        return true
    }
}
