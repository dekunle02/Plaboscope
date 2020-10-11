package com.adeleke.samad.plaboscope.ui.specialty

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.models.QuestionHelper
import com.adeleke.samad.plaboscope.models.Stat
import com.adeleke.samad.plaboscope.ui.test.TestActivity
import com.timqi.sectorprogressview.ColorfulRingProgressView
import java.util.*

class SpecialtyAdapter : RecyclerView.Adapter<SpecialtyAdapter.SpecialtyViewHolder>() {
    private val TAG = javaClass.simpleName

    var data = listOf<Stat>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class SpecialtyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTv: TextView = itemView.findViewById(R.id.specialtyTitle)
        private val detailTv: TextView = itemView.findViewById(R.id.specialtyDetail)
        private val ringTV: TextView = itemView.findViewById(R.id.specialtyRingTextView)
        private val ring: ColorfulRingProgressView = itemView.findViewById(R.id.specialtyRing)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.specialtyProgressBar)

        private val questionHelper = QuestionHelper(itemView.context)

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, TestActivity::class.java)
                intent.putExtra("testType", data[adapterPosition].specialtyName)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(stat: Stat) {
            titleTv.text = stat.specialtyName
            val currentQuestionIndex = stat.currentQuestionIndex
            val score: Int = if (stat.attemptTally != 0) stat.correctTally * 100 / stat.attemptTally else 0
            val timeTaken = stat.time
            val hours: Int = timeTaken / 3600
            val minutes: Int = timeTaken % 3600 / 60
            val secs: Int = timeTaken % 60
            val displayedTime = java.lang.String
                    .format(Locale.getDefault(),
                            "%d hr:%02d min:%02d sec", hours,
                            minutes, secs)

            detailTv.text = "Time spent:$displayedTime\n Question No: ${currentQuestionIndex}/${questionHelper.getSpecialtySize(stat.specialtyName)}"
            ringTV.text = "$score%"
            ring.percent = score.toFloat()
            progressBar.progress = currentQuestionIndex * 100 / questionHelper.getSpecialtySize(stat.specialtyName)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialtyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_specialty, parent, false)
        return SpecialtyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialtyViewHolder, position: Int) {
        val statItem = data[position]
        holder.bind(statItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}