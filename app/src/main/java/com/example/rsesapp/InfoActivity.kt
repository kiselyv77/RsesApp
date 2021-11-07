package com.example.rsesapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val info = findViewById<TextView>(R.id.info)
        val desc = findViewById<TextView>(R.id.desc)
        val logo = findViewById<ImageView>(R.id.logo)
        logo.setOnClickListener{
            val anim_update = AnimationUtils.loadAnimation(this, R.anim.alpha)
            logo.startAnimation(anim_update)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            info.setText(Html.fromHtml(
                    "Издание РСЭС<br/>\n" +
                    "Разработчик kiselyv77<br/>\n" +
                    "Версия 1.0<br/>\n" +
                    "<br/>\n" +
                    "Внимание, приложение находится в стадии активной разработки, при возникновении проблем пожалуйста свяжитесь с разработчиками!<br/>\n" +
                    "(разработчиком)<br/>", Html.FROM_HTML_MODE_LEGACY))
            desc.setText(Html.fromHtml(
                    "При потдержке:<br/>\n" +
                            "armPartyRses®<br/>\n" +
                            "bakeRses®", Html.FROM_HTML_MODE_LEGACY))
        }
        else {
            info.setText(Html.fromHtml((this.resources.getString(R.string.info))))
        }

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener{
            val anim_update = AnimationUtils.loadAnimation(this, R.anim.alpha)
            back.startAnimation(anim_update)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }
    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}