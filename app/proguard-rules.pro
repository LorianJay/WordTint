-keep public class * extends android.app.Activity

-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

-keep class com.github.lorenj.wordtint.database.entity.** { *; }

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

-keepclassmembers class * extends android.webkit.WebChromeClient {
    public void *(android.webkit.WebView, java.lang.String);
}

-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.jayway.jsonpath.** { *; }
-keep class net.minidev.json.** { *; }
-dontwarn net.minidev.json.**
-dontwarn com.jayway.jsonpath.**

-keep class com.github.lorenj.wordtint.database.vo.** { *; }
