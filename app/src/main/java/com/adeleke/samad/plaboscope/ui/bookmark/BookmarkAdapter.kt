package com.adeleke.samad.plaboscope.ui.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.models.Question

class BookmarkAdapter : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {
    private val TAG = javaClass.simpleName

    var data = listOf<Question>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionNumberTV: TextView = itemView.findViewById(R.id.questionCardNumber)
        private val specialtyTV: TextView = itemView.findViewById(R.id.questionCardSpecialty)
        var bodyLayout: LinearLayout = itemView.findViewById(R.id.questionCardBody)
        private val bodyTV: TextView = itemView.findViewById(R.id.questionCardTV)

        private val optionA: RadioButton = itemView.findViewById(R.id.questionCardOptionA)
        private val optionB: RadioButton = itemView.findViewById(R.id.questionCardOptionB)
        private val optionC: RadioButton = itemView.findViewById(R.id.questionCardOptionC)
        private val optionD: RadioButton = itemView.findViewById(R.id.questionCardOptionD)
        private val optionE: RadioButton = itemView.findViewById(R.id.questionCardOptionE)

        private val explanationTV: TextView = itemView.findViewById(R.id.questionCardExplanation)

        var bodyIsVisible = false

        init {
            itemView.setOnClickListener {
                if (bodyIsVisible) {
                    bodyLayout.visibility = View.GONE
                    bodyIsVisible = false
                } else {
                    bodyLayout.visibility = View.VISIBLE
                    bodyIsVisible = true
                }
            }
        }

        fun bind(question: Question) {
            questionNumberTV.text = "(${question.number})"
            specialtyTV.text = question.specialty
            bodyTV.text = question.body
            optionA.text = question.optionA
            optionB.text = question.optionB
            optionC.text = question.optionC
            optionD.text = question.optionD
            optionE.text = question.optionE
            explanationTV.text = question.explanation
            when (question.correctAnswerKey) {
                "0" -> optionA.shade()
                "1" -> optionB.shade()
                "2" -> optionC.shade()
                "3" -> optionD.shade()
                "4" -> optionE.shade()
            }
            if (question.optionC == ".") optionC.visibility = View.GONE
            if (question.optionD == ".") optionD.visibility = View.GONE
            if (question.optionE == ".") optionE.visibility = View.GONE
        }

        private fun RadioButton.shade() {
            this.setBackground(itemView.context.resources.getDrawable(R.drawable.correct_answer_background))
            this.setChecked(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkAdapter.BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question_card, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val question = data[position]
        holder.bind(question)
    }


    override fun getItemCount(): Int {
        return data.size
    }
}