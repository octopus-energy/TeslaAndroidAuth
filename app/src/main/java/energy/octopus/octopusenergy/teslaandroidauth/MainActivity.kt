package energy.octopus.octopusenergy.teslaandroidauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import energy.octopus.octopusenergy.teslauth.TeslAuth

const val TAG = "tesla_auth_test"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeslAuth(onSuccess = {
                Log.i(TAG, "Got auth token $it")
            }, onError = {
                Log.i(TAG, "Got error $it")
            })
        }
    }
}