package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Posts(pName:Int,lName:String,cName:String)
{
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment=Alignment.Center
        //            modifier = Modifier
//                .align(Alignment.TopCenter)
    )
    {
        Image(
            painter = painterResource(pName),
            contentDescription = "test",
            //
//            modifier = Modifier
//                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart),
        )
        {
            Column()
            {
                OutlinedText(
                    text = "名前:$lName",
                    stroke = Stroke(width = 4.0f),
                    textStyle = TextStyle(
                        fontSize = 20.sp
                    ),
                    strokeColor = Color.White
                )

            }
        }

        Box(
            modifier = Modifier
            .align(Alignment.BottomEnd),//iマークの位置設定
            )
        {
            var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            val bottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = skipPartiallyExpanded
            )
            var openBottomSheet by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    openBottomSheet = true
                }
            )
            {


                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Info",
                    modifier = Modifier
                        .border(2.dp, Color.White, RoundedCornerShape(20.dp)),
                    tint= Color(0xFFB0BEC5))
                if (openBottomSheet)
                {
                    ModalBottomSheet(
                        //modifier = Modifier.padding(top = 16.dp),
                        onDismissRequest = {openBottomSheet = false },
                        sheetState = bottomSheetState,
                    )
                    {
                        Column(
                            modifier = Modifier
                        )
                        {
                            Row()
                            {
                                Text("場所:")
                                Text(
                                    text = cName,
                                    textDecoration = TextDecoration.Underline,
                                    color = Color.Blue
                                    )
                            }
                        }
                    }
                }
            }

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
    var textLayoutResult: TextLayoutResult? by remember {
        mutableStateOf(null)
    }
    Text(
        text = text,
        style = textStyle,
        onTextLayout = {
            textLayoutResult = it
        },
        modifier = modifier
            .drawBehind {
                textLayoutResult?.let {
                    drawText(
                        textLayoutResult = it,
                        drawStyle = stroke,
                        color = strokeColor,
                    )
                }
            }
    )
}