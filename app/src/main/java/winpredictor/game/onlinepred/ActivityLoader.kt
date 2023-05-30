package winpredictor.game.onlinepred




import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.appsflyer.AppsFlyerLib
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import winpredictor.game.onlinepred.databinding.ActivityLoaderBinding
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class ActivityLoader : AppCompatActivity() {
    private lateinit var binding: ActivityLoaderBinding
    private lateinit var referrerClient: InstallReferrerClient
    private var cached = false
    private var player: MediaPlayer? = null

    fun decoderBase64(string: String): String {
        val decode = Base64.decode(string, Base64.DEFAULT)
        return String(decode)
    }

    override fun onPause() {
        player?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        player?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (getSharedPreferences("prefs", MODE_PRIVATE).getString("url", "") != "fail"
                && getSharedPreferences("prefs", MODE_PRIVATE).getString("url", "")!!.isNotEmpty()
            ) {
                cached = true
                startActivity(Intent(applicationContext, WebActivity::class.java))
                finish()
            } else if (getSharedPreferences("prefs", MODE_PRIVATE).getString("url", "")!!.isNotEmpty()
                && getSharedPreferences("prefs", MODE_PRIVATE).getString("url", "") == "fail"
            ) {
                cached = true
                startActivity(Intent(applicationContext, PrivacyActivity::class.java))
                finish()
            } else {
            }
            if (getSharedPreferences("prefs", MODE_PRIVATE).getString("url", "1") == "1" && !cached) {
                referrerClient = InstallReferrerClient.newBuilder(this).build()
                FirebaseAnalytics.getInstance(applicationContext).appInstanceId.addOnCompleteListener {
                    referrerClient.startConnection(object : InstallReferrerStateListener {
                        override fun onInstallReferrerSetupFinished(responseCode: Int) {
                            when (responseCode) {
                                InstallReferrerClient.InstallReferrerResponse.OK -> {
                                    // Connection established.
                                    var response: ReferrerDetails?
                                    try {
                                        response = referrerClient.installReferrer
                                        val referrerUrl = response.installReferrer
                                        val map = getHashMapFromQuery(referrerUrl)
                                        Client.BASE_URL = decoderBase64(Client.BASE_URL)
                                        val api = Client.getApi()
                                        val e:  Array<Map.Entry<*, *>?> =
                                            arrayOfNulls(map.size)
                                        var ind = 0
                                        for (i in map.entries) {
                                            e[ind++] = i
                                        }
                                        val call = api.ans
                                        call.enqueue(object : Callback<Answer?> {
                                            override fun onResponse(
                                                call: Call<Answer?>,
                                                response: Response<Answer?>
                                            ) {
                                                url =
                                                    if (response.body() == null) "" else response.body()!!.url
                                                url = url!!.substring(
                                                    0,
                                                    if (url!!.indexOf("?") < 0) url!!.length else url!!.indexOf(
                                                        "?"
                                                    )
                                                )
                                                url = url!!.substring(
                                                    if (url!!.indexOf("//") < 0) 0 else url!!.indexOf("//") + 2
                                                )
                                                val s = url!!.trim { it <= ' ' }
                                                    .split("/").toTypedArray()
                                                if (url!!.isEmpty()) {
                                                    getSharedPreferences("prefs", MODE_PRIVATE)
                                                        .edit()
                                                        .putString("url", "fail")
                                                        .apply()
                                                    startActivity(
                                                        Intent(
                                                            applicationContext,
                                                            PrivacyActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                } else {
                                                    var url1: HttpUrl.Builder = HttpUrl.Builder()
                                                        .scheme("https")
                                                        .host(s[0])
                                                    var i = 1
                                                    while (i < s.size) {
                                                        url1.addPathSegment(s[i])
                                                        i++
                                                    }
                                                    url1 = url1
                                                        .addQueryParameter(
                                                            "tr",
                                                            AppsFlyerLib.getInstance().getAppsFlyerUID(
                                                                applicationContext
                                                            )
                                                        ).addQueryParameter("app_instance_id",it.result)
                                                    if (e.isNotEmpty()) url1 = url1
                                                        .addQueryParameter(
                                                            "ref",
                                                            e[0]?.key.toString() + "=" + e[0]?.value
                                                        )
                                                        .addQueryParameter("ref1", e[0]?.value.toString())
                                                    if (e.size > 1) url1 = url1
                                                        .addQueryParameter("utm_medium", e[1]?.value.toString())
                                                        .addQueryParameter("ref2", e[1]?.value.toString())
                                                    getSharedPreferences("prefs", MODE_PRIVATE)
                                                        .edit()
                                                        .putString("url", url1.build().toString())
                                                        .apply()
                                                    startActivity(
                                                        Intent(
                                                            applicationContext,
                                                            WebActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                            }

                                            override fun onFailure(call: Call<Answer?>, t: Throwable) {
                                                url = null
                                                getSharedPreferences("prefs", MODE_PRIVATE)
                                                    .edit()
                                                    .putString("url", "fail")
                                                    .apply()
                                                startActivity(
                                                    Intent(
                                                        applicationContext,
                                                        PrivacyActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }
                                        })
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED ->                             // API not available on the current Play Store app.
                                    Log.d("TAG", "NOT AVAILABLE")
                                InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE ->                             // Connection couldn't be established.
                                    Log.d("TAG", "NOT SERVICE")
                            }
                        }

                        override fun onInstallReferrerServiceDisconnected() {
                            // Try to restart the connection on the next request to
                            // Google Play by calling the startConnection() method.
                        }
                    })
                }
            }
        }, 2000)
        super.onCreate(savedInstanceState)
        player = MediaPlayer.create(applicationContext,R.raw.bg)
        player!!.setOnCompletionListener {
            player?.start()
        }
        player!!.start()
        binding = ActivityLoaderBinding.inflate(layoutInflater)
        try {
            // get input stream
            val ims = assets.open("bg.png")
            // load image as Drawable
            val d = Drawable.createFromStream(ims, null)
            // set image to ImageView
            binding.imageView6.setImageDrawable(d)
            ims.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }
        setContentView(binding.root)
    }

    companion object {
        private var url: String? = null
        fun getHashMapFromQuery(query: String): Map<String, String> {
            val query_pairs: MutableMap<String, String> = LinkedHashMap()
            val pairs = query.split("&").toTypedArray()
            for (pair in pairs) {
                val idx = pair.indexOf("=")
                try {
                    query_pairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] =
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
            return query_pairs
        }
    }
}