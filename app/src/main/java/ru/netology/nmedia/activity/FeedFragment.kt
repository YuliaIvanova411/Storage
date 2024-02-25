package ru.netology.nmedia.activity

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.activity.ImageFragment.Companion.attachUrl


class FeedFragment : Fragment() {

    val viewModel: PostViewModel by activityViewModels()
    @SuppressLint("SuspiciousIndentation")
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
                    if (viewModel.isAuthorized(childFragmentManager)) {
                        viewModel.likeById(post.id)}
                    else findNavController()
                        .navigate(R.id.loginFragment)
               }

                override fun onAttachment(post: Post) {
                    if (post.attachment != null) {
                        findNavController().navigate(
                            R.id.action_feedFragment_to_imageFragment,
                            Bundle().apply { attachUrl = post.attachment.url }
                        )
                    }
                }

//                override fun onShare(post: Post) {
//                    val intent = Intent().apply {
//                        action = Intent.ACTION_SEND
//                        putExtra(Intent.EXTRA_TEXT, post.content)
//                        type = "text/plain"
//                    }
//                    val startIntent =
//                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                    startActivity(startIntent)
//                    viewModel.shareById(post.id)
//
//                }

            }

        )
        feedFragmentBinding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, { state : FeedModel ->
            adapter.submitList(state.posts)
            feedFragmentBinding.progress?.isVisible = state.loading
            feedFragmentBinding.errorGroup?.isVisible = state.error
            feedFragmentBinding.emptyText?.isVisible = state.empty
            feedFragmentBinding.internetErrorGroup?.isVisible = state.connectError
            feedFragmentBinding.swipeRefresh?.isRefreshing = state.loading
        })

        adapter.registerAdapterDataObserver(object  :RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        feedFragmentBinding.list.smoothScrollToPosition(0)
                    }
            }
        })

        feedFragmentBinding.retryButton?.setOnClickListener {
            viewModel.loadPosts()
        }
        feedFragmentBinding.swipeRefresh?.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it != 0) {
                feedFragmentBinding.loadNew?.visibility = View.VISIBLE
            }
        }
        feedFragmentBinding.loadNew?.setOnClickListener {
            viewModel.readNew()
            feedFragmentBinding.loadNew.visibility = View.GONE
        }


        feedFragmentBinding.add.setOnClickListener {
            if (viewModel.isAuthorized(childFragmentManager)) {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)}
            else findNavController()
                .navigate(R.id.loginFragment)

        }

        return feedFragmentBinding.root
    }












    }











