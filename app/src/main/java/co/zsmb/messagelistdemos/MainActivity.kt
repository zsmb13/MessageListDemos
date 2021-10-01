package co.zsmb.messagelistdemos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.zsmb.messagelistdemos.MessageListActivity.Companion.KEY_MODE
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.ChatDomain

const val SAMPLE_CID = "messaging:tutorial-app-channel-3"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initChatSdk()

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val buttonModifier = Modifier.padding(8.dp)
                Button(
                    modifier = buttonModifier,
                    onClick = { openMessageList() }
                ) {
                    Text("Default Message List")
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { openMessageList("colorful") }
                ) {
                    Text("Colorful Message List")
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { openMessageList("livestream") }
                ) {
                    Text("Livestream Message List")
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { openMessageList("team") }
                ) {
                    Text("Team Chat Message List")
                }
            }
        }
    }

    private fun openMessageList(mode: String? = null) {
        val intent = Intent(this, MessageListActivity::class.java)
        if (mode != null) {
            intent.putExtra(KEY_MODE, mode)
        }
        startActivity(intent)
    }

    private fun initChatSdk() {
        val client = ChatClient.Builder("b67pax5b2wdq", applicationContext)
            .logLevel(ChatLogLevel.ALL)
            .build()
        ChatDomain.Builder(client, applicationContext).build()

        val user = User(
            id = "tutorial-droid",
            extraData = mutableMapOf(
                "name" to "Tutorial Droid",
                "image" to "https://bit.ly/2TIt8NR",
            ),
        )
        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.NhEr0hP9W9nwqV7ZkdShxvi02C5PR7SJE7Cs4y7kyqg"
        ).enqueue()
    }
}
