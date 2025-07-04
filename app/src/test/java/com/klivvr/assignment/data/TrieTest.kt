package com.klivvr.assignment.data

import com.klivvr.assignment.cityLodz
import com.klivvr.assignment.cityLome
import com.klivvr.assignment.cityLondon
import com.klivvr.assignment.cityLosAngeles
import com.klivvr.assignment.citySpringfieldCA
import com.klivvr.assignment.citySpringfieldUS
import org.junit.Assert.*
import org.junit.Before
import com.klivvr.assignment.mockCitiesList
import org.junit.After
import org.junit.Test

class TrieTest {

    private lateinit var trie: Trie

    @Before
    fun setUp() {
        trie = Trie()
        mockCitiesList.forEach { trie.insert(it) }
    }

    @After
    fun tearDown() {
        trie.clear()
    }

    @Test
    fun `search returns correct cities for valid prefix`() {
        val results = trie.search("Lo")
        assertEquals(4, results.size)
        assertTrue(results.contains(cityLondon))
        assertTrue(results.contains(cityLosAngeles))
        assertTrue(results.contains(cityLome))
        assertTrue(results.contains(cityLodz))
    }

    @Test
    fun `search is case-insensitive`() {
        val results = trie.search("loS")
        assertEquals(1, results.size)
        assertTrue(results.contains(cityLosAngeles))
    }

    @Test
    fun `search returns both cities with same name`() {
        val results = trie.search("spring")
        assertEquals(2, results.size)
        assertTrue(results.contains(citySpringfieldUS))
        assertTrue(results.contains(citySpringfieldCA))
    }

    @Test
    fun `search returns empty list for unmatched prefix`() {
        val results = trie.search("Zur")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `insert adds cities correctly and search returns them`() {
        val newCity = City(
            country = "Tanzania",
            name = "Zanzibar",
            id = 300,
            coordinates =
                Coordinates(
                    lon = 39.19793,
                    lat = -6.16394
                )
        )
        trie.insert(newCity)
        val results = trie.search("Zan")
        assertEquals(1, results.size)
        assertEquals(newCity, results.first())
    }

    @Test
    fun `insert handles city names with special characters`() {
        val cityWithAccents = City(
            country = "France",
            name = "São Paulo",
            id = 100,
            coordinates = Coordinates(-46.6333, -23.5505)
        )

        val cityWithHyphen = City(
            country = "France",
            name = "Saint-Denis",
            id = 101,
            coordinates = Coordinates(2.3536, 48.9362)
        )

        val cityWithApostrophe = City(
            country = "Ireland",
            name = "Dún Laoghaire",
            id = 102,
            coordinates = Coordinates(-6.1358, 53.2936)
        )

        trie.insert(cityWithAccents)
        trie.insert(cityWithHyphen)
        trie.insert(cityWithApostrophe)

        // Search by prefix that includes special characters
        val result1 = trie.search("São")
        val result2 = trie.search("Saint-")
        val result3 = trie.search("Dún")

        assertEquals(1, result1.size)
        assertEquals(cityWithAccents, result1.first())

        assertEquals(1, result2.size)
        assertEquals(cityWithHyphen, result2.first())

        assertEquals(1, result3.size)
        assertEquals(cityWithApostrophe, result3.first())
    }

    @Test
    fun `search for exact match returns correct cities`() {
        val results = trie.search("London")
        assertEquals(1, results.size)
        assertEquals(cityLondon, results[0])
    }

    @Test
    fun `search for empty prefix returns all cities`() {
        val results = trie.search("")
        assertTrue(results.size >= 6)
        assertTrue(results.contains(cityLondon))
        assertTrue(results.contains(cityLodz))
        assertTrue(results.contains(cityLosAngeles))
    }

    @Test
    fun `clear removes all cities from trie`() {
        trie.clear()
        val results = trie.search("Lo")
        assertTrue(results.isEmpty())
    }

}