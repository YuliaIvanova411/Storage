package ru.netology.nmedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.EditPostFragmentBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {
    companion object {
        var Bundle.edit: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditPostFragmentBinding.inflate(inflater, container, false)
        arguments?.edit?.let(binding.content::setText)
        binding.content.setText(arguments?.getString("editedText"))

        //binding.content.setText(intent?.getStringExtra(Intent.EXTRA_TEXT))
        binding.ok.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.error_empty_content),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.changeContent(binding.content.text.toString())
                viewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }
        binding.cancelButton.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}
//        val activity = this
//        activity.onBackPressedDispatcher.addCallback(
//            activity, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    setResult(Activity.RESULT_CANCELED, intent)
//                    finish()
//                }
//            }
//        )

//    }
//    object Contract : ActivityResultContract<String, String?>() {
//        override fun createIntent(context: Context, input: String) =
//            Intent(context, EditPostActivity ::class.java).putExtra(Intent.EXTRA_TEXT, input)
//
//        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra(Intent.EXTRA_TEXT)
//
//    }
//}