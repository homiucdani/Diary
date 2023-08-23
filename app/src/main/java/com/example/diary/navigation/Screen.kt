package com.example.diary.navigation

sealed class Screen(val route: String) {

    object Authentication : Screen(route = "authentication")
    object Home : Screen(route = "home")
    object Write : Screen(route = "write?diaryId={diaryId}") {
        fun passDiaryId(id: String?) = "write?diaryId=$id"
    }
}
