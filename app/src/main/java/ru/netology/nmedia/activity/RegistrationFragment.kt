package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.RegisterViewModel
@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: RegisterViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater,container,false)

        val photoSet =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when(it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root,
                            R.string.error_loading,Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {
                        var uri = it.data?.data ?:
                        return@registerForActivityResult
                        viewModel.addPhoto(uri, uri.toFile())
                    }
                }
            }
        viewModel.media.observe(viewLifecycleOwner) { media ->
            if (media == null) {
                binding.avatarPreview.isGone = true
                return@observe
            } else {
                binding.avatarPreview.isVisible = true
                binding.registerAvatar.setImageURI(media.uri)
            }

        }
        binding.registerButton.setOnClickListener {
            val login = binding.signUpLogin.text.toString()
            val password = binding.signUpPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val name = binding.signUpName.text.toString()

            if (password == confirmPassword) {
                viewModel.register(login, password, name)
                AndroidUtils.hideKeyboard(requireView())
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.match_passwords,
                    Snackbar.LENGTH_LONG
                    ).show()
            }
        }
        viewModel.state.observe(viewLifecycleOwner) {state ->
            binding.registerButton.isEnabled = !state.loading

            if (state.loggedIn) {
                findNavController().navigateUp()
            }
            if (state.isBlank) {
                Snackbar.make(
                    binding.root,
                    R.string.blankRegister,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            if (state.error) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
            binding.photo.setOnClickListener {
                ImagePicker.with(this)
                    .cameraOnly()
                    .crop()
                    .compress(2048)
                    .createIntent (photoSet::launch)
            }
            binding.clear.setOnClickListener {
                viewModel.clearPhoto()
            }
        return binding.root
    }
}