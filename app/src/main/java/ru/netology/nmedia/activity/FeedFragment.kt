package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.activity.ImageFragment.Companion.attachUrl
import ru.netology.nmedia.adapter.PostLoadingStateAdapter


@AndroidEntryPoint
class FeedFragment : Fragment() {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: PostViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
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

            }

        )
        feedFragmentBinding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter{ adapter.retry() },
            footer = PostLoadingStateAdapter { adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest (adapter::submitData)
                }
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    feedFragmentBinding.swipeRefresh?.isRefreshing =
                        state.refresh is LoadState.Loading

                }
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            feedFragmentBinding.swipeRefresh?.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(feedFragmentBinding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }
        adapter.registerAdapterDataObserver(object  :RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        feedFragmentBinding.list.smoothScrollToPosition(0)
                    }
            }
        })

        feedFragmentBinding.swipeRefresh?.setOnRefreshListener (adapter::refresh)

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











