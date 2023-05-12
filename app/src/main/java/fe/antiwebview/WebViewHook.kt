package fe.antiwebview

import android.Manifest
import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.XModuleResources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.IconCompat
import androidx.webkit.WebViewCompat
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.ByteArrayOutputStream


class WebViewHook : IXposedHookZygoteInit, IXposedHookLoadPackage {
    private lateinit var modulePath: String

    companion object {
        const val AntiWebViewNotificationChannelId = "ANTIWEBVIEW_NOTIFICATION_CHANNEL"
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val context = AndroidAppHelper.currentApplication()
        val webViewPackage = WebViewCompat.getCurrentWebViewPackage(context)

        if (lpparam?.packageName != webViewPackage?.packageName) return
        if (lpparam?.classLoader == null) return

        val moduleRes = XModuleResources.createInstance(modulePath, null)

        Log.d("PreCreateNotificationChannel", "Pre")
        this.createNotificationChannel(context, moduleRes)
        Log.d("PreCreateNotificationChannel", "Post")

        val webViewClass = XposedHelpers.findClass("android.webkit.WebView", lpparam.classLoader)

        XposedBridge.hookAllMethods(webViewClass, "loadUrl", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val url = param?.args?.get(0)?.toString()
                if (url != null) {
                    Handler(Looper.getMainLooper()).post {
                        makeNotification(context, moduleRes, url)
                    }
                }
            }
        })
    }

    private fun createNotificationChannel(context: Context, moduleRes: XModuleResources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = moduleRes.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(AntiWebViewNotificationChannelId, name, importance).apply {
                    description = moduleRes.getString(R.string.channel_description)
                }

            val notificationManager = getSystemService(context, NotificationManager::class.java)
            Log.d("CreateNotificationChannel", "$notificationManager")
            notificationManager?.createNotificationChannel(
                channel
            )
        }
    }

    private fun makeNotification(context: Context, moduleRes: XModuleResources, url: String) {
        Log.d("MakeNotification", "Make notification $url")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MakeNotification", "No Permission")
            return
        }

        Log.d("MakeNotification", "Permission is granted")

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        Log.d("MakeNotification", "Intent built $pendingIntent")

        val drawable = moduleRes.getDrawable(R.drawable.ic_notification_icon, null)
        val bitmapIcon = convertToBitmap(drawable)

        Log.d("MakeNotification", "Drawable icon loaded $bitmapIcon")

        val builder = NotificationCompat.Builder(context, AntiWebViewNotificationChannelId)
            .setSmallIcon(IconCompat.createWithBitmap(bitmapIcon))
            .setContentTitle(moduleRes.getString(R.string.notification_title))
            .setContentText(moduleRes.getString(R.string.notification_content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Log.d("MakeNotification", "Notification built $builder")

        NotificationManagerCompat.from(context).notify(1, builder.build())
        Log.d("MakeNotification", "Notification posted")
    }

    private fun convertToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
