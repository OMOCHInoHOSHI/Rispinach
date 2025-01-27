import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach.ImageData

// Firebase Storage から画像を取得する関数
fun fetchImagesFromFirebaseStorage(onDataReceived: (List<ImageData>) -> Unit) {
    Log.d("FirebaseStorage", "FirebaseStorage_Start")
    // Firebase Storage のインスタンスを取得
    val storage = FirebaseStorage.getInstance()
    // "images" フォルダの参照を取得
    val storageRef = storage.reference.child("images")
    // 画像データを格納するリストを作成
    val imageList = mutableListOf<ImageData>()

//    /* ここの"//"を消すとFirebaseから読み取らなくなる

    // "images" フォルダ内の全てのアイテムをリストアップ
    storageRef.listAll().addOnSuccessListener { listResult ->
        // 各アイテムに対して処理を行う
        listResult.items.forEachIndexed { index, item ->
            // アイテムのメタデータを取得
            item.metadata.addOnSuccessListener { metadata ->
                // メタデータからtitle, speciesName, location, discoveryDate を取得（存在しない場合は "不明" とする）
                val title = metadata.getCustomMetadata("title") ?: "無題"     // タイトル
                val speciesName = metadata.getCustomMetadata("speciesName") ?: "不明"     // 生物名
                val location = metadata.getCustomMetadata("location") ?: "不明"       // 住所
                val discoveryDate = metadata.getCustomMetadata("discoveryDate") ?: "不明"     // 日付
//                val Dkey = metadata.getCustomMetadata("R_T_D_Key") ?: "不明"
                //Log.d("FirebaseStorage", "$speciesName, $Dkey")       // DKeyの確認用
                val latitudeStr = metadata.getCustomMetadata("latitude") ?: "不明"       // 緯度
                val longitudeStr = metadata.getCustomMetadata("longitude") ?: "不明"      // 経度

                var latitude: Double? = null
                var longitude: Double? = null

                // 緯度、経度を
                if (latitudeStr != "不明" && longitudeStr != "不明"){
                    latitude = latitudeStr.toDoubleOrNull()
                    longitude = longitudeStr.toDoubleOrNull()
                }

                //Log.d("FirebaseStorage", "$speciesName, $latitude   |   $longitude")       // DKeyの確認用

                // アイテムのバイトデータを取得
                item.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    // バイトデータを Bitmap に変換
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    // ImageData オブジェクトをリストに追加
                    imageList.add(ImageData(bitmap, title, speciesName, location, discoveryDate, index + 1, latitude, longitude))
                    // 全てのアイテムの処理が完了したら、ソートして、コールバックを呼び出す
                    if (imageList.size == listResult.items.size) {
                        Log.d("FirebaseStorage", "FirebaseStorage_End")

                        val sortedImageList = imageList.sortedByDescending  { it.id }       // 降順にソート
                        // val sortedImageList = imageList.sortedBy { it.id }       // 昇順にソート

                        onDataReceived(sortedImageList)
                    }
                }.addOnFailureListener { exception ->
                    // 画像のダウンロードに失敗した場合のエラーログ
                    Log.e("FirebaseStorage", "Failed to download image", exception)
                }
            }.addOnFailureListener { exception ->
                // メタデータの取得に失敗した場合のエラーログ
                Log.e("FirebaseStorage", "Failed to get metadata", exception)
            }
        }
    }.addOnFailureListener { exception ->
        // 画像リストの取得に失敗した場合のエラーログ
        Log.e("FirebaseStorage", "Failed to list images", exception)
    }
//    ここの"//"を消すとFirebaseから読み取らなくなる  */

}