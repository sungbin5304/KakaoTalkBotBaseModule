![banner](https://raw.githubusercontent.com/sungbin5304/KakaoTalkBotBaseModule/master/banner.png)
<p align="center">
  <a href="https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE"><img alt="License" src="https://img.shields.io/badge/License-MIT-green"/></a>
  <a href='https://bintray.com/sungbin5304/maven/kakaotalkbotbasemodule/_latestVersion'><img src='https://api.bintray.com/packages/sungbin5304/maven/kakaotalkbotbasemodule/images/download.svg'></a>
  <a href="https://github.com/sungbin5304/KakaoTalkBotBaseModuler"><img alt="Title" src="https://img.shields.io/badge/Module-KakaoTalkBot-ff69b4"/></a>
</p><br>

-----

# What is `KakaoTalkBotBaseModule`?
`KakaoTalkBotBaseModule` is a library designed for beginner Android developers to easily create KakaoTalk Bot applications.

# Download
```gradle
dependencies {
  implementation 'me.sungbin:kakaotalkbotbasemodule:{version}'
}
```

# How to Use?
## 1. Create `KakaoBot()` instance and init with `Context`.
```kotlin
val bot = KakaoBot().init(applicationContext)
```

## 2. Add bot listener [[example]](https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/app/src/main/java/me/sungbin/kakaotalkbotbasemodule/MainActivity.kt#L19)
```kotlin
bot.setBotListener(object : OnKakaoBotListener {
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
        log(sender, message, room, isGroupChat, action, profileImage, packageName)
        // if (sender == "성빈") bot.reply(action, "성공 ㅎㅎ 2222222")
        bot.replyRoom("TEST", "AAAA", { Log.w("AAAA", "없는방!") })
    }
    override fun onBotCreate(bot: KakaoBot) {
        log("bot service create")
    }
    override fun onBotDestroy(bot: KakaoBot) {
        log("bot service destroy")
    }
})
```

### or...you can just add `onMessageReceive` listener with `lambda-function`.
```kotlin
bot.setMessageReceiveListener { sender, message, room, isGroupChat, action, profileImage, packageName, bot ->
  if (sender == "성빈") bot.reply(action, "성공 ㅎㅎ 2222222")
}
```

## 3. **finish!** <br/>
Now, you can start your bot.

-----

# Add `Custom Package`, `Black User` or `Black Room`
```kotlin
.addData(type: Type, value: String)
```

### Type
1. `ROOM`
2. `SENDER`
3. `PACKAGE`

# Permission
`KakaoTalkBot` is require `NotificationListenerService` permission. <br/>
You can give permission with below method.
```kotlin
bot.requestReadNotification()
```

### or...just checking permission accepted.
```kotlin
bot.checkNotificationPermission()
```

# Reply
You can reply something room.
```kotlin
bot.replyRoom("성빈", "안녕 성빈!")
```
### or...you can reply use `Notification.Action`.
```kotlin
bot.reply(action, "성빈은 사람이다.")
```

# Bot `On/Off` control
You can set bot on/off with `.setPower(boolean)` method.

-----

# All methods
```kotlin
init(context: Context)
setBotListener(botListener: OnKakaoBotListener): KakaoBot
setMessageReceiveListener(onMessageReceive: (String, String, String, Boolean, Notification.Action, Bitmap, String) -> Unit): KakaoBot
requestReadNotification(): KakaoBot
addData(type: Type, value: String): KakaoBot
removeData(type: Type, value: String): KakaoBot
clearData()
addKakaoTalkPackage(value: String): KakaoBot
replyRoom(room: String, message: String, roomNotFoundException: Exception.() -> Unit = {}, replyException: Exception.() -> Unit = {})
reply(action: Notification.Action, message: String, exception: Exception.() -> Unit = {})
setPower(power: Boolean): KakaoBot

checkNotificationPermission(): Boolean
```

# TODO
1. [x] Save `Black` data.
2. [x] Save custom package data.

# Tip
**All methods is support `method-chaining`.**

# Happy Coding :)
