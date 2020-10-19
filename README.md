![banner](https://raw.githubusercontent.com/sungbin5304/KakaoTalkBotBaseModule/master/banner.png)
<p align="center">
  <a href="https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE"><img alt="License" src="https://img.shields.io/badge/License-MIT-blue"/></a>
  <a href="https://jitpack.io/#sungbin5304/KakaoTalkBotBaseModule"><img alt="Download" src="https://jitpack.io/v/sungbin5304/KakaoTalkBotBaseModule.svg"/></a>
  <a href="https://github.com/sungbin5304/KakaoTalkBotBaseModuler"><img alt="Title" src="https://img.shields.io/badge/Module-KakaoTalkBot-ff69b4"/></a>
</p><br>

-----

# What is `KakaoTalkBotBaseModule`?
`KakaoTalkBotBaseModule` is a library designed for beginner Android developers to easily create KakaoTalk bot applications.

# Download
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.sungbin5304:KakaoTalkBotBaseModule:Tag'
}
```

# How to Use?
## 1. Create `KakaoBot()` instance.
```kotlin
val bot = KakaoBot()
```
## 2. Add bot listener (just follow below example code)
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
        log("bot service destory")
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

# Black `room` or `sender`
### method
```kotlin
bot.addBlack(type: Type, value: String)
```

### Type
1. `ROOM`
2. `SENDER`

### Example
```kotlin
bot.addBlack(Type.SENDER, "코콩")
```

# Permission
`KakaoTalkBot` is require `Notification Listener Service Permission`. <br/>
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
-----

# all methods
```kotlin
setBotListener(botListener: OnKakaoBotListener): KakaoBot
setMessageReceiveListener(onMessageReceive: (String, String, String, Boolean, Notification.Action, Bitmap, String) -> Unit): KakaoBot
requestReadNotification(): KakaoBot
removeBlack(type: Type, value: String): KakaoBot
addKakaoTalkPackage(value: String): KakaoBot
replyRoom(room: String, message: String, roomNotFoundException: (Exception) -> Unit = {}, replyException: (Exception) -> Unit = {})
reply(action: Notification.Action, message: String, exception: (Exception) -> Unit = {})

checkNotificationPermission(): Boolean
```

# Tip
**all methods is support `method-chaining`.**

# Happy Coding :)
