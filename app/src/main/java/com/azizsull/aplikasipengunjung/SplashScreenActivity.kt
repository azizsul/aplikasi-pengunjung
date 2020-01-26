package com.azizsull.aplikasipengunjung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        object : Thread() {
            override fun run() {
                try {
                    sleep(800)
                    startActivity(Intent(baseContext, MainActivity::class.java))

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.also {
            it.start()
        }
    }
}
