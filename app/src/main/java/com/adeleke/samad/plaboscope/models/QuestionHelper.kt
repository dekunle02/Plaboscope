package com.adeleke.samad.plaboscope.models

import android.content.Context
import com.adeleke.samad.plaboscope.R


class QuestionHelper(context: Context) {

    private val questions: Array<String> = context.resources.getStringArray(R.array.blockQuestions)
    private val answers: Array<String> = context.resources.getStringArray(R.array.blockAnswers)
    private val explanations: Array<String> = context.resources.getStringArray(R.array.blockExplanations)
    private val optionA: Array<String> = context.resources.getStringArray(R.array.blockOptionA)
    private val optionB: Array<String> = context.resources.getStringArray(R.array.blockOptionB)
    private val optionC: Array<String> = context.resources.getStringArray(R.array.blockOptionC)
    private val optionD: Array<String> = context.resources.getStringArray(R.array.blockOptionD)
    private val optionE: Array<String> = context.resources.getStringArray(R.array.blockOptionE)


    private val cnsIndex: Array<String> = context.resources.getStringArray(R.array.CNSquestions)
    private val dermIndex: Array<String> = context.resources.getStringArray(R.array.DERMquestions)
    private val emIndex: Array<String> = context.resources.getStringArray(R.array.EMquestions)
    private val endoIndex: Array<String> = context.resources.getStringArray(R.array.ENDOquestions)
    private val entIndex: Array<String> = context.resources.getStringArray(R.array.ENTquestions)
    private val haemIndex: Array<String> = context.resources.getStringArray(R.array.HAEMOquestions)
    private val idIndex: Array<String> = context.resources.getStringArray(R.array.IDquestions)
    private val nephroIndex: Array<String> = context.resources.getStringArray(R.array.NEPHROquestions)
    private val obGynIndex: Array<String> = context.resources.getStringArray(R.array.OBGYNquestions)
    private val opthaIndex: Array<String> = context.resources.getStringArray(R.array.OPTHAquestions)
    private val orthoIndex: Array<String> = context.resources.getStringArray(R.array.ORTHOquestions)
    private val paedIndex: Array<String> = context.resources.getStringArray(R.array.PAEDquestions)
    private val psychIndex: Array<String> = context.resources.getStringArray(R.array.PSYCHquestions)
    private val respIndex: Array<String> = context.resources.getStringArray(R.array.RESPquestions)
    private val rheumaIndex: Array<String> = context.resources.getStringArray(R.array.RHEUMAquestions)
    private val surgeryIndex: Array<String> = context.resources.getStringArray(R.array.SURGERYquestions)
    private val otherIndex: Array<String> = context.resources.getStringArray(R.array.OTHERquestions)

    private val cns = context.resources.getString(R.string.cns)
    private val derm = context.resources.getString(R.string.dermatology)
    private val em = context.resources.getString(R.string.emergency_medicine)
    private val endo = context.resources.getString(R.string.endocrinology)
    private val ent = context.resources.getString(R.string.ent)
    private val haem = context.resources.getString(R.string.haematology)
    private val id = context.resources.getString(R.string.infectious_diseases)
    private val nephro = context.resources.getString(R.string.nephrology)
    private val obGyn = context.resources.getString(R.string.obs_amp_gyn)
    private val optha = context.resources.getString(R.string.ophthalmology)
    private val paed = context.resources.getString(R.string.paediatrics)
    private val ortho = context.resources.getString(R.string.orthopaedics)
    private val resp = context.resources.getString(R.string.pulmonology)
    private val rheum = context.resources.getString(R.string.rheumatology)
    private val surgery = context.resources.getString(R.string.surgery)
    private val psych = context.resources.getString(R.string.psychiatry)
    private val other = context.resources.getString(R.string.other)


    private fun fetchQuestionByIndex(i: Int): Question {
        val number = (i + 1).toString()
        val specialty: String = when (number) {
            in cnsIndex -> cns
            in dermIndex -> derm
            in emIndex -> em
            in endoIndex -> endo
            in entIndex -> ent
            in haemIndex -> haem
            in idIndex -> id
            in paedIndex -> paed
            in nephroIndex -> nephro
            in obGynIndex -> obGyn
            in opthaIndex -> optha
            in orthoIndex -> ortho
            in psychIndex -> psych
            in respIndex -> resp
            in rheumaIndex -> rheum
            in surgeryIndex -> surgery
            else -> other
        }

        return Question(
                number = number,
                body = questions[i].capitalize(),
                optionA = optionA[i],
                optionB = optionB[i],
                optionC = optionC[i],
                optionD = optionD[i],
                optionE = optionE[i],
                correctAnswerKey = answers[i],
                explanation = explanations[i],
                specialty = specialty
        )
    }


    companion object {
        fun getTotalQuestionsSize(context: Context): Int {
            val questions: Array<String> = context.resources.getStringArray(R.array.blockQuestions)
            return questions.size
        }
    }

    fun getIndicesOfSpecialty(specialty: String): List<String> {
        val arr = when (specialty) {
            cns -> cnsIndex
            derm -> dermIndex
            em -> emIndex
            endo -> endoIndex
            ent -> entIndex
            haem -> haemIndex
            id -> idIndex
            paed -> paedIndex
            nephro -> nephroIndex
            obGyn -> obGynIndex
            optha -> opthaIndex
            ortho -> orthoIndex
            resp -> respIndex
            psych -> psychIndex
            resp -> respIndex
            rheum -> rheumaIndex
            surgery -> surgeryIndex
            else -> otherIndex
        }
        return arr.toList()
    }

    fun initializeStatistics(): List<Stat> {
        val arr = mutableListOf<Stat>()
        listOf<String>(cns,
                derm,
                em,
                endo,
                ent,
                haem,
                id,
                nephro,
                obGyn,
                optha,
                paed,
                ortho,
                resp,
                rheum,
                surgery,
                psych,
                other,
                "all").forEach { specialtyName ->
            arr.add(
                    Stat(specialtyName = specialtyName)
            )
        }
        return arr.toList()
    }

    fun getListOfTags(): List<String> = listOf<String>(cns,
            derm,
            em,
            endo,
            ent,
            haem,
            id,
            nephro,
            obGyn,
            optha,
            paed,
            ortho,
            resp,
            rheum,
            surgery,
            psych,
            other,
            "General", "Personal")

    fun getAllQuestions(): List<Question> {
        val questionList = mutableListOf<Question>()
        for (i in questions.indices) {
            questionList.add(fetchQuestionByIndex(i))
        }
        return questionList.toList()
    }

    fun getQuestionShareString(question: Question): String {
        return "Hi. Check out this question." + "\n" +
                question.body + "\n" +
                question.optionA + "\n" +
                question.optionB + "\n" +
                question.optionC + "\n" +
                question.optionD + "\n" +
                question.optionE + "\n" +
                question.explanation

    }

    fun getSpecialtySize(specialty: String): Int {
        val sum = (cnsIndex.size + dermIndex.size + emIndex.size + endoIndex.size + entIndex.size
                + haemIndex.size
                + idIndex.size
                + paedIndex.size
                + nephroIndex.size
                + obGynIndex.size
                + opthaIndex.size
                + orthoIndex.size
                + respIndex.size
                + psychIndex.size
                + respIndex.size
                + rheumaIndex.size
                + surgeryIndex.size)
        return when (specialty) {
            cns -> cnsIndex.size
            derm -> dermIndex.size
            em -> emIndex.size
            endo -> endoIndex.size
            ent -> entIndex.size
            haem -> haemIndex.size
            id -> idIndex.size
            paed -> paedIndex.size
            nephro -> nephroIndex.size
            obGyn -> obGynIndex.size
            optha -> opthaIndex.size
            ortho -> orthoIndex.size
            resp -> respIndex.size
            psych -> psychIndex.size
            resp -> respIndex.size
            rheum -> rheumaIndex.size
            surgery -> surgeryIndex.size
            else -> otherIndex.size + (questions.size - sum)
        }
    }


}