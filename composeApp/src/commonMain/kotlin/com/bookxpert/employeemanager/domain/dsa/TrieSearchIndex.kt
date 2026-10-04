package com.bookxpert.employeemanager.domain.dsa

/**
 * Prefix Trie node representing character transitions.
 */
class TrieNode {
    val children = HashMap<Char, TrieNode>()
    val employeeIds = HashSet<Long>()
}

/**
 * In-memory Prefix Trie for fast full-text prefix lookups across Name, Email, and Department.
 *
 * Complexity:
 * - Insert word: O(L) where L is token length.
 * - Search prefix: O(P) where P is query prefix length.
 * - Space: O(total unique characters across tokens).
 */
class TrieSearchIndex {

    private val root = TrieNode()

    fun clear() {
        root.children.clear()
        root.employeeIds.clear()
    }

    /**
     * Inserts tokens for an employee record (tokens from Name, Email, Department).
     */
    fun insert(employeeId: Long, vararg texts: String) {
        for (text in texts) {
            val tokens = text.lowercase().split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }
            for (token in tokens) {
                insertToken(token, employeeId)
            }
        }
    }

    private fun insertToken(token: String, id: Long) {
        var current = root
        current.employeeIds.add(id)
        for (ch in token) {
            val next = current.children.getOrPut(ch) { TrieNode() }
            next.employeeIds.add(id)
            current = next
        }
    }

    /**
     * Returns matching employee IDs for a given search query prefix.
     */
    fun searchPrefix(query: String): Set<Long> {
        val clean = query.trim().lowercase()
        if (clean.isEmpty()) return emptySet()

        val tokens = clean.split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return emptySet()

        var resultSet: Set<Long>? = null

        for (token in tokens) {
            var current: TrieNode? = root
            for (ch in token) {
                current = current?.children?.get(ch)
                if (current == null) break
            }
            val matches = current?.employeeIds ?: emptySet()
            resultSet = if (resultSet == null) {
                matches
            } else {
                resultSet.intersect(matches)
            }
            if (resultSet.isEmpty()) break
        }

        return resultSet ?: emptySet()
    }
}
