package ru.netology.nmedia

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = :: requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)


        arguments?.textArg
            ?.let(binding.content::setText)


        binding.content.requestFocus()
        //val intent = Intent()
        //binding.content.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        binding.ok.setOnClickListener {
            val text = binding.content.text.toString()
            if (!text.isNullOrBlank()){
                val content = binding.content.text.toString()
                viewModel.changeContent(content)
                viewModel.save()
                findNavController().navigateUp()
            }

        }
        binding.cancelButton.setOnClickListener{
            //val intent = Intent()
            (Activity.RESULT_CANCELED)
            findNavController().navigateUp()
        }
        return binding.root
    }

//    object Contract : ActivityResultContract<Unit, String?>() {
//        override fun createIntent(context: Context, input: Unit) =
//            Intent(context, NewPostActivity ::class.java)
//
//        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra(Intent.EXTRA_TEXT)
//
//    }
}