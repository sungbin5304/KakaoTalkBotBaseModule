package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Notification
import android.app.RemoteInput
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.Spanned
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.text.HtmlCompat
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.actions
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.blackRoom
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.blackSender
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.botListener
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.context
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.kakaoTalkList
import java.util.*


class KakaoBot : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        botListener?.onBotCreate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        botListener?.onBotDestroy(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (kakaoTalkList.contains(sbn.packageName)) {
            val wExt = Notification.WearableExtender(sbn.notification)
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

                        if (!actions.containsKey(room)) actions[room!!] = action
                        if (!blackRoom.contains(room) && !blackSender.contains(sender)) {
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
    }

    fun setBotListener(botListener: OnKakaoBotListener): KakaoBot {
        KakaoBotModule.botListener = botListener
        return this
    }

    fun setMessageReceiveListener(
        onMessageReceive: (String, String, String, Boolean, Notification.Action, Bitmap, String) -> Unit
    ): KakaoBot {
        botListener = object : OnKakaoBotListener {
            override fun onMessageReceive(
                sender: String,
                message: String,
                room: String,
                isGroupChat: Boolean,
                action: Notification.Action,
                profileImage: Bitmap,
                packageName: String,
                bot: KakaoBot
            ) {
                onMessageReceive(
                    sender,
                    message,
                    room,
                    isGroupChat,
                    action,
                    profileImage,
                    packageName
                )
            }

            override fun onBotCreate(bot: KakaoBot) {}
            override fun onBotDestroy(bot: KakaoBot) {}
        }
        return this
    }

    fun requestReadNotification(): KakaoBot {
        if (!checkNotificationPermission()) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        return this
    }

    fun checkNotificationPermission() =
        NotificationManagerCompat.getEnabledListenerPackages(
            context
        ).contains(context.packageName)


    // todo: 데이터 저장되게 하기
    fun addBlack(type: Type, value: String) {
        when (type) {
            Type.ROOM -> blackRoom.add(value)
            Type.SENDER -> blackSender.add(value)
        }
    }

    fun removeBlack(type: Type, value: String) {
        when (type) {
            Type.ROOM -> blackRoom.remove(value)
            Type.SENDER -> blackSender.remove(value)
        }
    }

    fun addKakaoTalkPackage(value: String) {
        kakaoTalkList.add(value)
    }

    private fun chatHook(
        sender: String,
        message: String,
        room: String,
        isGroupChat: Boolean,
        action: Notification.Action,
        profileImage: Bitmap,
        packageName: String
    ) {
        botListener?.onMessageReceive(
            sender,
            message,
            room,
            isGroupChat,
            action,
            profileImage,
            packageName,
            this
        )
    }

    fun replyRoom(
        room: String,
        message: String,
        roomNotFoundException: (Exception) -> Unit = {},
        replyException: (Exception) -> Unit = {}
    ) {
        try {
            reply(actions[room]!!, message, replyException)
        } catch (e: Exception) {
            roomNotFoundException(e)
        }
    }

    fun reply(action: Notification.Action, message: String, exception: (Exception) -> Unit = {}) {
        try {
            val sendIntent = Intent()
            val msg = Bundle()
            for (inputable in action.remoteInputs) msg.putCharSequence(
                inputable.resultKey,
                message
            )
            RemoteInput.addResultsToIntent(action.remoteInputs, sendIntent, msg)
            action.actionIntent.send(context, 0, sendIntent)
        } catch (e: Exception) {
            exception(e)
        }
    }

    private fun Icon.toBitmap() = (this.loadDrawable(context) as BitmapDrawable).bitmap
}