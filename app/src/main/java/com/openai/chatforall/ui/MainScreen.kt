@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalGlideComposeApi::class)

package com.openai.chatforall.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.openai.chatforall.R
import com.openai.chatforall.data.MessageData
import com.openai.chatforall.data.MessageViewType
import com.openai.chatforall.data.RequestType
import kotlinx.coroutines.launch

@Composable
fun MainScreen(downloadImage: (String) -> Unit) {
    val viewModel: MainViewModel = viewModel()
    val messageList = viewModel.messageList.value
    val apiKey = stringResource(R.string.apikey)
    ChatScreen(
        messageList = messageList,
        { viewModel.selectedRequestType = RequestType.GPT },
        { viewModel.selectedRequestType = RequestType.DALLE },
        { prompt -> viewModel.sendRequest(apiKey, prompt) },
        downloadImage
    )
}

@Composable
fun ChatScreen(
    messageList: List<MessageData>,
    onGptClicked: () -> Unit,
    onDallEClicked: () -> Unit,
    onSendClicked: (String) -> Unit,
    downloadImage: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = Color("#F9F9F9".toColorInt()))
            .fillMaxSize()
            .testTag("ChatScreen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        ButtonRow(
            onGptClicked = onGptClicked, onDallEClicked = onDallEClicked
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            Modifier.weight(1f)
        ) {
            MessageList(messageList = messageList, downloadImage)
        }
        Spacer(modifier = Modifier.height(8.dp))
        InputSection(onSendClicked = onSendClicked)
    }
}

@Composable
fun MessageList(messageList: List<MessageData>, downloadImage: (String) -> Unit) {
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        Modifier
            .padding(start = 16.dp, end = 16.dp)
            .testTag("MessageList"), content = {
            coroutineScope.launch {
                lazyColumnListState.animateScrollToItem(messageList.size - 1)
            }
            items(messageList) { message ->
                when (message.messageType) {
                    MessageViewType.MESSAGE_USER -> UserMessage(message)
                    MessageViewType.MESSAGE_OTHER -> OtherMessage(message, downloadImage)
                    MessageViewType.MESSAGE_LOADING -> LoadingMessage()
                }
            }
        })
}

@Composable
fun InputSection(onSendClicked: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .testTag("InputSection"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
                .background(
                    Color("#F8F7FF".toColorInt()),
                    shape = RoundedCornerShape(10.dp)
                )
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { onSendClicked(text); text = "" }) {
            Icon(
                modifier = Modifier.size(20.dp),
                tint = Color("#5632FB".toColorInt()),
                imageVector = ImageVector.vectorResource(R.drawable.ic_send),
                contentDescription = "Send"
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun ButtonRow(onGptClicked: () -> Unit, onDallEClicked: () -> Unit) {
    var isGhatGpt by remember { mutableStateOf(true) }
    val colorChatGPT =
        if (isGhatGpt) Color("#ffffff".toColorInt()) else Color("#efefef".toColorInt())
    val colorDALLE =
        if (!isGhatGpt) Color("#ffffff".toColorInt()) else Color("#efefef".toColorInt())
    Row(
        modifier = Modifier
            .wrapContentSize()
            .background(
                Color("#EEEEEE".toColorInt()), RoundedCornerShape(15.dp)
            )
            .testTag("ButtonRow"),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            onClick = {
                isGhatGpt = true
                onGptClicked.invoke()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorChatGPT)
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_gpt),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "GPT")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
            onClick = {
                isGhatGpt = false
                onDallEClicked.invoke()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorDALLE)
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_dalle),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "DALL-E")
        }
    }
}

@Composable
fun UserMessage(message: MessageData) {
    message.content?.let {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("UserMessage"), horizontalAlignment = Alignment.End
        ) {
            Text(
                text = it,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        Color("#FFFFFF".toColorInt()),
                        shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
                    )
                    .padding(16.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageSaver(imageUrl: String, downloadImage: (String) -> Unit) {
    Box(
        Modifier
            .wrapContentSize()
            .background(
                Color("#5632FB".toColorInt())
            )
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(6.dp)
                .width(256.dp)
                .height(256.dp)
        )
        Icon(
            modifier = Modifier
                .padding(10.dp)
                .size(48.dp)
                .background(
                    Color("#66DEDEDE".toColorInt()),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { downloadImage(imageUrl) }
                .align(Alignment.TopEnd),
            tint = Color("#5632FB".toColorInt()),
            painter = painterResource(R.drawable.ic_save),
            contentDescription = "Save"
        )
    }
}


@Composable
fun OtherMessage(message: MessageData, downloadImage: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .testTag("OtherMessage"), horizontalAlignment = Alignment.Start
    ) {
        message.imageData?.get(0)?.url?.let {
            ImageSaver(imageUrl = it, downloadImage)
        }
        message.content?.let {
            Text(
                text = it,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .background(
                        Color("#5632FB".toColorInt()),
                        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                    )
                    .padding(16.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun LoadingMessage() {
    Column(
        Modifier
            .wrapContentHeight()
            .padding(top = 8.dp)
            .background(
                Color("#5632FB".toColorInt()),
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
            )
            .width(100.dp)
            .testTag("LoadingMessage"),
        horizontalAlignment = Alignment.Start
    ) {
        AndroidView(
            {
                SpinKitView(it).apply {
                    setIndeterminateDrawable(ThreeBounce())
                    setColor(android.graphics.Color.WHITE)
                }
            },
            modifier = Modifier
                .testTag("Loading")
                .padding(
                    start = 25.dp, top = 8.dp, bottom = 8.dp, end = 16.dp
                )
                .height(32.dp)
                .height(50.dp)
        )
    }
}

@Preview
@Composable
fun Preview() {
    ChatScreen(listOf(
        MessageData("selam", null, MessageViewType.MESSAGE_USER),
        MessageData("iyi senden", null, MessageViewType.MESSAGE_OTHER),
        MessageData(messageType = MessageViewType.MESSAGE_LOADING)
    ), {}, {}, {}, {})
}
