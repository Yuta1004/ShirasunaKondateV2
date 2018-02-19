package work.nityc_nyuta.sirasunakondate

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.graphics.Bitmap
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.widget.ProgressBar


class KondateDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kondate_detail)

        //サイト表示
        val loading_dialog_builder = AlertDialog.Builder(this)
        loading_dialog_builder.setMessage("Loading...")
        loading_dialog_builder.setCancelable(false)
        val loading_dialog = loading_dialog_builder.create()
        val detail_webview = findViewById<WebView>(R.id.detail_webview)
        detail_webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loading_dialog.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loading_dialog.dismiss()
            }
        }
        detail_webview.loadUrl("http://shirasunaryou.sakura.ne.jp/cgi-bin/shirasuna/kondate/index.cgi?display=sp")
        detail_webview.settings.javaScriptEnabled = true
        setTitle("情報取得先サイト")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_main -> {
                finish()
            }
            R.id.action_browse -> {
                val uri = Uri.parse("http://shirasunaryou.sakura.ne.jp/cgi-bin/shirasuna/kondate/index.cgi?display=sp")
                val browse = Intent(Intent.ACTION_VIEW,uri);
                startActivity(browse)

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
