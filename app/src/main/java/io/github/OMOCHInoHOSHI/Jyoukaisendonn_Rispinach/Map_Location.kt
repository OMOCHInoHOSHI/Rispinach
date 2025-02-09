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
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


// Androidアプリで位置情報を取得するためのクラスS----------------------------------------------------
class LocationViewModel(private val context: Context) : ViewModel() {

    // 位置情報が更新されたらこのLiveDataに格納する
    private val _location: MutableLiveData<Location> = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    var location_string_latitude: Double? = null
    var location_string_longitude: Double? = null

    // 位置情報へのアクセス権限を要求する関数
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

        // 位置情報を取得// 位置情報を取得したらListenerが反応する
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    _location.postValue(location) // LiveDataに結果を格納
                    // 緯度経度を文字列で結合し、ログに出力
                    val locationString = "${location.latitude}, ${location.longitude}"
                    location_string_latitude = location.latitude
                    location_string_longitude = location.longitude

                    Log.d("LocationSensor", "位置情報取得成功: $locationString")
                    println("location_string_latitude = $location_string_latitude")
                    println("location_string_longitude = $location_string_longitude")
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

// LocationViewModelを生成するFactoryクラスS-----------------------------------------------------
class LocationViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
// LocationViewModelを生成するFactoryクラスS-----------------------------------------------------
