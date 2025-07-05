package com.klivvr.assignment.ui.screens.city.models

import com.klivvr.assignment.data.models.City

sealed class UiModel {
    data class CityItem(val city: City) : UiModel()
    data class HeaderItem(val letter: Char) : UiModel()
}