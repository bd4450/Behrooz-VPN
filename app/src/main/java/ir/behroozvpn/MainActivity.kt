
package ir.behroozvpn

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ScrollView

class MainActivity : Activity() {

    private val bg = Color.rgb(10, 16, 13)
    private val panel = Color.rgb(24, 36, 29)
    private val green = Color.rgb(45, 210, 125)
    private val muted = Color.rgb(177, 194, 183)

    private lateinit var status: TextView
    private val prefs by lazy {
        getSharedPreferences("BehroozVPN", MODE_PRIVATE)
    }

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

        val scroll = ScrollView(this)
        scroll.addView(page)

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
                val server = prefs.getString("server", "") ?: ""

                if (server.isEmpty()) {
                    message("ابتدا باید یک سرور معتبر اضافه کنید.")
                } else {
                    message("آدرس سرور ثبت شده است؛ موتور اتصال VPN هنوز باید پیاده‌سازی شود.")
                }
            }
        }

        page.addView(connect)

        status = TextView(this).apply {
            text = "وضعیت: هنوز متصل نیستید"
            textSize = 15f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 25, 0, 25)
        }
        page.addView(status)

        addSection(page, "انتخاب هوشمند سرور") {
            val server = prefs.getString("server", "") ?: ""
            if (server.isEmpty()) {
                message("ابتدا یک سرور اختصاصی اضافه کنید.")
            } else {
                message("سرور ثبت‌شده شما انتخاب شد: $server")
            }
        }

        addSection(page, "سرورهای رایگان") {
            AlertDialog.Builder(this)
                .setTitle("سرورهای رایگان")
                .setMessage("هنوز فهرست سرورهای رایگان به برنامه اضافه نشده است.")
                .setPositiveButton("باشه", null)
                .show()
        }

        addSection(page, "افزودن سرور اختصاصی") {
            addServer()
        }

        addSection(page, "تنظیمات") {
            AlertDialog.Builder(this)
                .setTitle("تنظیمات Behrooz VPN")
                .setMessage(
                    "نام برنامه: Behrooz VPN\n" +
                    "نسخه: آزمایشی\n" +
                    "وضعیت اتصال: قطع"
                )
                .setPositiveButton("باشه", null)
                .show()
        }

        setContentView(scroll)
    }

    private fun addSection(
        parent: LinearLayout,
        name: String,
        action: () -> Unit
    ) {
        val item = TextView(this).apply {
            text = name
            textSize = 17f
            setTextColor(green)
            setPadding(18, 20, 18, 20)
            setBackgroundColor(panel)
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 12

        parent.addView(item, params)
    }

    private fun addServer() {
        val input = EditText(this).apply {
            hint = "آدرس سرور را وارد کنید"
            setSingleLine(true)
        }

        AlertDialog.Builder(this)
            .setTitle("افزودن سرور اختصاصی")
            .setMessage("آدرس سرور معتبر را وارد کنید:")
            .setView(input)
            .setPositiveButton("ذخیره") { _, _ ->
                val server = input.text.toString().trim()

                if (server.isNotEmpty()) {
                    prefs.edit().putString("server", server).apply()
                    message("آدرس سرور ذخیره شد.")
                } else {
                    message("آدرس سرور خالی است.")
                }
            }
            .setNegativeButton("انصراف", null)
            .show()
    }

    private fun message(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
