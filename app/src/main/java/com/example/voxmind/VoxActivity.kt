/**
 * VoxActivity represents the core functionality of the quiz game.
 * It includes:
 * - Displaying quiz questions and options.
 * - Navigating questions by detecting shake gestures:
 *   - Shake left: Go to the previous question.
 *   - Shake right: Go to the next question.
 * - Keeping track of score and quiz progress.
 * - Showing a custom dialog with the quiz result.
 */
package com.example.voxmind

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.voxmind.databinding.ActivityVoxBinding
import com.example.voxmind.databinding.ScoreDialogBinding

class VoxActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var questionModelList: List<QuestionModel> = listOf()
        var time: String = ""
    }

    private lateinit var binding: ActivityVoxBinding
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 8.0 // Reduced sensitivity threshold for easier triggering

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the sensor manager and accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        binding.apply {
            btn0.setOnClickListener(this@VoxActivity)
            btn1.setOnClickListener(this@VoxActivity)
            btn2.setOnClickListener(this@VoxActivity)
            btn3.setOnClickListener(this@VoxActivity)
            nextBtn.setOnClickListener(this@VoxActivity)
        }

        loadQuestions()
        startTimer()
    }

    private fun startTimer() {
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text =
                    String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    private fun loadQuestions() {
        selectedAnswer = ""
        if (currentQuestionIndex >= questionModelList.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text =
                "Question ${currentQuestionIndex + 1} / ${questionModelList.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex.toFloat() / questionModelList.size.toFloat()) * 100).toInt()

            questionTextview.text = questionModelList[currentQuestionIndex].question
            val options = questionModelList[currentQuestionIndex].options
            btn0.text = options.getOrNull(0) ?: "Option A"
            btn1.text = options.getOrNull(1) ?: "Option B"
            btn2.text = options.getOrNull(2) ?: "Option C"
            btn3.text = options.getOrNull(3) ?: "Option D"

            resetButtonColors()
            selectedAnswer = ""
        }
    }

    private fun resetButtonColors() {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }
    }

    override fun onClick(view: View?) {
        val clickedBtn = view as Button

        if (clickedBtn.id == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Please select an answer to continue", Toast.LENGTH_SHORT).show()
                return
            }

            if (selectedAnswer == questionModelList[currentQuestionIndex].correct) {
                score++
                Log.i("Score of Quiz", "Score: $score")
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            resetButtonColors()
            clickedBtn.setBackgroundColor(getColor(R.color.red))
            selectedAnswer = clickedBtn.text.toString()
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0] // Horizontal motion
                val y = it.values[1] // Vertical motion
                val z = it.values[2] // Depth motion

                val currentTime = System.currentTimeMillis()

                if (currentTime - lastShakeTime > 1000) { // Debounce time
                    lastShakeTime = currentTime

                    if (x > shakeThreshold) {
                        // Shake to the right
                        navigateToNextQuestion()
                    } else if (x < -shakeThreshold) {
                        // Shake to the left
                        navigateToPreviousQuestion()
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun navigateToNextQuestion() {
        if (currentQuestionIndex < questionModelList.size - 1) {
            currentQuestionIndex++
            loadQuestions()
            Toast.makeText(this, "Next Question!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No more questions!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            loadQuestions()
            Toast.makeText(this, "Previous Question!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "This is the first question!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finishQuiz() {
        val totalQuestion = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestion.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            if (percentage > 60) {
                scoreProgressIndicator.setIndicatorColor(getColor(R.color.teal))
                scoreTitle.text = "Congratulations! You Passed"
                scoreTitle.setTextColor(getColor(R.color.teal))
            } else {
                scoreProgressIndicator.setIndicatorColor(getColor(R.color.red))
                scoreTitle.text = "Oops! You Failed"
                scoreTitle.setTextColor(getColor(R.color.red))
            }
            scoreSubtitle.text = "$score out of $totalQuestion are correct"
            finishBtn.setOnClickListener { finish() }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialog.show()

        Log.i("Quiz Complete", "Final Score: $score")
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }
//change1
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }
}
