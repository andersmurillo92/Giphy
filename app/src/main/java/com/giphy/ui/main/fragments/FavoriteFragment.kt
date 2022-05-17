package com.giphy.ui.main.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.giphy.databinding.FragmentFavoriteBinding
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.GiphyLoadingProvider
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.ui.utils.FavoriteConfig
import com.giphy.ui.utils.LoadingDrawable

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Giphy.configure(requireContext(), "gpI75qPdfpZJ2IAT53aW4RN0UDveGjT4")
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.apply {
            gifsGridView.direction = FavoriteConfig.direction
            gifsGridView.spanCount = FavoriteConfig.spanCount
            gifsGridView.cellPadding = FavoriteConfig.cellPadding
            gifsGridView.fixedSizeCells = FavoriteConfig.fixedSizeCells
            gifsGridView.showCheckeredBackground = FavoriteConfig.showCheckeredBackground
        }

        binding.gifsGridView.setGiphyLoadingProvider(loadingProviderClient)

        return root
    }

    private val loadingProviderClient = object : GiphyLoadingProvider {
        override fun getLoadingDrawable(position: Int): Drawable {
            return LoadingDrawable(if (position % 2 == 0) LoadingDrawable.Shape.Rect else LoadingDrawable.Shape.Circle)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        setTrendingQuery()
    }

    private fun setTrendingQuery() {
        binding.gifsGridView.content = when (FavoriteConfig.contentType) {
            GPHContentType.clips -> GPHContent.trendingVideos
            GPHContentType.gif -> GPHContent.trendingGifs
            GPHContentType.sticker -> GPHContent.trendingStickers
            GPHContentType.text -> GPHContent.trendingText
            GPHContentType.emoji -> GPHContent.emoji
            GPHContentType.recents -> GPHContent.recents
            else -> throw Exception("MediaType ${FavoriteConfig.mediaType} not supported ")
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}