package com.adeleke.samad.plaboscope.ui.specialty

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.plaboscope.R

class SpecialtyFragment : Fragment() {
  private val TAG = javaClass.simpleName

  private val viewModel: SpecialtyViewModel by viewModels()
  private lateinit var specialtyRecyclerView: RecyclerView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_specialty, container, false)

    specialtyRecyclerView = view.findViewById(R.id.specialtyRecyclerView)
    specialtyRecyclerView.layoutManager = LinearLayoutManager(context)
    val adapter = SpecialtyAdapter()
    viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
      adapter.data = it
    })
    specialtyRecyclerView.adapter = adapter


    return view
  }

  override fun onResume() {
    super.onResume()
    viewModel.populateRecyclerView()
  }

}