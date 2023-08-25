package com.example.diary.navigation

sealed class Screen(val route: String) {

    object Authentication : Screen(route = "authentication")
    object Home : Screen(route = "home")
    object Details : Screen(route = "details?diaryId={diaryId}") {
        fun passDiaryId(id: String?) = "details?diaryId=$id"
    }
}
