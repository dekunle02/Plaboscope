package com.adeleke.samad.plaboscope.ui.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.databinding.FragmentDashboardBinding
import com.adeleke.samad.plaboscope.ui.test.TestActivity

class DashboardFragment : Fragment() {

    private val TAG = javaClass.simpleName

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        // onClick listeners
        binding.randomTestButton.setOnClickListener {
            val i = Intent(context, TestActivity::class.java)
            i.putExtra("testType", "random")
            startActivity(i)
        }
        binding.mockButton.setOnClickListener{
            val i =Intent(context, TestActivity::class.java)
            i.putExtra("testType", "mock")
            startActivity(i)
        }
        binding.mainTestButton.setOnClickListener {
            val i = Intent(context, TestActivity::class.java)
            i.putExtra("testType","1700")
            startActivity(i)
        }

        // viewModel observers
        viewModel.overallProgressValue.observe(viewLifecycleOwner, Observer {
          if (it != null) {
            binding.waveLoadingView.progressValue = it
            binding.waveLoadingView.centerTitle = "Overall Progress: ${it}%"
          }
        })
        viewModel.ringProgressValue.observe(viewLifecycleOwner, Observer {
          binding.ringProgressView.percent = it
        })

        return binding.root
    }

}