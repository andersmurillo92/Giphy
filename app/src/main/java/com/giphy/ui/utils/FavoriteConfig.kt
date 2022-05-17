package com.giphy.ui.utils

import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.views.GiphyGridView

object FavoriteConfig {
    var spanCount = 2
    var cellPadding = 32
    var mediaType = MediaType.gif
    var contentType = GPHContentType.recents
    var direction = GiphyGridView.VERTICAL
    var fixedSizeCells = false
    var showCheckeredBackground = true
}
