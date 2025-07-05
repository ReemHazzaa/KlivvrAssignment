package com.klivvr.assignment.data.search

import com.klivvr.assignment.util.cityLodz
import com.klivvr.assignment.util.cityLome
import com.klivvr.assignment.util.cityLondon
import com.klivvr.assignment.util.cityLosAngeles
import com.klivvr.assignment.util.citySpringfieldCA
import com.klivvr.assignment.util.citySpringfieldUS
import com.klivvr.assignment.data.models.City
import com.klivvr.assignment.data.models.Coordinates
import com.klivvr.assignment.util.mockCitiesList
import org.junit.After
import org.junit.Assert
import org.junit.Before
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
        Assert.assertEquals(4, results.size)
        Assert.assertTrue(results.contains(cityLondon))
        Assert.assertTrue(results.contains(cityLosAngeles))
        Assert.assertTrue(results.contains(cityLome))
        Assert.assertTrue(results.contains(cityLodz))
    }

    @Test
    fun `search is case-insensitive`() {
        val results = trie.search("loS")
        Assert.assertEquals(1, results.size)
        Assert.assertTrue(results.contains(cityLosAngeles))
    }

    @Test
    fun `search returns both cities with same name`() {
        val results = trie.search("spring")
        Assert.assertEquals(2, results.size)
        Assert.assertTrue(results.contains(citySpringfieldUS))
        Assert.assertTrue(results.contains(citySpringfieldCA))
    }

    @Test
    fun `search returns empty list for unmatched prefix`() {
        val results = trie.search("Zur")
        Assert.assertTrue(results.isEmpty())
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
        Assert.assertEquals(1, results.size)
        Assert.assertEquals(newCity, results.first())
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

        Assert.assertEquals(1, result1.size)
        Assert.assertEquals(cityWithAccents, result1.first())

        Assert.assertEquals(1, result2.size)
        Assert.assertEquals(cityWithHyphen, result2.first())

        Assert.assertEquals(1, result3.size)
        Assert.assertEquals(cityWithApostrophe, result3.first())
    }

    @Test
    fun `search for exact match returns correct cities`() {
        val results = trie.search("London")
        Assert.assertEquals(1, results.size)
        Assert.assertEquals(cityLondon, results[0])
    }

    @Test
    fun `search for empty prefix returns all cities`() {
        val results = trie.search("")
        Assert.assertTrue(results.size >= 6)
        Assert.assertTrue(results.contains(cityLondon))
        Assert.assertTrue(results.contains(cityLodz))
        Assert.assertTrue(results.contains(cityLosAngeles))
    }

    @Test
    fun `clear removes all cities from trie`() {
        trie.clear()
        val results = trie.search("Lo")
        Assert.assertTrue(results.isEmpty())
    }

}