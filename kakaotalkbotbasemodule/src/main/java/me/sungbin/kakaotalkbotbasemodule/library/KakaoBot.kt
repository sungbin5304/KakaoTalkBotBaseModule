package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.Spanned
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.text.HtmlCompat
import java.util.*


class KakaoBot : NotificationListenerService() {

    private lateinit var context: Context
    private var listener: OnMessageReceive? = null

    interface OnMessageReceive {
        fun onMessageReceive(sender: String,
                             message: String,
                             room: String,
                             isGroupChat: Boolean,
                             session: Notification.Action,
                             profileImage: Bitmap,
                             packageName: String)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        // todo
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        // todo: var packages = DataUtils.readData(ctx!!, "packages", "com.kakao.talk").trim()
        var packages = ""
        if (packages.isBlank()) packages = "com.kakao.talk"
        if (packages.split("\n").contains(sbn.packageName)) {
            val wExt =
                    Notification.WearableExtender(sbn.notification)
            for (action in wExt.actions) {
                if (action.remoteInputs != null && action.remoteInputs.isNotEmpty()) {
                    if (action.title.toString().toLowerCase(Locale.getDefault()).contains("reply")
                            || action.title.toString()
                                    .toLowerCase(Locale.getDefault()).contains("답장")
                    ) {
                        val extras = sbn.notification.extras
                        var room: String?
                        var sender: String?
                        var message: String?
                        var isGroupChat = false
                        val packageName = sbn.packageName

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            room = extras.getString("android.summaryText")
                            sender = extras.get("android.title")?.toString()
                            message = extras.get("android.text")?.toString()
                            if (room == null) {
                                room = sender
                                isGroupChat = false
                            } else isGroupChat = true
                        } else {
                            var kakaotalkVersion = 0L
                            var noKakaoTalk = false
                            try {
                                kakaotalkVersion =
                                        PackageInfoCompat.getLongVersionCode(
                                                packageManager.getPackageInfo("com.kakao.talk", 0)
                                        )
                            } catch (ignored: Exception) {
                                noKakaoTalk = true
                            }

                            if (noKakaoTalk || packageName != "com.kakao.talk"
                                    || kakaotalkVersion < 1907310
                            ) {
                                room = extras.getString("android.title")
                                if (extras.get("android.text") !is String) {
                                    val html = HtmlCompat.toHtml(
                                            extras.get("android.text")
                                                    as Spanned,
                                            HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
                                    )
                                    sender = HtmlCompat.fromHtml(
                                            html.split("<b>")[1].split("</b>")[0],
                                            HtmlCompat.FROM_HTML_MODE_COMPACT
                                    ).toString()
                                    message = HtmlCompat.fromHtml(
                                            html.split("</b>")[1].split("</p>")[0]
                                                    .substring(1), HtmlCompat.FROM_HTML_MODE_COMPACT
                                    ).toString()
                                } else {
                                    sender = room
                                    message = extras.get("android.text")?.toString()
                                }
                            } else {
                                room = extras.getString("android.subText")
                                sender = extras.getString("android.title")
                                message = extras.getString("android.text")
                                isGroupChat = room != null
                                if (room == null) room = sender
                            }
                        }

                        // todo
                        /*if (!sessions.containsKey(room)) sessions[room] = action
                        val blackRoom = DataUtils.readData(
                            applicationContext, "RoomBlackList", ""
                        )
                        val blackSender = DataUtils.readData(
                            applicationContext, "SenderBlackList", ""
                        )
                        if (!blackRoom.contains(room!!) ||
                            !blackSender.contains(sender!!)
                        ) {
                            chatHook(
                                sender!!, msg!!, room, isGroupChat, action,
                                ((sbn.notification.getLargeIcon())
                                    .loadDrawable(ctx) as BitmapDrawable).bitmap,
                                sbn.packageName,
                                ctx!!
                            )
                        }*/

                        chatHook(
                                sender!!, message!!, room!!, isGroupChat, action,
                                sbn.notification.getLargeIcon().toBitmap(),
                                sbn.packageName
                        )
                    }
                }
            }
        }
    }

    fun setOnMessageReceiveListener(listener: OnMessageReceive?): KakaoBot {
        this.listener = listener
        return this
    }

    fun setOnMessageReceiveListener(listener: (String, String, String, Boolean, Notification.Action, Bitmap, String) -> Unit): KakaoBot {
        this.listener = object : OnMessageReceive {
            override fun onMessageReceive(sender: String,
                                          message: String,
                                          room: String,
                                          isGroupChat: Boolean,
                                          session: Notification.Action,
                                          profileImage: Bitmap,
                                          packageName: String) {
                listener(sender, message, room, isGroupChat, session, profileImage, packageName)
            }
        }
        return this
    }


    private fun chatHook(
            sender: String,
            message: String,
            room: String,
            isGroupChat: Boolean,
            session: Notification.Action,
            profileImage: Bitmap,
            packageName: String
    ) {

    }

    private fun Icon.toBitmap(): Bitmap = (this.loadDrawable(context) as BitmapDrawable).bitmap
}