package com.vishnuraman.imageprocessor

import java.awt.Dimension
import java.awt.Graphics
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.system.exitProcess

// Java Swing
object App {
    private lateinit var frame: JFrame
    private lateinit var imagePanel: ImagePanel

    class ImagePanel(private var image: Image) : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            // render the picture inside this "graphics"
           image.draw(g)
        }

        // TODO
        override fun getPreferredSize(): Dimension =
            Dimension(image.width, image.height)


// should not be allowed.
//        private fun beautify() {
//            // should not be allowed
//            image.bufferedImage.setRGB(10,10,20,20, IntArray(400) {0}, 0,20)
//        }

        fun replaceImage(newImage: Image) {
            image = newImage
            // by mistake
            revalidate()
            repaint()
        }

        fun getImage(): Image = image
    }

    fun loadResource(path: String) {
        val image = Image.load(path)
        if (!this::frame.isInitialized) {
            frame = JFrame("Kotlin Rocks Image App")
            imagePanel = ImagePanel(image)
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.contentPane.add(imagePanel)
            frame.pack()
            frame.isVisible = true
        } else {
            imagePanel.replaceImage(image)
            frame.pack() // resizes the window to the "preferred dimensions"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        while (true) {
            println("> ")
            val command  = scanner.nextLine()
            val words = command.split(" ")
            val action = words[0]
            when (action) {
                "load" -> try {
                    loadResource(words[1])
                } catch (e: Exception) {
                    println("Error: cannot loaded image at path ${words[1]}")
                }
                "save" ->
                    if (!this::frame.isInitialized)
                        println("No image loaded. ")
                    else
                        imagePanel.getImage().save(words[1])
                "size" ->
                    if (!this::frame.isInitialized)
                        println("No image loaded.")
                    else
                        println("${imagePanel.getImage().width} x ${imagePanel.getImage().height}")
                "exit" -> exitProcess(0)
                else -> {
                    val transformation = Transformation.parse(command)
                    val newImage = transformation(imagePanel.getImage())
                    imagePanel.replaceImage(newImage)
                    frame.pack()
                }
            }
        }
    }
}