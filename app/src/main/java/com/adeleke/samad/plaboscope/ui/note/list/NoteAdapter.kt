package com.adeleke.samad.plaboscope.ui.note.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.models.Note
import com.adeleke.samad.plaboscope.NOTE_INTENT_TAG
import com.adeleke.samad.plaboscope.ui.note.edit.EditNoteActivity

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private val TAG = javaClass.simpleName

    var data = listOf<Note>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTv: TextView = itemView.findViewById(R.id.noteTitleTV)
        private val tagTv: TextView = itemView.findViewById(R.id.noteTagTV)
        private val bodyTv: TextView = itemView.findViewById(R.id.noteBodyTV)

        fun bind(note: Note) {
            titleTv.text = note.title
            tagTv.text = note.tag
            bodyTv.text = note.body
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = data[position]
        holder.bind(note)
        holder.itemView.setOnClickListener {
            val i = Intent(holder.itemView.context, EditNoteActivity::class.java)
            i.putExtra(NOTE_INTENT_TAG, note.id)
            holder.itemView.context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}