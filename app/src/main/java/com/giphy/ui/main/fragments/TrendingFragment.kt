package com.giphy.ui.main.fragments

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.giphy.R
import com.giphy.databinding.FragmentTrendingBinding
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.GiphyLoadingProvider
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import com.giphy.sdk.ui.views.GiphyGridView
import com.giphy.ui.utils.TrendingConfig
import com.giphy.ui.utils.LoadingDrawable
import timber.log.Timber

class TrendingFragment : Fragment() {

    private var _binding: FragmentTrendingBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Giphy.configure(requireContext(), "gpI75qPdfpZJ2IAT53aW4RN0UDveGjT4")
        _binding = FragmentTrendingBinding.inflate(inflater, container, true)
        val root = binding.root

        binding.apply {
            gifsGridView.direction = TrendingConfig.direction
            gifsGridView.spanCount = TrendingConfig.spanCount
            gifsGridView.cellPadding = TrendingConfig.cellPadding
            gifsGridView.fixedSizeCells = TrendingConfig.fixedSizeCells
            gifsGridView.showCheckeredBackground = TrendingConfig.showCheckeredBackground
            setTrendingQuery()
            if (TrendingConfig.mediaType == MediaType.emoji) {
                searchInput.isEnabled = false
                searchBtn.isEnabled = false
            }
        }

        if (TrendingConfig.direction == GiphyGridView.HORIZONTAL) {
            // Limit height
            val constraints = ConstraintSet()
            constraints.clone(binding.parentView)
            constraints.clear(R.id.gifsGridView, ConstraintSet.BOTTOM)
            constraints.constrainHeight(R.id.gifsGridView, 200 * TrendingConfig.spanCount)
            constraints.applyTo(binding.parentView)
        }

        binding.gifsGridView.callback = object : GPHGridCallback {
            override fun contentDidUpdate(resultCount: Int) {
                Timber.d("contentDidUpdate $resultCount")
            }

            override fun didSelectMedia(media: Media) {
                Timber.d("didSelectMedia ${media.id}")
                Toast.makeText(
                    requireContext(),
                    "media selected: ${media.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.gifsGridView.searchCallback = object : GPHSearchGridCallback {
            override fun didTapUsername(username: String) {
                Timber.d("didTapUsername $username")
            }

            override fun didLongPressCell(cell: GifView) {
                Timber.d("didLongPressCell")
            }

            override fun didScroll(dx: Int, dy: Int) {
                Timber.d("didScroll")
            }
        }

        binding.searchBtn.setOnClickListener {
            dismissKeyboard()
            performCustomSearch()
        }

        binding.gifsGridView.setGiphyLoadingProvider(loadingProviderClient)

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_GO) {
                dismissKeyboard()
                performCustomSearch()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                performCustomSearch()
            }
        })

        return root
    }

    private fun performCustomSearch() {
        if (binding.searchInput.text.isNullOrEmpty())
            setTrendingQuery()
        else
            binding.gifsGridView.content =
                GPHContent.searchQuery(binding.searchInput.text.toString(), TrendingConfig.mediaType)
    }

    fun dismissKeyboard() {
        val imm = binding.root.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    private fun setTrendingQuery() {
        binding.gifsGridView.content = when (TrendingConfig.contentType) {
            GPHContentType.clips -> GPHContent.trendingVideos
            GPHContentType.gif -> GPHContent.trendingGifs
            GPHContentType.sticker -> GPHContent.trendingStickers
            GPHContentType.text -> GPHContent.trendingText
            GPHContentType.emoji -> GPHContent.emoji
            GPHContentType.recents -> GPHContent.recents
            else -> throw Exception("MediaType ${TrendingConfig.mediaType} not supported ")
        }
    }

    private val loadingProviderClient = object : GiphyLoadingProvider {
        override fun getLoadingDrawable(position: Int): Drawable {
            return LoadingDrawable(if (position % 2 == 0) LoadingDrawable.Shape.Rect else LoadingDrawable.Shape.Circle)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): TrendingFragment {
            return TrendingFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}