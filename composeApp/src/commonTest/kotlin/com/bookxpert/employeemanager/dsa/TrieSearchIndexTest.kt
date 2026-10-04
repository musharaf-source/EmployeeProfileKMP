package com.bookxpert.employeemanager.dsa

import com.bookxpert.employeemanager.domain.dsa.TrieSearchIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrieSearchIndexTest {

    @Test
    fun searchPrefix_matchesTokensFromDifferentFields() {
        val trie = TrieSearchIndex()
        trie.insert(1L, "Musharaf Mohammad", "musharaf@bookxpert.com", "Engineering")
        trie.insert(2L, "Suresh Raina", "suresh@cricket.com", "Design")
        trie.insert(3L, "Mohammed Ali", "ali@boxing.com", "Sales")

        val nameMatches = trie.searchPrefix("Mush")
        assertEquals(setOf(1L), nameMatches)

        val mohammedMatches = trie.searchPrefix("Mohamm")
        assertEquals(setOf(1L, 3L), mohammedMatches)

        val deptMatches = trie.searchPrefix("Engine")
        assertEquals(setOf(1L), deptMatches)

        val emptyMatches = trie.searchPrefix("xyz123")
        assertTrue(emptyMatches.isEmpty())
    }
}
