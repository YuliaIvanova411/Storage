package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.model.PhotoModel
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
     ownerProducer = ::requireParentFragment,
    )

    private val photoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = requireNotNull(it.data?.data)
            viewModel.setPhoto(PhotoModel(uri = uri, file = uri.toFile()))

        } else {
            Toast.makeText(requireContext(),(R.string.pick_photo_error), Toast.LENGTH_SHORT)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)


        arguments?.textArg
            ?.let(binding.content::setText)

        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.previewContainer.isGone = true
                return@observe
            }
            binding.previewContainer.isVisible = true
            binding.preview.setImageURI(it.uri)


        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.content.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)
        binding.camera.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent (photoLauncher::launch)
        }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent (photoLauncher::launch)
        }

        binding.content.requestFocus()
        //val intent = Intent()
        //binding.content.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
//        binding.ok.setOnClickListener {
//
//            val text = binding.content.text.toString()
//            if (!text.isBlank()){
//                val content = binding.content.text.toString()
//                viewModel.changeContent(content)
//                viewModel.save()
//                findNavController().navigateUp()
//            }
            viewModel.postCreated.observe(viewLifecycleOwner) {

                findNavController().navigateUp()
                viewModel.loadPosts()
            }



//        binding.cancelButton.setOnClickListener{
//            //val intent = Intent()
//            //(Activity.RESULT_CANCELED)
//            AndroidUtils.hideKeyboard(requireView())
//            findNavController().navigateUp()
//        }
        return binding.root
    }

}