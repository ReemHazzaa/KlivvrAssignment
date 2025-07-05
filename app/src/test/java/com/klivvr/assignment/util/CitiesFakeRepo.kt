package com.klivvr.assignment.util

import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.data.models.Coordinates

val mockCitiesList: List<City>
    get() = listOf(
        cityLondon,
        cityLodz,
        cityLosAngeles,
        cityLome,
        citySpringfieldUS,
        citySpringfieldCA
    )

val cityLondon = City(
    id = 1,
    name = "London",
    country = "UK",
    coordinates = Coordinates(lon = -0.1276, lat = 51.5074)
)

val cityLodz = City(
    id = 2,
    name = "Lodz",
    country = "Poland",
    coordinates = Coordinates(lon = 19.4550, lat = 51.7592)
)

val cityLosAngeles = City(
    id = 3,
    name = "Los Angeles",
    country = "USA",
    coordinates = Coordinates(lon = -118.2437, lat = 34.0522)
)

val cityLome = City(
    id = 4,
    name = "Lome",
    country = "Togo",
    coordinates = Coordinates(lon = 1.2074, lat = 6.1319)
)

val citySpringfieldUS = City(
    id = 5,
    name = "Springfield",
    country = "USA",
    coordinates = Coordinates(lon = -93.2923, lat = 37.2153)
)

val citySpringfieldCA = City(
    id = 6,
    name = "Springfield",
    country = "Canada",
    coordinates = Coordinates(lon = -80.5555, lat = 43.2890)
)
