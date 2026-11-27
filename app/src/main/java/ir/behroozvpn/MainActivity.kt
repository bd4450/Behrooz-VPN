package ir.behroozvpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import java.io.ByteArrayInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val profiles = mutableListOf<VpnProfile>()
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var backend: GoBackend
    private lateinit var status: TextView
    private lateinit var list: LinearLayout
    private lateinit var smartButton: Button
    private var selected: VpnProfile? = null
    private var activeTunnel: AppTunnel? = null
    private val importCode = 4102
    private val vpnCode = 4103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backend = GoBackend(this)
        buildScreen()
    }

    private fun buildScreen() {
        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF0A100D.toInt())
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        scroll.addView(page)
        setContentView(scroll)

        page.addView(label("بهروز VPN", 30, 0xFF2DD27D.toInt(), true))
        page.addView(label("اتصال امن با پروفایل WireGuard", 15, 0xFFE5EEE8.toInt()))
        status = label("وضعیت: آماده — هنوز سروری اضافه نشده", 16, 0xFFFFD166.toInt())
        status.setPadding(0, 24, 0, 20)
        page.addView(status)

        val import = button("＋ افزودن فایل سرور (.conf)") { chooseConfig() }
        page.addView(import)
        smartButton = button("انتخاب هوشمند بهترین سرور") { smartSelect() }
        page.addView(smartButton)
        page.addView(button("تست سرورهای اضافه‌شده") { testAll() })
        page.addView(button("اتصال به سرور انتخاب‌شده") { connectSelected() })
        page.addView(button("قطع اتصال") { disconnect() })

        page.addView(label("سرورهای من", 20, 0xFF2DD27D.toInt(), true))
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(list)
        page.addView(label("راهنما: فایل معتبر WireGuard با پسوند .conf را وارد کنید. سرور رایگان ناشناس الزاماً امن نیست.", 13, 0xFFB8C7BE.toInt()))
        refreshList()
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            this.text = text
            textSize = size.toFloat()
            setTextColor(color)
            gravity = Gravity.CENTER
            if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(8, 10, 8, 10)
        }

    private fun button(text: String, action: () -> Unit) =
        Button(this).apply {
            this.text = text
            isAllCaps = false
            setTextColor(0xFF07110B.toInt())
            setBackgroundColor(0xFF2DD27D.toInt())
            setOnClickListener { action() }
            val p = LinearLayout.LayoutParams(-1, -2)
            p.setMargins(0, 7, 0, 7)
            layoutParams = p
        }

    private fun chooseConfig() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, importCode)
    }

    @Deprecated("Legacy activity result used for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == importCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            try {
                val text = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: return
                val endpoint = Regex("(?m)^\\s*Endpoint\\s*=\\s*([^:#\\s]+):(\\d+)").find(text)
                    ?: throw IllegalArgumentException("خط Endpoint در فایل پیدا نشد")
                val name = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "سرور ${profiles.size + 1}"
                profiles.add(VpnProfile(name, text, endpoint.groupValues[1], endpoint.groupValues[2].toInt()))
                selected = profiles.last()
                status.text = "وضعیت: پروفایل اضافه شد — ${selected!!.name}"
                refreshList()
            } catch (e: Exception) {
                status.text = "خطا در خواندن فایل: ${e.message}"
            }
        }
        if (requestCode == vpnCode && resultCode == RESULT_OK) startTunnel()
    }

    private fun refreshList() {
        list.removeAllViews()
        if (profiles.isEmpty()) list.addView(label("هنوز پروفایلی وارد نشده است", 14, 0xFFB8C7BE.toInt()))
        profiles.forEach { p ->
            val row = Button(this).apply {
                text = (if (p == selected) "✓ " else "") + p.name + (p.latencyMs?.let { " — ${it}ms" } ?: "")
                isAllCaps = false
                setOnClickListener { selected = p; refreshList(); status.text = "انتخاب شد: ${p.name}" }
            }
            list.addView(row)
        }
    }

    private fun testAll() {
        if (profiles.isEmpty()) { status.text = "ابتدا فایل سرور وارد کنید"; return }
        status.text = "در حال آزمایش سرورها..."
        executor.execute {
            profiles.forEach { p ->
                p.latencyMs = try {
                    val start = System.currentTimeMillis()
                    Socket().use { it.connect(InetSocketAddress(p.endpointHost, p.endpointPort), 2500) }
                    System.currentTimeMillis() - start
                } catch (_: Exception) { Long.MAX_VALUE }
            }
            runOnUiThread {
                refreshList()
                status.text = "آزمایش تمام شد؛ کمترین تأخیرِ قابل‌دسترسی معیار انتخاب است."
            }
        }
    }

    private fun smartSelect() {
        if (profiles.isEmpty()) { status.text = "برای انتخاب هوشمند، ابتدا پروفایل سرور وارد کنید."; return }
        smartButton.isEnabled = false
        status.text = "انتخاب هوشمند: در حال سنجش پینگ سرورها..."
        executor.execute {
            profiles.forEach { p ->
                p.latencyMs = try {
                    val t = System.currentTimeMillis()
                    Socket().use { it.connect(InetSocketAddress(p.endpointHost, p.endpointPort), 2500) }
                    System.currentTimeMillis() - t
                } catch (_: Exception) { Long.MAX_VALUE }
            }
            val best = profiles.filter { it.latencyMs != Long.MAX_VALUE }.minByOrNull { it.latencyMs ?: Long.MAX_VALUE }
            runOnUiThread {
                selected = best
                smartButton.isEnabled = true
                refreshList()
                status.text = if (best == null) "هیچ endpoint قابل‌دسترسی نبود؛ شبکه یا سرورها را بررسی کنید."
                    else "بهترین گزینه فعلی: ${best.name} با پینگ ${best.latencyMs}ms"
            }
        }
    }

    private fun connectSelected() {
        val p = selected
        if (p == null) { status.text = "ابتدا یک پروفایل وارد و انتخاب کنید"; return }
        val permission = VpnService.prepare(this)
        if (permission != null) startActivityForResult(permission, vpnCode) else startTunnel()
    }

    private fun startTunnel() {
        val p = selected ?: return
        try {
            val config = Config.parse(ByteArrayInputStream(p.configText.toByteArray(Charsets.UTF_8)))
            val tunnel = AppTunnel(p.name)
            backend.setState(tunnel, Tunnel.State.UP, config)
            activeTunnel = tunnel
            status.text = "وضعیت: درخواست اتصال به ${p.name} ارسال شد"
        } catch (e: Exception) {
            status.text = "اتصال ناموفق: ${e.message}"
        }
    }

    private fun disconnect() {
        try {
            activeTunnel?.let { backend.setState(it, Tunnel.State.DOWN, null) }
            activeTunnel = null
            status.text = "وضعیت: اتصال قطع شد"
        } catch (e: Exception) {
            status.text = "خطا هنگام قطع اتصال: ${e.message}"
        }
    }

    private class AppTunnel(private val tunnelName: String) : Tunnel {
        override fun getName(): String = tunnelName.replace(Regex("[^A-Za-z0-9_=+.-]"), "_").take(15).ifBlank { "BehroozVPN" }
        override fun onStateChange(newState: Tunnel.State) {}
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
