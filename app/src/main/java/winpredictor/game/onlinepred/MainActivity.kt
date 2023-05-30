package winpredictor.game.onlinepred

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

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
        super.onCreate(savedInstanceState)
        player = MediaPlayer.create(applicationContext,R.raw.bg)
        player!!.setOnCompletionListener {
            player?.start()
        }
        player!!.start()
        setContentView(R.layout.activity_main)
    }
}