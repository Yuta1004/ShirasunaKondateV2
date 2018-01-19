package work.nityc_nyuta.sirasunakondate

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView

class CreditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit)

        //githubアイコンをタップするとgithubのwebページを表示
        val github_icon = findViewById<ImageView>(R.id.github_icon)
        github_icon.setImageResource(R.drawable.github)
        github_icon.setOnClickListener { v ->
            val url = Uri.parse("https://github.com/Yuta1004/Shirasuna_Kondate_v2")
            val intent = Intent(Intent.ACTION_VIEW,url)
            startActivity(intent)
        }

        setTitle("クレジット")
    }

    //Backボタン
    override fun onBackPressed() {
        finish()
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
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
