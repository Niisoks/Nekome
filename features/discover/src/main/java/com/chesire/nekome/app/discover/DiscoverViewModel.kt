package com.chesire.nekome.app.discover

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chesire.nekome.core.Resource
import com.chesire.nekome.core.extensions.postError
import com.chesire.nekome.core.extensions.postSuccess
import com.chesire.nekome.core.flags.AsyncState
import com.chesire.nekome.core.flags.SeriesStatus
import com.chesire.nekome.core.flags.UserSeriesStatus
import com.chesire.nekome.core.models.ImageModel
import com.chesire.nekome.core.models.SeriesModel
import com.chesire.nekome.trending.api.TrendingApi
import com.chesire.nekome.trending.api.TrendingEntity
import kotlinx.coroutines.launch

/**
 * ViewModel to aid with performing series discovery.
 */
class DiscoverViewModel @ViewModelInject constructor(
    private val trending: TrendingApi
) : ViewModel() {

    private val _trendingAnime by lazy {
        val trendingData =
            MutableLiveData<AsyncState<List<SeriesModel>, DiscoverError>>(AsyncState.Loading())
        viewModelScope.launch {
            when (val animeList = trending.getTrendingAnime()) {
                is Resource.Success -> trendingData.postSuccess(
                    animeList.data.map { it.toSeriesModel() }
                )
                is Resource.Error -> trendingData.postError(DiscoverError.Error)
            }
        }
        return@lazy trendingData
    }

    private val _trendingManga by lazy {
        val trendingData =
            MutableLiveData<AsyncState<List<SeriesModel>, DiscoverError>>(AsyncState.Loading())
        viewModelScope.launch {
            when (val mangaList = trending.getTrendingManga()) {
                is Resource.Success -> trendingData.postSuccess(
                    mangaList.data.map { it.toSeriesModel() }
                )
                is Resource.Error -> trendingData.postError(DiscoverError.Error)
            }
        }
        return@lazy trendingData
    }

    /**
     * Get the current trending Anime series.
     */
    val trendingAnime: LiveData<AsyncState<List<SeriesModel>, DiscoverError>>
        get() = _trendingAnime

    /**
     * Get the current trending Manga series.
     */
    val trendingManga: LiveData<AsyncState<List<SeriesModel>, DiscoverError>>
        get() = _trendingManga

    private fun TrendingEntity.toSeriesModel(): SeriesModel = SeriesModel(
        id,
        0,
        type,
        subtype,
        slug,
        synopsis,
        canonicalTitle,
        SeriesStatus.Unknown,
        UserSeriesStatus.Unknown,
        0,
        0,
        posterImage ?: ImageModel.empty,
        coverImage ?: ImageModel.empty,
        nsfw,
        startDate ?: "",
        endDate ?: ""
    )
}
