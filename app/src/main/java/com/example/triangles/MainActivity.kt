package com.example.triangles

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.triangles.databinding.ActivityMainBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.icu.text.SimpleDateFormat
import android.view.View
import android.view.animation.AnimationSet
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.delay
import java.util.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding<ActivityMainBinding>()
    private val myCalendar: Calendar = Calendar.getInstance()
    private val logic: Logic = Logic()
    private val triangles by lazy {
        arrayOf(
            binding.triangleView,
            binding.triangleView2,
            binding.triangleView3,
            binding.triangleView4,
            binding.triangleView5
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val date = OnDateSetListener { view, year, month, day ->
            myCalendar[Calendar.DAY_OF_MONTH] = day
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.YEAR] = year
            updateLabel()
            updateTriangles()
        }
        binding.editText.setOnClickListener {
            DatePickerDialog(
                this@MainActivity, date,
                myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.editText.setText(dateFormat.format(myCalendar.time))
    }

    @SuppressLint("Recycle")
    private fun updateTriangles() {
        logic.setDate(
            myCalendar.get(Calendar.DAY_OF_MONTH),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.YEAR)
        )
        for (i in triangles.indices) {
            AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(triangles[i], View.ALPHA, 1f, 0.0f).apply {
                    duration = 400
                    startDelay = 400L * i
                    addListener(object: Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {}
                        override fun onAnimationEnd(p0: Animator) {
                            triangles[i].setUpperText(logic.getUpperCorner(i).toString())
                            triangles[i].setLeftLowerText(logic.getLowerCorner(i, i - 1).toString())
                            triangles[i].setRightLowerText(logic.getLowerCorner(i, i + 1).toString())
                            triangles[i].setInnerLowerText(logic.getInnerLowerCorner(i).toString())
                            triangles[i].setInnerLeftUpperText(logic.getInnerLeftUpperCorner(i).toString())
                            triangles[i].setInnerRightUpperText(logic.getInnerRightUpperCorner(i).toString())
                        }
                        override fun onAnimationCancel(p0: Animator) {}
                        override fun onAnimationRepeat(p0: Animator) {}
                    })
                }).before(ObjectAnimator.ofFloat(triangles[i], View.ALPHA, 0.0f, 1f).apply {
                    duration = 400
                })
            }.start()
        }

        binding.missionText.text = logic.missionCalculate()
    }
}