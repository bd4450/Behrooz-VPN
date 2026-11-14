# Behrooz VPN — starter project

This is the first Android UI/build scaffold for Behrooz VPN.

## Important status

This starter builds an Android APK containing the Persian interface and a file picker.
**It does not establish a VPN tunnel yet.** The Connect button is deliberately disabled.
Do not enter or distribute private keys or configuration files to unknown people.

A real VPN release still needs:
- the official WireGuard tunnel integration;
- a trustworthy VPN server operated by a provider the user has checked;
- secure configuration handling and permission/connection lifecycle;
- testing for DNS/IP leaks and reconnect behavior.

## Build on GitHub

The GitHub Actions workflow builds a debug APK when pushed to `main`, or when run manually
from the Actions tab. The APK is uploaded as an artifact named `Behrooz-VPN-debug-apk`.

Package: `ir.behrooz.vpn`
Minimum Android version: Android 7.0 (API 24)
Target SDK: 35
