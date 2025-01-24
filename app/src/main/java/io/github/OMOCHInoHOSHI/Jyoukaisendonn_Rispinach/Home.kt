package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

//import androidx.compose.ui.draw.EmptyBuildDrawCacheParams.size
//import kotlin.collections.EmptyList.size
import android.graphics.Bitmap
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import fetchImagesFromFirebaseStorage
import kotlinx.coroutines.launch

data class ImageData(
    val bitmap: Bitmap,     // 画像のビットマップ情報
    val title: String,      // タイトル
    val name: String,       // 生物名
    val location: String,       // 住所
    val discoveryDate: String,      // 日付
    val id: Int,        // ID
    val Dkey: String,
    val latitude: Double?, // 緯度
    val longitude: Double? // 経度
)

// Firebadeの情報を共有(pictureNameに投稿情報を格納)
class ImageViewModel : ViewModel() {
    var pictureName by mutableStateOf(listOf<ImageData>())
        private set

    fun fetchImages() {
        viewModelScope.launch {
            fetchImagesFromFirebaseStorage { images ->
                pictureName = images
            }
        }
    }
}
//enum class ImageItems(
//    val id: String,
//    val image: Image
//){
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(imageViewModel: ImageViewModel = viewModel())
{
    // _uploadSuccessの値が変わったら、Homeを更新したい
    // Post_SucsessViewModelのインスタンスを取得
    val post_viewModel = viewModel<Post_SucsessViewModel>()
    post_viewModel.checkUploadSuccess()
    println("Home　post_viewModel.checkUploadSuccess() = ")
    println(post_viewModel.checkUploadSuccess())

    var poet_scsess by remember { mutableStateOf(false) }
    poet_scsess = post_viewModel.checkUploadSuccess()

    fun onUploadSuccess() {
        // アップロード成功時の処理
        Log.d("Callback", "アップロード成功時の処理を実行します")
    }

    fun onUploadFailure() {
        // アップロード失敗時の処理
        Log.d("Callback", "アップロード失敗時の処理を実行します")
    }
    post_viewModel.setOnUploadStatusChangedListener { success ->
        if (success) {
            Log.d("UploadStatus", "アップロードが成功しました！")
            // ここで別の関数を呼び出す
            onUploadSuccess()
        } else {
            Log.d("UploadStatus", "アップロードに失敗しました。")
            // 必要に応じて処理
            onUploadFailure()
        }
    }


    SideEffect { Log.d("compose-log", "Home") }
    //val itemsIndexedList = rememberSaveable { mutableListOf("A", "B", "C", "D", "E")}
    //var activePhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    //var openBottomSheet by remember { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    // ロード中フラグ
    var isLoading by rememberSaveable { mutableStateOf(true) }
    //var n="R.drawable.test1"
    /*コメントアウト(中村)
    var pictureName=rememberSaveable { mutableListOf(
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("tizu_kakkokari1",R.drawable.tizu_kakkokari1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1),
        ImageData("test1",R.drawable.test1) )}
     */
    //var pictureName=rememberSaveable { mutableListOf(n.toInt(),R.drawable.tizu_kakkokari1 )}
    //var pictureName=R.drawable.test1
    var lsName=""
    //var open=openBottomSheet

    var lod by rememberSaveable { mutableStateOf(false) }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    // ファイヤーベースを再読み込みS--------------------------------------------
    if(isRefreshing){
        println("再ロード中")
        // Firebase Storageからデータを読み込む
        LaunchedEffect(Unit) {
            fetchImagesFromFirebaseStorage { images ->
//            pictureName = images
                isLoading = false // ロード完了後にフラグをfalseにする
                isRefreshing = false
            }
            imageViewModel.fetchImages()
        }

    }
    // ファイヤーベースを再読み込みE--------------------------------------------


    // Firebase Storageからデータを読み込む
    LaunchedEffect(Unit) {
        fetchImagesFromFirebaseStorage { images ->
//            pictureName = images
            isLoading = false // ロード完了後にフラグをfalseにする
        }
        imageViewModel.fetchImages()
    }

    Column(
        modifier = Modifier
    )
    {
        //地図枠(仮)
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp))
        {
//            Image(
//                modifier = Modifier.padding(start = 0.dp, top = 0.dp,end=0.dp, bottom = 225.dp),
//                painter = painterResource1(R.drawable.tizu_kakkokari1), contentDescription = "test"
//            )
            //MapContent()      // コメントアウト(中村)
            MapMarkers()        // マーカー付き地図
            SideEffect { Log.d("compose-log", "Map") }
            //デバッグ用
//            Text("main/map")
            println("map")
        }


        // くるくる表示S-----------------------------------------------
        if(isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center // 中央揃え
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp), // サイズを指定
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        // くるくる表示E-----------------------------------------------

        // Pull to Refresh を設定　下スワイプで更新できるS--------------------------
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
                println("更新中")
            }
        ) {
            // Pull to Refresh を設定　下スワイプで更新できるE--------------------------
            //投稿(仮)
            Column(modifier = Modifier.fillMaxSize())
            {
                Text("過去の投稿")
                LazyVerticalGrid(
                    modifier = Modifier,
                    columns = GridCells.Fixed(3),
                    //columns = GridCells.FixedSize(/*minSize = */128.dp)
                    //columns = GridCells.Adaptive(minSize = 128.dp),
                )
                {

//            itemsIndexed(pictureName)
//            {index, item ->
//                items(item) { pName ->
//                    PhotoItem(pName)
//                }
//            }
                    itemsIndexed(imageViewModel.pictureName/*itemsIndexedList*/)
                    { index, item ->
                        //var checked by rememberSaveable(index) { mutableStateOf(false)}
                        var checked: MyDto by rememberSaveable(index, stateSaver = MyDtoSaver) {
                            mutableStateOf(
                                MyDto(false)
                            )
                        }
                        var openBottomSheet by remember(index) { mutableStateOf(false) }
                        //var checked: MyDto by rememberLazyListState(index,stateSaver = MyDtoSaver) { mutableStateOf(MyDto(false)) }
                        //lsName=itemsIndexedList.get(index)
                        //lsName= item[index].toString()
                        //var lsName=itemsIndexedList[index]
                        //var lName=rememberSaveable(index) { item }
                        lsName = rememberSaveable(index) { mutableStateOf("") }.toString()
                        //lsName=itemsIndexedList[index]
                        Image(
                            //painter = painterResource1(pictureName[index]), contentDescription = "test",
                            //painter = painterResource1(pictureName[index]), contentDescription = "test",
                            bitmap = item.bitmap.asImageBitmap(),       // 画像を表示するためのBitmap
                            contentScale = ContentScale.Crop,
                            //painter = painterResource1(pictureName[index].id),コメントアウト(中村)
                            contentDescription = imageViewModel.pictureName[index].name,
                            modifier = Modifier
                                .size(128.dp)
                                .padding(2.dp)
                                .clickable
                                {
                                    //lsName=itemsIndexedList[index]
                                    println(/*itemsIndexedList*/imageViewModel.pictureName[index].name)
                                    openBottomSheet = true
                                },
                        )
                        Box()
                        {
                            IconToggleButton(
//                        checked = checked,
//                        onCheckedChange = {checked = it },
                                onCheckedChange = { checked = MyDto(it) },
                                checked = checked.data,


                                )
                            {
                                val tint by animateColorAsState(
                                    if (checked.data/*checked*/) {
                                        Color(0xFFEC407A)
                                    } else {
                                        Color(0xFFB0BEC5)
                                    }
                                )
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = "Localized description",
                                    tint = tint
                                )
//                        if(checked.data==true)
//                        {
//                            SideEffect { Log.d("compose-log", "true") }
//                        }
//                        else
//                        {
//                            SideEffect { Log.d("compose-log", "false") }
//                        }
                            }
                        }
                        if (openBottomSheet) {
                            ModalBottomSheet(
                                modifier = Modifier,
                                //modifier = Modifier.padding(top = 16.dp),
                                onDismissRequest = { openBottomSheet = false },
                                sheetState = bottomSheetState,
                            )
                            {
                                Column(
                                    modifier = Modifier
                                    //.padding(start = 16.dp, bottom = 50.dp)
                                    //.imePadding()//.padding(start = 16.dp, bottom = 24.dp)
                                ) {
                                    println(imageViewModel.pictureName[index].name)
                                    Posts(
                                        imageViewModel.pictureName[index].bitmap,
                                        imageViewModel.pictureName[index].name,
                                        imageViewModel.pictureName[index].title,
                                        imageViewModel.pictureName[index].location,
                                        imageViewModel.pictureName[index].discoveryDate,
                                        imageViewModel.pictureName[index].latitude,
                                        imageViewModel.pictureName[index].longitude,
                                        imageViewModel.pictureName[index].id,
                                    )        // 画像情報、生物名、idを送る場合

                                    //LoginScreen()
                                    //Posts(pictureName[index], lsName)
//                            println(imageViewModel.pictureName[index].name)
//                            Posts(imageViewModel.pictureName[index].bitmap, imageViewModel.pictureName[index].name, imageViewModel.pictureName[index].id)        // 画像情報、生物名、idを送る場合
                                    //Posts(pictureName[index].bitmap, pictureName[index].name, pictureName[index].location, pictureName[index].discoveryDate, pictureName[index].id)       // 全てのデータを送る場合
//                            println(pictureName[index].name)
                                    //LoginScreen()
                                    //Posts(pictureName[index].id, pictureName[index].name, pictureName[index].name /* 仮置き */)
//            BottomSheetIconTextRow(icon = R.drawable.baseline_share_24, text = "Share")
//            BottomSheetIconTextRow(icon = R.drawable.baseline_link_24, text = "Get link")
//            BottomSheetIconTextRow(icon = R.drawable.baseline_edit_24, text = "Edit name")
//            BottomSheetIconTextRow(icon = R.drawable.baseline_delete_24, text = "Delete collection")
                                }
                            }
                        }
                    }
                }
            }
        }
//        if (openBottomSheet)
//        {
//            ModalBottomSheet(
//                //modifier = Modifier.padding(top = 16.dp),
//                onDismissRequest = {openBottomSheet = false },
//                sheetState = bottomSheetState,
//            )
//            {
//                Column(
//                    modifier = Modifier//.padding(start = 16.dp, bottom = 24.dp)
//                ) {
//                    //Posts(pictureName[index], lsName)
//                    println(itemsIndexedList[index])
//                    Posts(pictureName, lsName)
////            BottomSheetIconTextRow(icon = R.drawable.baseline_share_24, text = "Share")
////            BottomSheetIconTextRow(icon = R.drawable.baseline_link_24, text = "Get link")
////            BottomSheetIconTextRow(icon = R.drawable.baseline_edit_24, text = "Edit name")
////            BottomSheetIconTextRow(icon = R.drawable.baseline_delete_24, text = "Delete collection")
//                }
//            }
//        }

    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PullToRefreshBasicSample(
//    isRefreshing: Boolean,
//    onRefresh: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    PullToRefreshBox(
//        isRefreshing = isRefreshing,
//        onRefresh = onRefresh,
//        modifier = modifier
//    ) {
//        LazyColumn(Modifier.fillMaxSize()) {
//
//        }
//    }
//}



////@Composable
//fun HomeMap()
//{
//
//}

class MyDto(val data: Boolean)
val MyDtoSaver = listSaver<MyDto, Any>(
    save = { mutableListOf(it.data) },
    restore = { MyDto(it[0] as Boolean) }
)

//@Composable
fun ImageList(photos:List<Photo>)
{
//    var activePhotoId by rememberSavable { mutableStateOf<Int?>(null) }
//    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
//        items(photos, { it.id }) { photo ->
//            ImageItem(
//                photo,
//                Modifier.clickable { activePhotoId = photo.id }
//            )
//        }
//    }
//    if (activePhotoId != null) {
//        FullScreenImage(
//            photo = photos.first { it.id == activePhotoId },
//            onDismiss = { activePhotoId = null }
//        )
//    }

}

