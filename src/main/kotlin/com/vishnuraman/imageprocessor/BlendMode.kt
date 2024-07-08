package com.vishnuraman.imageprocessor


interface BlendMode {
    fun combine(fg: Color, bg: Color): Color
    companion object {
        fun parse(string: String): BlendMode = when(string) {
            "transparency" -> Transparency(0.5)
            "multiply" -> Multiply
            "screen" -> Screen
            else -> NoBlend
        }
    }
}

class Transparency(f: Double) : BlendMode {
    val factor =
        if (f < 0.0) 0.0
        else if (f >= 1.0) 1.0
        else f

    override fun combine(fg: Color, bg: Color): Color =
        Color(
            (fg.red * factor + bg.red * (1 - factor)).toInt(),
            (fg.green * factor + bg.green * (1 - factor)).toInt(),
            (fg.blue * factor + bg.blue * (1 - factor)).toInt()
        )
}

object Multiply: BlendMode {
    override fun combine(fg: Color, bg: Color): Color {
        return Color(
            (fg.red * bg.red / 255.0).toInt(),
            (fg.green * bg.green / 255.0).toInt(),
            (fg.blue * bg.blue / 255.0).toInt(),
        )
    }
}

object Screen: BlendMode {
    override fun combine(fg: Color, bg: Color): Color {
        return Color(
            (255 - (255 - fg.red) * (255 - bg.red) / 255.0).toInt(),
            (255 - (255 - fg.green) * (255 - bg.green) / 255.0).toInt(),
            (255 - (255 - fg.blue) * (255 - bg.blue) / 255.0).toInt(),
        )
    }
}

object NoBlend: BlendMode {
    override fun combine(fg: Color, bg: Color): Color {
        return fg
    }
}
