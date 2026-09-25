package com.lumatech.timeleaf

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = "iOS"
    override val version: String = UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()