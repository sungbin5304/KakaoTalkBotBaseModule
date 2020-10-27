package me.sungbin.kakaotalkbotbasemodule

import android.app.Notification
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.sungbin.kakaotalkbotbasemodule.library.KakaoBot
import me.sungbin.kakaotalkbotbasemodule.library.OnKakaoBotListener
import me.sungbin.kakaotalkbotbasemodule.library.Type

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KakaoBot()
            .addBlack(Type.SENDER, "코콩")
            .requestReadNotification()
            .setBotListener(object : OnKakaoBotListener {
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
                    bot.replyRoom("TEST", "send message", { Log.w("error", "없는방!") })
                }

                override fun onBotCreate(bot: KakaoBot) {
                    log("봇 서비스 시작됨")
                }

                override fun onBotDestroy(bot: KakaoBot) {
                    log("봇 서비스 종료됨")
                }
            })
            .setPower(true)

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            toast("이미 시작됨!!!! \uD83E\uDD2C")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun log(vararg values: Any) {
        Log.w("KakaoTalkBotModule", values.joinToString(", "))
    }
}
