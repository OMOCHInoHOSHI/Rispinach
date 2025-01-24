package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 日付選択ダイアログのコンポーネントS-------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
// 日付選択ダイアログのコンポーネントE-------------------------------------------------

// 日付選択ドロップダウンS-----------------------------------------------------------------------
@Composable
fun SelectOutlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current.copy(textIndent = TextIndent(firstLine = 6.sp)) // デフォルトのテキストインデントを設定
) {
    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Press -> {
                        onClick()
                    }
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val iconWidth = 48.dp.toPx() // アイコンの幅を考慮
                val padding = 8.dp.toPx() // アイコンと線の間のパディング
                val x = iconWidth + padding + strokeWidth / 2
                drawLine(
                    color = Color.Gray,
                    start = Offset(x - 11, 0f + 22),
                    end = Offset(x - 11, size.height - 22),
                    strokeWidth = strokeWidth
                )
            },
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "  選択してください") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
            )
        },
        readOnly = true,    //読み取り専用
        interactionSource = interactionSource,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        textStyle = textStyle // テキストのインデントを設定
    )
}
// 日付選択ドロップダウンE-----------------------------------------------------------------------

// 日付フォーマットS----------------------------------------------------------------------------
fun convertMillisToDate(millis: Long?): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return millis?.let {
        formatter.format(Date(it))
    } ?: ""
}
// 日付フォーマットE----------------------------------------------------------------------------
