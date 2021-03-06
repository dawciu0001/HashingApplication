package wingeddev.example.hashingapplication.presentation.selectionFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import wingeddev.example.hashingapplication.R
import wingeddev.example.hashingapplication.databinding.FragmentSelectionBinding
import wingeddev.example.hashingapplication.presentation.mainActivity.MainActivityViewModel
import wingeddev.example.hashingapplication.util.generateRandomString

class SelectionFragment : Fragment() {

    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        setUpDropdownMenu(binding)
        /* fixes a small bug, that prevents users from selecting
        other algorithms after getting back to this fragment */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionBinding.inflate(inflater, container, false)
        setUpDropdownMenu(binding)

        // salting functionality
        binding.addSaltCB.setOnCheckedChangeListener { _, isChecked ->
            binding.randomSaltTV.text = generateRandomString(32)
            binding.randomSaltTV.isVisible = isChecked
            binding.randomSaltTitleTV.isVisible = isChecked
        }

        // generate button functionality
        binding.generateBTN.setOnClickListener {
            val usersInput = binding.textToHashET.text.toString()
            val salt = binding.randomSaltTV.text.toString()
            val dataToEncrypt = salt + usersInput // Prepending the salt
            val result = when(binding.selectedAlgorithmACTV.text.toString()) {
                "MD5" -> { viewModel.md5FromText(dataToEncrypt) }
                "SHA-256" -> { viewModel.sha256FromText(dataToEncrypt) }
                "SHA-512" -> { viewModel.sha512FromText(dataToEncrypt) }
                else -> { "Well I do not know how you have achieved that ;)" }
            }
            findNavController().navigate(R.id.resultFragment, bundleOf("result" to result))
        }

        return binding.root
    }

    private fun setUpDropdownMenu(binding: FragmentSelectionBinding) {
        val hashingAlgorithms = resources.getStringArray(R.array.hashing_algorithms)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_menu_item, hashingAlgorithms)
        binding.selectedAlgorithmACTV.setAdapter(arrayAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // prevents memory leaks
    }
}