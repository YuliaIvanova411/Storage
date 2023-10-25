package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val feedFragmentBinding = FragmentFeedBinding.inflate(layoutInflater, container, false)



//        val editPostContract = registerForActivityResult(EditPostActivity.Contract) { result ->
//            result ?: return@registerForActivityResult
//            viewModel.changeContent(result)
//            viewModel.save()
//        }

        val adapter = PostAdapter(
            object : PostListener {
                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    // editPostContract.launch(post.content)
                }


                override fun onPlay(post: Post) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink))
                    startActivity(intent)
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val startIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(startIntent)
                }

            }

        )
        feedFragmentBinding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        feedFragmentBinding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)

        }
        return feedFragmentBinding.root
    }







//       activityMainBinding.cancelEdit.setOnClickListener {
//            viewModel.clearEdit()
//           activityMainBinding.content.setText("")
//           activityMainBinding.group.visibility = View.GONE
//        }




    }











