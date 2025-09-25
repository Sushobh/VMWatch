package com.sushobh.fraglens.nav


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


class FragmentB : Fragment() {

    private val sharedViewModel: SharedViewModel by navGraphViewModels(R.id.my_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = layoutInflater.inflate(
            R.layout.fragment_b_layout,
            container,
            false
        )

        val textView: TextView = view.findViewById(R.id.textViewB)
        val editTextB: EditText = view.findViewById(R.id.editTextB)
        val buttonUpdate: Button = view.findViewById(R.id.buttonUpdateB)
        val buttonGoToA: Button = view.findViewById(R.id.buttonGoToA)


        sharedViewModel.sharedData.observe(viewLifecycleOwner) { data ->
            textView.text = "From ViewModel: $data"
        }

        buttonUpdate.setOnClickListener {
            sharedViewModel.updateData(editTextB.text.toString())
        }

        buttonGoToA.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}
