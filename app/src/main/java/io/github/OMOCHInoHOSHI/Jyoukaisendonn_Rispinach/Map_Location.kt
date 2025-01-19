package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// Androidアプリで位置情報を取得するためのクラスS----------------------------------------------------
class LocationViewModel(private val context: Context) : ViewModel() {

    // 位置情報が更新されたらこのLiveDataに格納する
    private val _location: MutableLiveData<Location> = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    // 位置情報へのアクセス権限を要求する関数S---------------------------------
    fun requestLocationPermission(activity: Activity) {
        val LOCATION_PERMISSION_REQUEST_CODE = 1001

        // 位置情報の権限があるか確認する
        val isAccept = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isAccept) {
            // 権限が許可されていない場合はリクエストする
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    // 位置情報へのアクセス権限を要求する関数E---------------------------------

//    // FusedLocationProviderClientを使ってデバイスの最後の既知の位置を取得S---------------
//    fun fusedLocation():String {
//
//        // 緯度経度
//        var locationString = ""
//
//        // 最後に確認された位置情報を取得
//        val fusedLocationClient: FusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(context)
//
//        // 一応権限のチェック
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("LocationSensor","権限がない")
//            // 権限もらえないと困っちゃうなぁ
//            return null.toString()
//        }
//
//        // 位置情報を取得したらListenerが反応する
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location ->
////                Log.d("LocationSensor", "$location")
//
//                if (location != null) {
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    // 緯度経度を文字列として結合
//                    locationString = "$latitude, $longitude"
//
//                    Log.d("LocationSensor", "緯度経度: $locationString")
//
//                    _location.postValue(location)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("LocationSensor", "Error getting location", exception)
//            }
//
//        return locationString
//    }
//    // FusedLocationProviderClientを使ってデバイスの最後の既知の位置を取得E---------------

    // FusedLocationProviderClientを使ってデバイスの最後の既知の位置を取得
    fun fusedLocation() {
        // FusedLocationProviderClientを取得
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // 権限のチェック
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationSensor", "権限がない")
            return
        }

        // 位置情報を取得
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    _location.postValue(location) // LiveDataに結果を格納
                } else {
                    Log.d("LocationSensor", "位置情報が取得できませんでした")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationSensor", "位置情報取得中にエラー", exception)
            }
    }
}
// Androidアプリで位置情報を取得するためのクラスE----------------------------------------------------