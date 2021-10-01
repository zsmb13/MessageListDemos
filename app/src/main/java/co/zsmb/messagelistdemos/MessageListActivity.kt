package co.zsmb.messagelistdemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.state.messages.items.Bottom
import io.getstream.chat.android.compose.state.messages.items.MessageItem
import io.getstream.chat.android.compose.state.messages.items.MessageItemGroupPosition
import io.getstream.chat.android.compose.state.messages.items.Middle
import io.getstream.chat.android.compose.state.messages.items.None
import io.getstream.chat.android.compose.state.messages.items.Top
import io.getstream.chat.android.compose.ui.attachments.content.MessageAttachmentsContent
import io.getstream.chat.android.compose.ui.common.MessageBubble
import io.getstream.chat.android.compose.ui.common.Timestamp
import io.getstream.chat.android.compose.ui.common.avatar.Avatar
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import kotlin.math.abs

class MessageListActivity : ComponentActivity() {

    companion object {
        const val KEY_MODE = "KEY_MODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra(KEY_MODE)

        val vmFactory = MessagesViewModelFactory(context = this, channelId = SAMPLE_CID)
        val listViewModel: MessageListViewModel by viewModels { vmFactory }

        setContent {
            ChatTheme {
                if (mode == null) {
                    MessageList(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = listViewModel,
                    )
                } else {
                    MessageList(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = listViewModel,
                        itemContent = { item ->
                            when (mode) {
                                "colorful" -> ColorfulChatItemContent(item)
                                "livestream" -> LiveStreamItemContent(item)
                                "team" -> TeamChatItemContent(item)
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun RowScope.MessageAvatar(
        position: MessageItemGroupPosition,
        user: User,
    ) {
        if (position == Bottom || position == None) {
            UserAvatar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.Bottom),
                user = user
            )
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }
    }

    @Composable
    fun ColorfulChatItemContent(messageItem: MessageItem) {
        val (message, position) = messageItem

        val ownsMessage = messageItem.isMine

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = if (ownsMessage) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Row(
                Modifier.widthIn(max = 300.dp)
            ) {

                if (!ownsMessage) {
                    MessageAvatar(position, message.user)
                }

                Column(horizontalAlignment = if (ownsMessage) Alignment.End else Alignment.Start) {
                    val bubbleShape = RoundedCornerShape(16.dp).let { shape ->
                        when (position) {
                            Top, Middle -> shape
                            else -> when (ownsMessage) {
                                true -> shape.copy(bottomEnd = CornerSize(0))
                                false -> shape.copy(bottomStart = CornerSize(0))
                            }
                        }
                    }

                    val colors = arrayOf(
                        0xFFFFADAD, 0xFFFFD6A5, 0xFFFDFFB6, 0xFFCAFFBF, 0xFF9BF6FF, 0xFFA0C4FF, 0xFFBDB2FF, 0xFFFFC6FF,
                    ).map(::Color)
                    val bubbleColor = colors[abs(message.user.id.hashCode()) % colors.size]

                    MessageBubble(
                        modifier = Modifier.widthIn(max = 250.dp),
                        shape = bubbleShape,
                        color = bubbleColor,
                        border = null,
                        content = {
                            Column {
                                MessageAttachmentsContent(message = messageItem.message, onLongItemClick = {})

                                if (message.text.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        text = message.text,
                                        style = ChatTheme.typography.bodyBold
                                    )
                                }
                            }
                        }
                    )

                    val spacerSize = if (position == None || position == Bottom) 4.dp else 2.dp
                    Spacer(Modifier.size(spacerSize))
                }

                if (ownsMessage) {
                    MessageAvatar(position, message.user)
                }
            }
        }
    }

    @Composable
    fun LiveStreamItemContent(messageItem: MessageItem) {
        val message = messageItem.message
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 4.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0x443474EB))
                .padding(4.dp)
                .widthIn(max = 300.dp)
        ) {
            Row {
                Avatar(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp),
                    painter = rememberImagePainter(data = message.user.image)
                )

                Text(
                    modifier = Modifier.padding(2.dp),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(message.user.name)
                        }
                        append("  ")
                        append(message.text)
                    },
                    style = ChatTheme.typography.bodyBold,
                    fontSize = 14.sp
                )
            }
        }
    }

    @Composable
    fun TeamChatItemContent(messageItem: MessageItem) {
        val message = messageItem.message
        val firstItem = messageItem.groupPosition == Top || messageItem.groupPosition == None

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .widthIn(max = 300.dp)
        ) {
            if (firstItem) {
                Spacer(Modifier.padding(4.dp))
            }

            Row {
                if (firstItem) {
                    Image(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        painter = rememberImagePainter(data = message.user.image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Spacer(modifier = Modifier.size(width = 36.dp, height = 0.dp))
                }

                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    if (firstItem) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.user.name,
                                style = TextStyle(fontWeight = FontWeight.Bold),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.size(4.dp))
                            Timestamp(date = message.updatedAt ?: message.createdAt)
                        }
                        Spacer(Modifier.padding(2.dp))
                    }
                    Text(text = message.text.trim())
                    Spacer(Modifier.padding(2.dp))
                }
            }
        }
    }

}
