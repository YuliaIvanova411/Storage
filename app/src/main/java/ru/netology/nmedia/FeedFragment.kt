package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {

    val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val feedFragmentBinding = FragmentFeedBinding.inflate(layoutInflater, container, false)

        val adapter = PostAdapter(
            object : PostListener {
                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    val text = post.content
                    val bundle = Bundle()
                    bundle.putString("editedText", text)
                    findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)

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
                    viewModel.shareById(post.id)

                }

            }

        )
        feedFragmentBinding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            feedFragmentBinding.progress?.isVisible = state.loading
            feedFragmentBinding.errorGroup?.isVisible = state.error
            feedFragmentBinding.emptyText?.isVisible = state.empty
        }

        feedFragmentBinding.retryButton?.setOnClickListener {
            viewModel.loadPosts()
        }
        feedFragmentBinding.swipeRefresh?.setOnRefreshListener {
            viewModel.loadPosts()
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











