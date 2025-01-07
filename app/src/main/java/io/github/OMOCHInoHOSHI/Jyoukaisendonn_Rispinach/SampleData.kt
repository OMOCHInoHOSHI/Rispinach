// SampleData.kt
package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

/**
 * メッセージデータクラス
 */
data class Message(val author: String, val body: String)

/**
 * サンプルデータを提供するオブジェクト
 */
object SampleData {
    val conversationSample = listOf(
        Message("Lexi", "テスト...テスト...テスト..."),
        Message(
            "Lexi",
            """Androidのバージョン一覧:
                |Android KitKat (API 19)
                |Android Lollipop (API 21)
                |Android Marshmallow (API 23)
                |Android Nougat (API 24)
                |Android Oreo (API 26)
                |Android Pie (API 28)
                |Android 10 (API 29)
                |Android 11 (API 30)
                |Android 12 (API 31)""".trimMargin()
        ),
        Message(
            "Lexi",
            """Kotlinは私のお気に入りのプログラミング言語だと思います。
                |とても楽しいです！""".trimMargin()
        ),
        Message(
            "Lexi",
            "XMLレイアウトの代替手段を探しています..."
        ),
        Message(
            "Lexi",
            """Jetpack Composeを見てみてください。素晴らしいですよ！
                |AndroidのネイティブUIを構築するための最新ツールキットです。
                |UI開発を簡素化し、加速します。
                |コードが少なく、強力なツール、直感的なKotlin API :)""".trimMargin()
        ),
        Message(
            "Lexi",
            "ComposeはAPI 21以上で利用可能です :)"
        ),
        Message(
            "Lexi",
            "UI用にKotlinを書くのはとても自然に感じます。Compose、あなたは今までどこにいたの？"
        ),
        Message(
            "Lexi",
            "Android Studioの次のバージョンの名前はArctic Foxです"
        ),
        Message(
            "Lexi",
            "Android Studio Arctic FoxのComposeツールはトップクラスです ^_^"
        ),
        Message(
            "Lexi",
            "Android Studioから直接エミュレーターを実行できることを知りませんでした"
        ),
        Message(
            "Lexi",
            "Composeのプレビューは、コンポーザブルレイアウトがどのように見えるかを迅速に確認するのに最適です"
        ),
        Message(
            "Lexi",
            "プレビューは実験的な設定を有効にするとインタラクティブにもなります"
        ),
        Message(
            "Lexi",
            "build.gradleをKTSで書いてみましたか？"
        )
    )
}