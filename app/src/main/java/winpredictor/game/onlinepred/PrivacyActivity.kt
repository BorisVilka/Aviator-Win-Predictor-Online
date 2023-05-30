package winpredictor.game.onlinepred

import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import winpredictor.game.onlinepred.databinding.ActivityPrivacyBinding
import java.io.IOException

class PrivacyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyBinding
    private var player: MediaPlayer? = null

    override fun onPause() {
        player?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        player?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("privacy", false)) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        } else {
            player = MediaPlayer.create(applicationContext,R.raw.bg)
            player!!.setOnCompletionListener {
                player?.start()
            }
            player!!.start()
        }
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        try {
            // get input stream
            val ims = assets.open("bg.png")
            // load image as Drawable
            val d = Drawable.createFromStream(ims, null)
            // set image to ImageView
            binding.imageView2.setImageDrawable(d)
            ims.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
        setContentView(binding.root)
        binding.button5.setOnClickListener { view ->
            getSharedPreferences("prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("privacy", true)
                .apply()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        binding.checkBox.setOnCheckedChangeListener { _, b ->
            binding.button5.isEnabled = b
        }
        binding.textView2.movementMethod =
            LinkMovementMethod.getInstance()
        binding.textView2.setLinkTextColor(getColor(R.color.teal_200))
        binding.textView2.linksClickable = true
    }
}