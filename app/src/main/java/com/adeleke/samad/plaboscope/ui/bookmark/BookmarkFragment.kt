package com.adeleke.samad.plaboscope.ui.bookmark

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.snackbar.Snackbar

class BookmarkFragment: Fragment() {

    private val TAG = javaClass.simpleName

    private val viewModel: BookmarkViewModel by viewModels()
    private lateinit var bookmarkRecyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        bookmarkRecyclerView = view.findViewById(R.id.bookmarkRecyclerView)
        bookmarkRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = BookmarkAdapter()
        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            adapter.data = it
        })
        bookmarkRecyclerView.adapter = adapter
        val myItemTouchHelper = ItemTouchHelper(MySwipeCallback(adapter))
        myItemTouchHelper.attachToRecyclerView(bookmarkRecyclerView)

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.populateRecyclerViewData()
    }

    inner class MySwipeCallback (val adapter: BookmarkAdapter): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        val colorBookmarkBackground = ColorDrawable(resources.getColor(R.color.wrongAnswerRed))
        val bookmarkIcon =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_bookmark) }!!

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
            val pos = viewHolder.adapterPosition
            val questionSwiped = viewModel.recyclerData.value!![pos]
            viewModel.removeQuestionFromBookmarked(questionSwiped)
            adapter.notifyItemRemoved(pos)
            Snackbar.make(
                    bookmarkRecyclerView,
                    getString(R.string.question_unbookmarked),
                    Snackbar.LENGTH_LONG
            ).setAction(
                    getString(R.string.undo_snackbar_message)
            ) {
                viewModel.bookmarkQuestion(pos, questionSwiped)
                adapter.notifyItemInserted(pos)
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
                    (viewHolder.itemView.height - bookmarkIcon.intrinsicHeight) / 2
            // swiped towards left
            if (dX > 0) {
                colorBookmarkBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                )
                bookmarkIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + bookmarkIcon.intrinsicWidth,
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
                bookmarkIcon.setBounds(
                        itemView.right - iconMarginVertical - bookmarkIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                )
                bookmarkIcon.level = 0
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

            bookmarkIcon.draw(c)

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