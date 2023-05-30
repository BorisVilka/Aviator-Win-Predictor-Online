package winpredictor.game.onlinepred

import android.app.Application
import android.media.MediaPlayer
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.onesignal.OneSignal

class MyApp : Application() {



    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        AppsFlyerLib.getInstance()
            .init("FDCL5fL8dd8YArakyjfSjJ", object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(map: Map<String, Any>) {}
                override fun onConversionDataFail(s: String) {}
                override fun onAppOpenAttribution(map: Map<String, String>) {}
                override fun onAttributionFailure(s: String) {}
            }, this)
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setDebugLog(true)

    }

    companion object {
        private const val ONESIGNAL_APP_ID = "1246a16f-7517-4366-a35c-7097a5b4302b"
    }
}
