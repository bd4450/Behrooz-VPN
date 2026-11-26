
package com.behrooz.vpn

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(15, 25, 45))
        }

        val title = TextView(this).apply {
            text = "BEHROOZ VPN"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "اتصال هوشمند به سرورهای رایگان"
            textSize = 18f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 25, 0, 25)
        }

        val status = TextView(this).apply {
            text = "وضعیت: موتور اتصال هنوز نصب نشده است"
            textSize = 16f
            setTextColor(Color.YELLOW)
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "در حال توسعه"
            isEnabled = false
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)
        layout.addView(button)

        setContentView(layout)
    }
}
