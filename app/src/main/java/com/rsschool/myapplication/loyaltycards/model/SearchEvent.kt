package com.rsschool.myapplication.loyaltycards.model

sealed class SearchEvent {
    class SearchQueryInput(val input: String): SearchEvent()
}
