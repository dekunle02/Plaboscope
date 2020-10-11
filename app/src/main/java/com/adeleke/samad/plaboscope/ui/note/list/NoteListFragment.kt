package com.adeleke.samad.plaboscope.ui.note.list

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.NEW_NOTE_ID
import com.adeleke.samad.plaboscope.NOTE_INTENT_TAG
import com.adeleke.samad.plaboscope.ui.note.edit.EditNoteActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class NoteListFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private val viewModel: NoteListViewModel by viewModels()
    private lateinit var noteRecyclerView: RecyclerView
    private lateinit var addNewButton: FloatingActionButton

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)

        noteRecyclerView = view.findViewById(R.id.noteListRecyclerView)
        noteRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NoteAdapter()

        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            adapter.data = it
        })
        noteRecyclerView.adapter = adapter
        val myItemTouchHelper = ItemTouchHelper(MySwipeCallback(adapter))
        myItemTouchHelper.attachToRecyclerView(noteRecyclerView)

        addNewButton = view.findViewById<FloatingActionButton>(R.id.addNewNoteButton)
        addNewButton.setOnClickListener {
            addNewNote()
        }
        return view
    }

    private fun addNewNote() {
        val i = Intent(context, EditNoteActivity::class.java)
        i.putExtra(NOTE_INTENT_TAG, NEW_NOTE_ID)
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        viewModel.populateRecyclerView()
    }

    inner class MySwipeCallback(val adapter: NoteAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        val colorBookmarkBackground = ColorDrawable(resources.getColor(R.color.wrongAnswerRed))
        val deleteIcon =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete) }!!

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
            val pos = viewHolder.adapterPosition
            val swipedNote = viewModel.recyclerData.value!![pos]
            viewModel.deleteNote(swipedNote)
            adapter.notifyItemRemoved(pos)
            viewModel.populateRecyclerView()
            Snackbar.make(
                    addNewButton,
                    getString(R.string.note_deleted),
                    Snackbar.LENGTH_LONG
            ).setAction(
                    getString(R.string.undo_snackbar_message)
            ) {
                viewModel.addNote(pos, swipedNote)
                adapter.notifyItemInserted(pos)
                viewModel.populateRecyclerView()
            }.setActionTextColor(resources.getColor(R.color.yellow))
                    .show()
        }

        override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2
            // swiped towards left
            if (dX > 0) {
                colorBookmarkBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                )
                deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                )
                // swiped towards left

            } else {

                colorBookmarkBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                )
                deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                )
                deleteIcon.level = 0
            }

            colorBookmarkBackground.draw(c)

            c.save()

            if (dX > 0)
                c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            else
                c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                )

            deleteIcon.draw(c)

            c.restore()

            super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
            )
        }

    }


}