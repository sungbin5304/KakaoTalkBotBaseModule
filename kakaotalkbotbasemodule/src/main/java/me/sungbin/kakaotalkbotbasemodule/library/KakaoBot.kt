/*
 * Copyright (c) 2021. Sungbin Ji. All rights reserved.
 *
 * KakaoTalkBotBaseModule license is under the MIT license.
 * SEE LICENSE: https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE
 */

package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Notification
import android.app.RemoteInput
import android.content.Context
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
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.botListener
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBotModule.Companion.power
import java.util.Locale

class KakaoBot : NotificationListenerService() {

    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        botListener?.onBotCreate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        botListener?.onBotDestroy(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (!power) return
        if (DataUtil.read(context, Type.PACKAGE).contains(sbn.packageName)) {
            val wExt = Notification.WearableExtender(sbn.notification)
            for (action in wExt.actions) {
                if (action.remoteInputs != null && action.remoteInputs.isNotEmpty()) {
                    if (action.title.toString().toLowerCase(Locale.getDefault())
                        .contains("reply") ||
                        action.title.toString()
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

                            if (noKakaoTalk || packageName != "com.kakao.talk" ||
                                kakaotalkVersion < 1907310
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
                                            .substring(1),
                                        HtmlCompat.FROM_HTML_MODE_COMPACT
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
                        if (!DataUtil.read(context, Type.ROOM).contains(room.toString()) &&
                            !DataUtil.read(context, Type.SENDER).contains(sender.toString())
                        ) {
                            chatHook(
                                sender!!, message!!.trim(), room!!, isGroupChat, action,
                                sbn.notification.getLargeIcon().toBitmap(),
                                sbn.packageName
                            )
                        }
                    }
                }
            }
        }
    }

    fun init(context: Context): KakaoBot {
        this.context = context
        DataUtil.save(context, Type.PACKAGE, "com.kakao.talk")
        return this
    }

    fun setPower(power: Boolean): KakaoBot {
        KakaoBotModule.power = power
        return this
    }

    fun setBotListener(botListener: OnKakaoBotListener): KakaoBot {
        KakaoBotModule.botListener = botListener
        return this
    }

    fun setMessageReceiveListener(
        onMessageReceive: (String, String, String, Boolean, Notification.Action, Bitmap, String, KakaoBot) -> Unit,
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
                bot: KakaoBot,
            ) {
                onMessageReceive(
                    sender,
                    message,
                    room,
                    isGroupChat,
                    action,
                    profileImage,
                    packageName,
                    bot
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

    fun addData(type: Type, value: String): KakaoBot {
        DataUtil.save(context, type, value)
        return this
    }

    fun removeData(type: Type, value: String): KakaoBot {
        DataUtil.remove(context, type, value)
        return this
    }

    fun clearData(): KakaoBot {
        DataUtil.clear(context)
        return this
    }

    private fun chatHook(
        sender: String,
        message: String,
        room: String,
        isGroupChat: Boolean,
        action: Notification.Action,
        profileImage: Bitmap,
        packageName: String,
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
        roomNotFoundException: Exception.() -> Unit = {},
        replyException: Exception.() -> Unit = {},
    ) {
        try {
            reply(actions[room]!!, message, replyException)
        } catch (exception: Exception) {
            roomNotFoundException(exception)
        }
    }

    fun reply(action: Notification.Action, message: String, exception: Exception.() -> Unit = {}) {
        try {
            val sendIntent = Intent()
            val msg = Bundle()
            for (inputable in action.remoteInputs) msg.putCharSequence(
                inputable.resultKey,
                message
            )
            RemoteInput.addResultsToIntent(action.remoteInputs, sendIntent, msg)
            action.actionIntent.send(context, 0, sendIntent)
        } catch (exception: Exception) {
            exception(exception)
        }
    }

    private fun Icon.toBitmap() = (this.loadDrawable(context) as BitmapDrawable).bitmap
}
