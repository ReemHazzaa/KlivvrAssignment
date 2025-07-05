package com.klivvr.assignment.data

/**
 * A node in the Trie data structure.
 * @property children A map of characters to their corresponding TrieNode.
 * @property citiesAtNode A list of cities that end at this node, allowing for multiple cities with the same name.
 */
class TrieNode {
    val children: MutableMap<Char, TrieNode> = mutableMapOf()
    val citiesAtNode: MutableList<City> = mutableListOf()
}

/**
 * Trie (Prefix Tree) implementation for fast, case-insensitive prefix searching of city names.
 * This data structure is chosen because its search time complexity is O(k), where k is the
 * length of the prefix. This is significantly more efficient than a linear scan (O(n))
 * on a large dataset of 200,000 cities, directly addressing the assignment's performance requirement.
 */
class Trie {
    private val root = TrieNode()

    /**
     * Inserts a city into the Trie. The city name is treated as case-insensitive.
     * It supports cities with:
     * 1. Same name
     * 2. Same prefix
     * 3. Special characters (as long as they're individual Chars).
     */
    fun insert(city: City) {
        var currentNode = root
        // Converts the city name to lowercase to make the search case-insensitive.
        val name = city.name.lowercase()
        // Iterating over each character:
        // If the character doesn't exist as a child node, create a new TrieNode.
        // Move down to the next node.
        for (char in name) {
            currentNode = currentNode.children.getOrPut(char) { TrieNode() }
        }
        // When the full name is inserted, store the City object at the final node in citiesAtNode.
        currentNode.citiesAtNode.add(city)
    }

    /**
     * Searches the Trie for all cities matching the given prefix.
     * The search is case-insensitive.
     * This implementation makes search time complexity O(k), where k = length of the prefix.
     * That’s much faster than O(n) if we loop through all cities manually.
     * @param prefix The string to search for.
     * @return A list of all cities that start with the given prefix.
     */
    fun search(prefix: String): List<City> {
        var currentNode = root
        // Convert the input prefix to lowercase(Perform search in lowercase).
        val lowerCasePrefix = prefix.lowercase()
        // Walk down the Trie according to each character in the prefix:
        // If a character isn't found, return an empty list.
        for (char in lowerCasePrefix) {
            currentNode = currentNode.children[char] ?: return emptyList()
        }
        // Once the node for the last character in the prefix is reached:
        // Call findAllCitiesFromNode() to collect all matching cities below it (depth-first traversal).
        return findAllCitiesFromNode(currentNode)
    }

    /**
     * This function recursively collects all cities that are either:
     * - Exactly at the prefix node
     * - Or deeper (longer names that still match the prefix)
     *
     * It uses a breadth-first traversal via a queue (ArrayDeque)
     * to walk through all descendant nodes and gathers their citiesAtNode entries.
     *
     * Example:
     * If we insert:
     * "London" → adds L → O → N → D → O → N nodes, then stores the City at the last node.
     * "Lodz" → shares L → O, then diverges into D → Z
     *
     * Then:
     * ```
     * trie.search("Lo")
     * ```
     * Will return both London and Lodz, since both start with "Lo".
     */
    private fun findAllCitiesFromNode(node: TrieNode): List<City> {
        val results = mutableListOf<City>()
        val queue = ArrayDeque<TrieNode>()
        queue.add(node)

        while (queue.isNotEmpty()) {
            val currentNode = queue.removeFirst()
            results.addAll(currentNode.citiesAtNode)
            queue.addAll(currentNode.children.values.toList())
        }
        return results
    }

    /**
     * Clears all entries in the Trie.
     *
     * This operation removes all child nodes and cities stored in the Trie by
     * clearing the root node's children and associated city list.
     * After calling this method, the Trie will behave as if no cities were inserted.
     *
     * Time complexity: O(1) — all nodes become unreachable and will be garbage-collected.
     */
    fun clear() {
        // Reset the root node, dropping all children and stored cities
        root.children.clear()
        root.citiesAtNode.clear()
    }
}