package energy.octopus.octopusenergy.teslaandroidauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import energy.octopus.octopusenergy.teslauth.TeslAuth
import energy.octopus.octopusenergy.teslauth.logging.LogLevel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var message by remember { mutableStateOf<String?>(null) }
            if (message == null) {
                TeslAuth(
                    logLevel = LogLevel.DEFAULT,
                    onSuccess = {
                        message = it.toString()
                    }, onError = {
                        message = it.toString()
                    })
            } else {
                Box(Modifier.padding(16.dp)) {
                    Text(
                        message ?: "",
                        Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}