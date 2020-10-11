package com.adeleke.samad.plaboscope.ui.test

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adeleke.samad.plaboscope.MainActivity
import com.adeleke.samad.plaboscope.NOTE_INTENT_TAG
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.databinding.ActivityTestBinding
import com.adeleke.samad.plaboscope.ui.note.edit.EditNoteActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.timqi.sectorprogressview.ColorfulRingProgressView

class TestActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: ActivityTestBinding
    private lateinit var viewModel: TestViewModel

    private var canNavigateToNext = false
    private var chosenOption = ""
    private var rightOptionPosition = ""
    private var lastChosenOption = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        // Set up custom toolbar
        setSupportActionBar(binding.testToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // Set up on Click Listener to change Text of button
        binding.checkAnswerButton.setOnClickListener {
            viewModel.checkAnswerButtonClick(chosenOption)
        }

        // Get intent and instantiate viewModel
        val intent = intent
        val passedArg = intent.getStringExtra("testType")
        Log.d(TAG, "onCreate: passedArg -> $passedArg")
        supportActionBar!!.title = passedArg!!.capitalize()
        val viewModelFactory = TestViewModel.TestViewModelFactory(passedArg, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TestViewModel::class.java)
        binding.viewModel = viewModel


        // Observables
        viewModel.snackMessage.observe(this, Observer {
            Snackbar.make(binding.testLayout, it!!, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.rightAnswerPosition.observe(this, Observer {
            rightOptionPosition = it!!
        })
        viewModel.optionToHide.observe(this, Observer {
            when (it) {
                "C" -> binding.optionC.visibility = View.GONE
                "D" -> binding.optionD.visibility = View.GONE
                "E" -> binding.optionE.visibility = View.GONE
            }
        })
        viewModel.canShadeOptions.observe(this, Observer {
            if (it) {
                shadeOptions()
            } else {
                cleanOptions()
            }
        })
        viewModel.testResults.observe(this, Observer { results ->
            if (results != null) {
                showCompletePopup(binding.testLayout, results)
            }
        })
        viewModel.lastChosenAnswer.observe(this, Observer {
            lastChosenOption = it
        })
        viewModel.canShowLastAnswerPicked.observe(this, Observer {
            if (it) shadeOptions(lastChosenOption)
        })
        viewModel.canShowCheckAnswerButton.observe(this, Observer {canShowCheckAnswer->
            if (canShowCheckAnswer) {
                binding.checkAnswerButton.visibility = View.VISIBLE
                binding.nextQuestionButton.visibility = View.GONE
            } else {
                binding.checkAnswerButton.visibility = View.GONE
                binding.nextQuestionButton.visibility = View.VISIBLE
            }
        })


        setContentView(binding.root)
    }


    // This option cleans/shades the options after they have been colored green/red or checked


    private fun cleanOptions() {
        binding.optionsRadioGroup.clearCheck()
        binding.optionsRadioGroup.forEach {radio ->
            radio as RadioButton
            radio.setBackgroundColor(Color.TRANSPARENT)
            radio.visibility = View.VISIBLE
        }

        for (i in 0 until binding.optionsRadioGroup.childCount) {
            val button = binding.optionsRadioGroup.getChildAt(i) as RadioButton
            button.setBackgroundColor(Color.TRANSPARENT)
            button.visibility = View.VISIBLE
        }
        binding.explanationTextView.visibility = View.GONE
    }
    private fun shadeOptions() {
        binding.optionsRadioGroup.forEach { radio ->
            radio as RadioButton
            if (radio.tag == chosenOption) {
                radio.setBackground(getDrawable(R.drawable.wrong_answer_background))
            }
            if (radio.tag == rightOptionPosition) {
                radio.setBackground(getDrawable(R.drawable.correct_answer_background))
            }
        }
        binding.explanationTextView.visibility = View.VISIBLE
    }
    private fun shadeOptions(lastChoice: String) {
        cleanOptions()
        binding.optionsRadioGroup.forEach { radio ->
            radio as RadioButton
            if (radio.tag == lastChoice) {
                radio.setChecked(true)
                radio.setBackground(getDrawable(R.drawable.wrong_answer_background))
            }
            if (radio.tag == rightOptionPosition) {
                radio.setBackground(getDrawable(R.drawable.correct_answer_background))
            }
        }
        binding.explanationTextView.visibility = View.VISIBLE
    }


    fun optionSelected(view: View) {
        chosenOption = when (view.id) {
            R.id.optionA -> "0"
            R.id.optionB -> "1"
            R.id.optionC -> "2"
            R.id.optionD -> "3"
            R.id.optionE -> "4"
            else -> ""
        }
    }



    private fun showCompletePopup(view: View, result: Map<String, String>) {
        val builder = AlertDialog.Builder(this)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View = LayoutInflater.from(view.context).inflate(R.layout.popup_test_complete, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val percentageTV = dialogView.findViewById<TextView>(R.id.percentTextView)
        val crpv: ColorfulRingProgressView = dialogView.findViewById(R.id.ringProgressView)
        val timeTV: TextView = dialogView.findViewById(R.id.timeTextView)

        val percentage = result["percentage"]
        val time = result["time"]

        timeTV.text = time
        val progressAnimator = ObjectAnimator.ofFloat(crpv,
                "percent",
                0F, percentage!!.toFloat())
        progressAnimator.duration = 1500
        progressAnimator.interpolator = AccelerateInterpolator()
        progressAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                percentageTV.text = "Score:\n${percentage}%"
            }
        })
        progressAnimator.start()

        val dashboardButton: ExtendedFloatingActionButton = dialogView.findViewById(R.id.dashboardButton)
        dashboardButton.setOnClickListener {
            alertDialog.dismiss()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        alertDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_test, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bookmark_question -> {
                viewModel.bookmarkQuestion()
                return true
            }
            R.id.action_share_question -> {
                val shareMessage = viewModel.shareQuestion()
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        shareMessage)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
                return true
            }
            R.id.action_make_note -> {
                val i = Intent(this, EditNoteActivity::class.java)
                val createdNoteId = viewModel.getNoteAndIdFromQuestion()
                i.putExtra(NOTE_INTENT_TAG, createdNoteId)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}