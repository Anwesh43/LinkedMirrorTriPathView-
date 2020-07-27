package com.anwesh.uiprojects.mirrortripathview

/**
 * Created by anweshmishra on 28/07/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val colors : Array<String> = arrayOf("#3F51B5", "#03A9F4", "#F44336", "#FFEB3B", "#4CAF50")
val parts : Int = 3
val divs : Int = 2
val scGap : Float = 0.02f / (parts * divs)
val strokeFactor : Int = 90
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawTriPath(i : Int, scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sfi : Float = scale.divideScale(i, parts)
    val sfi1 : Float = scale.divideScale(0, divs)
    val sfi2 : Float = scale.divideScale(1, divs)
    val k : Int = (i + 1) / 2
    val l : Int = i / 2
    val y : Float = (h / (1 + k)) * (2 * k - 1)
    val y1 : Float = y * sfi1
    val y2 : Float = y * sfi2
    val dx : Float = (1 - 2 * l) * (w / 2) * k
    val x1 : Float = l * w / 2 + dx * sfi1
    val x2 : Float = l * w / 2 + dx * sfi2
    drawLine(x1, y1, x2, y2, paint)
}

fun Canvas.drawMirrorTriPath(scale : Float, w : Float, h : Float, paint : Paint) {
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f)
        translate(-w / 2, - h / 2)
        drawTriPath(j, scale, w, h, paint)
        restore()
    }
}

fun Canvas.drawMTPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(w / 2, h / 2)
    drawMirrorTriPath(scale, w, h, paint)
    restore()
}

class MirrorTriPathView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class MTPNode(var i : Int, val state : State = State()) {

        private var next : MTPNode? = null
        private var prev : MTPNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = MTPNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawMTPNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MTPNode {
            var curr : MTPNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class MirrorTriPath(var i : Int) {

        private var curr : MTPNode = MTPNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
}