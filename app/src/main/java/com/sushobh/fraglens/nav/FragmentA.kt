package com.sushobh.fraglens.nav

import androidx.compose.ui.layout.layout



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sushobh.fraglens.main.R


class FragmentA : Fragment() {

    private val sharedViewModel: SharedViewModel by navGraphViewModels(R.id.my_nav_graph) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = layoutInflater.inflate(R.layout.fragment_a_layout, container, false) // Assume you have fragment_a_layout.xml

        val textView: TextView = view.findViewById(R.id.textViewA)
        val editTextA: EditText = view.findViewById(R.id.editTextA)
        val buttonUpdate: Button = view.findViewById(R.id.buttonUpdateA)
        val buttonGoToB: Button = view.findViewById(R.id.buttonGoToB)

        sharedViewModel.sharedData.observe(viewLifecycleOwner) { data ->
            textView.text = "From ViewModel: $data"
        }

        buttonUpdate.setOnClickListener {
            sharedViewModel.updateData(editTextA.text.toString())
        }

        buttonGoToB.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentA_to_fragmentB)
        }

        return view
    }
}
