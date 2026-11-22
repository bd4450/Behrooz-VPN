
package ir.behroozvpn

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    private val bg = Color.rgb(10, 16, 13)
    private val panel = Color.rgb(24, 36, 29)
    private val green = Color.rgb(45, 210, 125)
    private val muted = Color.rgb(177, 194, 183)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(bg)
        }

        val title = TextView(this).apply {
            text = "🛡 Behrooz VPN"
            textSize = 28f
            setTextColor(green)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        page.addView(title)

        val subtitle = TextView(this).apply {
            text = "اتصال امن و هوشمند"
            textSize = 18f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 30)
        }
        page.addView(subtitle)

        val connect = Button(this).apply {
            text = "اتصال"
            textSize = 20f
            isAllCaps = false
            setTextColor(Color.BLACK)
            setBackgroundColor(green)
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "برای اتصال باید سرور معتبر اضافه شود.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        page.addView(connect)

        val status = TextView(this).apply {
            text = "وضعیت: هنوز متصل نیستید"
            textSize = 15f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 25, 0, 25)
        }
        page.addView(status)

        addSection(page, "انتخاب هوشمند سرور")
        addSection(page, "سرورهای رایگان")
        addSection(page, "افزودن سرور اختصاصی")
        addSection(page, "تنظیمات")

        setContentView(page)
    }

    private fun addSection(parent: LinearLayout, name: String) {
        val item = TextView(this).apply {
            text = name
            textSize = 17f
            setTextColor(green)
            setPadding(18, 20, 18, 20)
            setBackgroundColor(panel)
            gravity = Gravity.RIGHT
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 12

        parent.addView(item, params)
    }
}
