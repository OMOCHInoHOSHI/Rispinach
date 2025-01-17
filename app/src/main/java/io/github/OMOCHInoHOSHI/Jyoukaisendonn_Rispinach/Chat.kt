package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp

@Composable
fun Conversation(messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(bottom = 1.dp) // 必要に応じてパディングを調整
    ) {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(msg: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
    )

    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row()
    {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = modifier
                //.fillMaxWidth()
                .width(300.dp)
                .padding(8.dp),
            maxLines = 5,
            singleLine = false,
            label = { Text("メッセージを入力") }
        )

        //投稿ボタン
        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically),
            onClick = {/*投稿処理*/}
        )
        {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "投稿",
                modifier = Modifier
                    .size(100.dp),
                //.border(2.dp, Color.White, RoundedCornerShape(20.dp)),
                tint = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    stroke: Stroke = Stroke(),
    strokeColor: Color = Color.Transparent,
) {
    var textLayoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    BasicText(
        text = text,
        style = textStyle,
        onTextLayout = { textLayoutResult = it },
        modifier = modifier
            .padding(4.dp)
            .drawBehind {
                textLayoutResult?.let {
                    drawText(
                        textLayoutResult = it,
                        color = strokeColor,
                        //style = textStyle.copy(drawStyle = stroke)
                    )
                }
            }
    )
}