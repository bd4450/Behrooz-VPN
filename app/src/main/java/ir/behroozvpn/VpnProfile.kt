package ir.behroozvpn

data class VpnProfile(
    val name: String,
    val configText: String,
    val endpointHost: String,
    val endpointPort: Int,
    var latencyMs: Long? = null
)
