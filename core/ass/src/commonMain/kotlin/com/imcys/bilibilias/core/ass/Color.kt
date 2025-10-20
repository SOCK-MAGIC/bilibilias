package com.imcys.bilibilias.core.ass

@JvmInline
value class Color(val value: Int) {
    override fun toString(): String {
        val r = ((value shr 16) and 0xFF).toString(16).padStart(2, '0').uppercase()
        val g = ((value shr 8) and 0xFF).toString(16).padStart(2, '0').uppercase()
        val b = (value and 0xFF).toString(16).padStart(2, '0').uppercase()
        return "\\c&H$b$g$r"
    }
}