package com.vishnuraman.imageprocessor

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// crop a picture
class Image(private val bufferedImage: BufferedImage) { // NEVER expose mutable state outside this class
    val width = bufferedImage.width
    val height = bufferedImage.height

    fun getColor(x: Int, y: Int) = Color.fromHex(bufferedImage.getRGB(x, y))

    fun setColor(x: Int, y: Int, color: Color) = bufferedImage.setRGB(x, y, color.toInt())

    fun save(path: String) =
        ImageIO.write(bufferedImage, "JPG", File(path))

    fun draw(g: Graphics) {
        g.drawImage(bufferedImage, 0, 0, null)
    }

    /*
    1. check dimensions - return null if any dimension is invalid
    2. create a black image of width x height
    3. iterate through coords startX ..< startX+w, startY ..< startY+h
        - use buffImage.getRGB to get a pixel from the original image
        - use resultImage.buffImage.setRGB to set a pixel in the result
        - calculate the coordinates
        - return the result image
     */
    fun crop(startX: Int, startY: Int, w: Int, h: Int): Image? {
        // dimensions check
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) return null
        if (w < 0 || startX + w >= width || h < 0 || startY + h >= height) return null

        // happy path
        val result = Image.black(w, h)
        for (x in startX..<(startX + w))
            for (y in startY..<(startY + h)) {
                val originalPixel = bufferedImage.getRGB(x, y)
                result.bufferedImage.setRGB(x - startX, y - startY, originalPixel)
            }

        return result
    }

    /*
       +-------------------------------------------------------+
       |                                                       |
       |           a1 a2 a3 a4 a5                              |
       |           b1 b2 b3 b4 b5                              |
    y >|           c1 c2 XX c4 c5                              |
       |           d1 d2 d3 d4 d5                              |
       |           e1 e2 e3 e4 e5                              |
       |                                                       |
       |                                                       |
       |                                                       |
       |                                                       |
       +-------------------------------------------------------+
                          ^
                          x
       Window(5,5, [a1,a2,a3,a4,a5, b1,b2,b3,b4,b5, c1,c2,XX,c4,c5, d1,d2,d3,d4,d5, e1,e2,e3,e4,e5])
     */
    fun window(xc: Int, yc: Int, width: Int, height: Int): Window {
        val offsetX = (width - 1) / 2
        val offsetY = (height - 1) / 2
        val horizCoords = ((xc - offsetX) .. (xc + offsetX))
            .map { x ->
                if (x <= 0) 0
                else if (x >= this.width) this.width - 1
                else x
            }
        val vertCoords = ((yc - offsetY) .. (yc + offsetY))
            .map { y ->
                if (y <= 0) 0
                else if (y >= this.height) this.height - 1
                else y
            }
        val colors = vertCoords
            .flatMap { y ->
                horizCoords.map { x -> getColor(x,y) }
            }
        return Window(width, height, colors)
    }

    companion object {

        fun black(width: Int, height: Int): Image {
            val buffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val pixels = IntArray(width * height) { 0 }
            buffImage.getRGB(0, 0, width, height, pixels, 0, width)
            return Image(buffImage)
        }

        fun load(path: String): Image =
            Image(ImageIO.read(File(path)))

        fun fromColors(width: Int, height: Int, colors: List<Color>): Image {
            val buffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val pixels = colors.map { it.toInt() }.toIntArray()
            buffImage.setRGB(0,0,width,height,pixels,0,width)
            return Image(buffImage)
        }


    }
}
