package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach.ui.theme.RispinachTheme
import kotlinx.coroutines.delay

enum class MainScreenTab(
    var id: String,
    val icon: ImageVector,
    val label: String,
    val idx:Int,
    //val enabled:Boolean
)
{
    Home(
        id = "main/home",
        icon = Icons.Outlined.Home,
        label = "Home",
        idx=0,
       // enabled = true
    ),
    Camera(
        id = "main/camera",
        icon = Icons.Outlined.Camera,
        label = "Camera",
        idx=1,
      //  enabled = true
    ),
    Map(
        id = "main/map",
        icon = Icons.Outlined.Map,
        label = "Map",
        idx=2,
        //enabled = true
    ),
    MyPage(
    id = "main/mypage",
    icon = Icons.Outlined.AccountCircle,
    label = "MyPage",
    idx=3
    //enabled = true
    )
}

//レイアウトはここに追加していく
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(/*onBClick:(()->Unit)?=null,*/)
{
    SideEffect { Log.d("compose-log", "MainScreen") }
    var drawerState by remember { mutableStateOf(DrawerState(initialValue = DrawerValue.Closed)) }
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    //val navBackStackEntry by rememberSaveable { mutableStateOf(nestedNavController.currentBackStackEntryAsState()) }
    val currentTab = navBackStackEntry?.destination?.route
    val localDensity = LocalDensity.current
    var bottomBarHeight by remember { mutableStateOf(0.dp) }
    var topBarHeight by remember { mutableStateOf(0.dp) }
    var btmEnabled by rememberSaveable { mutableStateOf(true) }
    var selectButton=currentTab
    var DismissibleDrawerEnabled=true
    var goMap=false

    //var navEnabled by rememberSaveable { mutableStateOf(true) }
    //var n=true
    //currentTab=="main/home"
    //var x=null
    //WindowInsets.run { x?.let { navigationBars.getBottom(it) } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = DismissibleDrawerEnabled,
        drawerContent = {
            DismissibleDrawerSheet(
                modifier = Modifier.width(200.dp)
            )
            {
                println(drawerState)
                SideEffect { Log.d("compose-log", "ModalNavigationDrawer") }
                Text(text = "ナビゲーションドロワー")
                MainScreenTab.entries.forEachIndexed { index, item ->
                    if(selectButton=="main/camera")
                    {
                        selectButton="main/home"
                        //selectIndex=0
                    }
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        onClick = dropUnlessResumed()
                        {
                            //デバッグ用
                            println(item.id)
//                            if(index==item.idx)
//                            {
//                                return@NavigationBarItem
//                            }
                            if(currentTab==item.id)
                            {
                                btmEnabled=false
                                //return@dropUnlessResumed
                                //return@NavigationBarItem
                            }
                            else
                            {
                                btmEnabled=true
                            }

                            nestedNavController.navigate(item.id)
                            {
                                //launchSingleTop = true
                                popUpTo(item.id)
                                {
                                    saveState = true
                                    //inclusive=true
                                }
                                launchSingleTop = true
                                //restoreState = true
                            }
//                            nestedNavController.navigate(item.id)
//                            {
//                                restoreState=true
//                            }

                        },
                        //enabled = currentTab==item.id==(!btmEnabled),
                        //selected = currentTab == item.id/*==btmEnabled*/,
                        selected = selectButton==item.id,
                    )
                }
            }
        },
    )
    {
        Scaffold(
            modifier = Modifier,
            //ナビゲーションバー--------------------------------------------------------------------
            bottomBar = {
                NavigationBar(
                    contentColor = Color.White,
                    containerColor = Color.Black //BottomBarの背景色
                )
                {
                    SideEffect { Log.d("compose-log", "NavigationBar") }
                    MainScreenTab.entries.forEachIndexed { index, item ->

                        //var selectIndex=item.idx
//                    var selectId=item.id
//                    var selectIndex=0
//                    var selectBottom=currentTab

                        if(selectButton=="main/camera")
                        {
                            selectButton="main/home"
                            //selectIndex=0
                        }
//
//                    if(currentTab=="main/camera")
//                    {
//                        selectIndex=0
//                        selectBottom="main/home"
//                        selectId="main/home"
//                    }
//                    else
//                    {
//
//                        selectBottom=currentTab
//                        selectId=item.id
//                    }

                        NavigationBarItem(
                            colors= NavigationBarItemColors(Color.Gray,Color.Gray,Color.LightGray,Color.White,Color.White,Color.DarkGray,Color.DarkGray),
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    bottomBarHeight = with(localDensity) { coordinates.size.height.toDp() /* 高さをdpで取得*/ }
                                }
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null,
//                            )
//                            {}
//                            .pointerInput(Unit) {
//                                detectDragGestures { change, _ -> change.consume() }
//                            }
//                            .safeClickable {
//                                //デバッグ用
//                                println(item.id)
//                                if(currentTab==item.id)
//                                {
//                                    btmEnabled=false
//                                }
//                                else
//                                {
//                                    btmEnabled=true
//                                }
//                            }
//                            .clickable {
//
//                            }
                            ,
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            onClick = dropUnlessResumed()
                            {
//                            if(item.id=="main/camera")
//                            {
//                                Home()
//                            }
                                //デバッグ用
                                //println(item.id)
                                if(currentTab==item.id)
                                {
                                    btmEnabled=false
                                    //return@dropUnlessResumed
                                }
                                else
                                {
                                    btmEnabled=true
                                }

                                nestedNavController.navigate(item.id)
                                {
                                    //launchSingleTop = true

                                    println(currentTab)
                                    //println(item.id)

                                    if(currentTab!=item.id)
                                    {
                                        popUpTo(item.id)
                                        {
                                            saveState = true
                                            //inclusive=true
                                        }
                                        launchSingleTop = true
                                    }

                                    //restoreState = true
                                }
//                            nestedNavController.navigate(item.id)
//                            {
//                                restoreState=true
//                            }

                            },

                            //selected = btmEnabled
                            enabled = selectButton!=item.id/*selectId*//*currentTab!=item.id*//*!=btmEnabled*/,
                            selected = selectButton==item.id/*selectIndex == item.idx*//*==(!btmEnabled)*/,
                            //enabled = false==item.enabled,
                        )
                    }
                    LaunchedEffect(key1=btmEnabled)
                    {
                        if(!btmEnabled)
                        {
                            delay(3000)
                        }
                    }
                }
            },
            //ナビゲーションバー--------------------------------------------------------------------
            //ドロワーメニュー----------------------------------------------------------------------
            topBar = {
                if(selectButton=="main/map")
                {
                    goMap=true
                    DismissibleDrawerEnabled=false
                    drawerState = DrawerState(initialValue = DrawerValue.Closed)
                }
                else
                {
                    goMap=false
                    DismissibleDrawerEnabled=true
                }

                if(!goMap) {
                    TopAppBar(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                            topBarHeight =
                                with(localDensity) { coordinates.size.height.toDp() /* 高さをdpで取得*/ }
                        },
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            )
                            {
                                Text(
                                    text = "Rispinach",
                                    modifier = Modifier
                                        .offset(x=-28.dp)
                                )
                            }

                        },

                        navigationIcon = {
                            IconButton(
                                //modifier = Modifier.padding(start = 30.dp, top = 20.dp, end = 20.dp),
                                onClick = {
                                    println("a")
                                    //DismissibleDrawerEnabled=true
                                    //drawerState!=drawerState
                                    drawerState = DrawerState(initialValue = DrawerValue.Open)
                                    //drawerState = DrawerState(initialValue = DrawerValue.Open)
                                },
                                enabled = !goMap,
                            )
                            {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = null,
                                    //tint = Color.White,
                                    modifier = Modifier
                                        .height(60.dp)
                                        .width(60.dp)
                                    //.border(2.dp, Color.White, RoundedCornerShape(20.dp))
                                )

                            }
                        },

                        colors=TopAppBarColors(Color.Black/*TopBar背景色*/,Color.White,Color.White,Color.White,Color.White)


                    //colors = TopAppBarColors(containerColor = Color.Black)

//                backgroundColor = MaterialTheme.colors.primary,
//                contentColor = Color.White

                    )



                }
                else
                {
                    topBarHeight=0.dp
                }


            },
            //ドロワーメニュー----------------------------------------------------------------------


        )
        {
            Box(
                modifier = Modifier.padding(it)
            )
            {
                SideEffect { Log.d("compose-log", "Box3") }
//            NavHost(
//                navController = nestedNavController,
//                startDestination = "main/home",
//                modifier = Modifier,
//            )
//            {
//                screenMode()
//            }

            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(top=topBarHeight,bottom = bottomBarHeight)

        )
        {
            SideEffect { Log.d("compose-log", "Box2") }
            NavHost(
                navController = nestedNavController,
                startDestination = "main/home",
                modifier = Modifier,
            )
            {
                //if(n==true) {
                //デバッグ用
                println("check")

                screenMode()
                //}
            }
            //ドロワーメニューのアイコン----------------------------------------
//                    IconButton(
//                        enabled = !goMap,
//                        modifier = Modifier.padding(start = 30.dp, top = 20.dp, end = 20.dp),
//                        onClick = {
//                            //DismissibleDrawerEnabled=true
//                            drawerState = DrawerState(initialValue = DrawerValue.Open)
//                        }
//                    )
//                    {
//                        if(!goMap)
//                        {
//                            Icon(
//                                imageVector = Icons.Filled.Menu,
//                                contentDescription = null,
//                                tint = Color.White,
//                                modifier = Modifier
//                                    .height(60.dp)
//                                    .width(60.dp)
//                            )
//                        }
//                    }
            //ドロワーメニューのアイコン----------------------------------------
        }
    }




}

fun NavGraphBuilder.mainScreen()
{
    navigation(route = "main", startDestination = "main/entry")
    {
        composable("main/entry")
        {
            MainScreen()
        }
    }
}

//各画面の処理------------------------------------------------
fun NavGraphBuilder.screenMode()
{
    println("eee")
    composable("main/home")
    {
        Home()
    }
    composable("main/camera")
    {
        Camera()
    }
    composable("main/map")
    {
        Map()
    }
}
//各画面の処理------------------------------------------------

///**
// * 多重タップ、複数同時タップを抑止したクリック処理を提供する
// */
//fun Modifier.safeClickable(
//    defaultIntervalMillis: Long = 1000L,
//    enabled: Boolean = true,
//    role: Role? = null,
//    onClickLabel: String? = null,
//    onClick: () -> Unit,
//) = composed(inspectorInfo = NoInspectorInfo) {
//    val clickableInvoker = LocalSafeClickableInvoker.current
//    Modifier.clickable(
//        enabled = enabled,
//        role = role,
//        onClickLabel = onClickLabel,
//        onClick = { clickableInvoker.invoke(defaultIntervalMillis, onClick) }
//    )
//}
//
//// SafeClickableInvokerのインスタンスを保持するCompositionLocalのインスタンス
//val LocalSafeClickableInvoker = compositionLocalOf {
//    SafeClickableInvoker()
//}
//
///**
// * Clickイベントの発火を制御するクラス
// */
//class SafeClickableInvoker {
//    private var lastInvokedMillis: Long = 0L
//
//    operator fun invoke(intervalMillis: Long, call: (() -> Unit)) {
//        val now = System.currentTimeMillis()
//        if (now - lastInvokedMillis > intervalMillis) {
//            call()
//            lastInvokedMillis = now
//        }
//    }
//}

fun buttonClick(
    modifier: Modifier=Modifier,

)
{

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview()
{
    RispinachTheme()
    {
        MainScreen()
    }
}