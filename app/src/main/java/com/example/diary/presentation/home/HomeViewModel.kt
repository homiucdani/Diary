package com.example.diary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mongoDb: App
) : ViewModel() {

    fun signOut() {
        viewModelScope.launch {
            mongoDb.currentUser?.logOut()
        }
    }
}