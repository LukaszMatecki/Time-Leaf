package com.lumatech.timeleaf

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitWeekday
import platform.Foundation.NSDate
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = "iOS"
    override val version: String = UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun getCurrentDayOfWeekIndex(): Int {
    val calendar = NSCalendar.currentCalendar
    val date = NSDate()
    val weekday = calendar.component(NSCalendarUnitWeekday, fromDate = date).toInt()
    return when (weekday) {
        2 -> 0 // Mon
        3 -> 1 // Tue
        4 -> 2 // Wed
        5 -> 3 // Thu
        6 -> 4 // Fri
        7 -> 5 // Sat
        1 -> 6 // Sun
        else -> 0
    }
}