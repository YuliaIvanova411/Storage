package ru.netology.nmedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()
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
            if (!text.isBlank()){
                val content = binding.content.text.toString()
                viewModel.changeContent(content)
                viewModel.save()
                findNavController().navigateUp()
            }
            viewModel.postCreated.observe(viewLifecycleOwner) {

                findNavController().navigateUp()
                viewModel.loadPosts()
            }


        }
        binding.cancelButton.setOnClickListener{
            //val intent = Intent()
            //(Activity.RESULT_CANCELED)
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        return binding.root
    }

}