package com.yosuke.nfcloader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.layout.Center
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import java.util.*

//TODO: アンチパターンまみれだと思うので要修正
class MainActivity : AppCompatActivity() {
    private val nfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }
    private val state by lazy { +state { "" } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Center {
                    ReadResult(state.value)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // NFCがかざされたときの設定
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        // ほかのアプリを開かないようにする
        val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                0
        )
        nfcAdapter.enableForegroundDispatch(
                this,
                pendingIntent,
                null,
                null
        )
    }

    override fun onPause() {
        nfcAdapter.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
        intent.getByteArrayExtra(NfcAdapter.EXTRA_ID).also {
            state.value = Arrays.toString(it)
            Log.d("read", state.value)
        }
    }
}

@Composable
fun ReadResult(result: String) {
    Text(text = "Read NFC -> $result")
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Center {
            ReadResult("Android")
        }
    }
}
