package com.ferelin.core.ui.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ferelin.core.ui.R

class Separator @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  companion object {
    private const val initialCornerRadius = 8F
  }

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val cornerRadius = initialCornerRadius * resources.displayMetrics.density

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    paint.color = ContextCompat.getColor(context, R.color.grey)
    canvas.drawRoundRect(
      0F,
      0F,
      width.toFloat(),
      height.toFloat(),
      cornerRadius,
      cornerRadius,
      paint
    )
  }
}