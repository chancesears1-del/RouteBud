package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.ui.theme.AppSkin
import com.example.ui.theme.getSkinChatColors

@Composable
fun ChatLogView(
    messages: List<ChatMessage>,
    activeSkin: AppSkin,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val skinChatColors = getSkinChatColors(activeSkin)

    // Scroll to latest message when messages list updates
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_log_list")
    ) {
        items(messages, key = { it.id }) { msg ->
            ChatBubbleItem(
                message = msg,
                skinChatColors = skinChatColors
            )
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    skinChatColors: com.example.ui.theme.SkinChatColors
) {
    val alignment = if (message.isSent) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isSent) skinChatColors.bubbleSentBg else skinChatColors.bubbleReceivedBg
    val textColor = if (message.isSent) skinChatColors.bubbleSentText else skinChatColors.bubbleReceivedText

    val shape = if (message.isSent) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isSent) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if (!message.isSent) {
                Text(
                    text = "🤖 ",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp, end = 2.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(bgColor)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = textColor,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
