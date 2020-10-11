package com.adeleke.samad.plaboscope.ui.note.edit

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.databinding.ActivityEditNoteBinding
import com.adeleke.samad.plaboscope.models.QuestionHelper
import com.adeleke.samad.plaboscope.NOTE_INTENT_TAG
import com.google.android.material.snackbar.Snackbar

class EditNoteActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: EditNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        // Setting up Toolbar
        this.setSupportActionBar(binding.editNoteToolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Setting up spinner
        val questionHelper =QuestionHelper(this)
        binding.noteTagEdit.setItems(questionHelper.getListOfTags())
        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == (Configuration.UI_MODE_NIGHT_YES)) {
            binding.noteTagEdit.setBackgroundColor(resources.getColor(R.color.textColorSecondary))
        }



        // Receive id for item to display and pass into viewModel
        val intent = intent
        val passedArgId = intent.getStringExtra(NOTE_INTENT_TAG)
        val viewModelFactory =
                EditNoteViewModel.EditNoteViewModelFactory(passedArgId!!, application)
        viewModel =
                ViewModelProvider(this, viewModelFactory).get(EditNoteViewModel::class.java)
        binding.viewmodel = viewModel


        // observables
        viewModel.snackMessage.observe(this, Observer {
            Snackbar.make(binding.noteBodyEdit, it!!, Snackbar.LENGTH_SHORT).show()
        })

        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        viewModel.addOrUpdateNote()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_note -> {
                showDeleteAlertDialog()
                return true
            }
            R.id.action_share -> {
                shareNote()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setIcon(R.drawable.ic_warning)
                .setCancelable(true)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.are_you_sure_delete))
                .setPositiveButton(
                        getString(R.string.yes)
                ) { p0, p1 ->
                    viewModel.deleteNote()
                    onBackPressed()
                }
                .setNegativeButton(
                        getString(R.string.no)
                ) { dialogInterface, p1 ->
                    dialogInterface!!.cancel()
                }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun shareNote() {
        val shareMessage = viewModel.note.toString()
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                shareMessage)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }


}