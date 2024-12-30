package com.wngud.locationalarm.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wngud.locationalarm.data.db.remote.NaverApiService
import com.wngud.locationalarm.data.db.remote.NaverSearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val naverApiService: NaverApiService
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<NaverSearchItem>>(emptyList())
    val searchResults: StateFlow<List<NaverSearchItem>> get() = _searchResults

    fun searchLocation(query: String) {
        viewModelScope.launch {
            try {
                val response = naverApiService.searchLocation(query)
                _searchResults.value = response.items.map { item ->
                    item.copy(title = cleanTitle(item.title))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun cleanTitle(title: String): String {
        return title.replace(Regex("<.*?>"), "") // HTML 태그 제거
    }
}