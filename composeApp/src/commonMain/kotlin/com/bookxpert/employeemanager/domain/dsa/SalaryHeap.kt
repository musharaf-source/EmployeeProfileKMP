package com.bookxpert.employeemanager.domain.dsa

import com.bookxpert.employeemanager.domain.model.Employee

/**
 * Pure Kotlin Min-Heap implementation to support top-N queries across Android and iOS
 * without depending on java.util.PriorityQueue.
 */
class MinHeap<T>(
    private val capacity: Int,
    private val comparator: Comparator<T>
) {
    private val heap = ArrayList<T>(capacity)

    val size: Int get() = heap.size

    fun peek(): T? = heap.firstOrNull()

    fun poll(): T? {
        if (heap.isEmpty()) return null
        val root = heap[0]
        val lastItem = heap.removeAt(heap.size - 1)
        if (heap.isNotEmpty()) {
            heap[0] = lastItem
            siftDown(0)
        }
        return root
    }

    fun add(element: T) {
        heap.add(element)
        siftUp(heap.size - 1)
    }

    fun toList(): List<T> = ArrayList(heap)

    private fun siftUp(index: Int) {
        var current = index
        while (current > 0) {
            val parent = (current - 1) / 2
            if (comparator.compare(heap[current], heap[parent]) < 0) {
                swap(current, parent)
                current = parent
            } else {
                break
            }
        }
    }

    private fun siftDown(index: Int) {
        var current = index
        val half = heap.size / 2
        while (current < half) {
            var child = 2 * current + 1
            val right = child + 1
            if (right < heap.size && comparator.compare(heap[child], heap[right]) > 0) {
                child = right
            }
            if (comparator.compare(heap[current], heap[child]) <= 0) {
                break
            }
            swap(current, child)
            current = child
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
    }
}

/**
 * Finds the top [n] employees by salary using a bounded Min-Heap.
 *
 * Algorithmic Details:
 * - Time Complexity: O(M * log N) where M is the total employee count and N is the top count.
 * - Space Complexity: O(N) auxiliary space — the heap retains at most N elements in memory.
 *
 * Engineering Rationale:
 * A full collection sort runs in O(M * log M) time and O(M) space. When N is small relative
 * to M (e.g., top 5 out of 10,000 employees), iterating with a min-heap of size N avoids
 * sorting the entire dataset, saving both CPU cycles and memory allocations.
 */
fun topNBySalary(employees: List<Employee>, n: Int = 5): List<Employee> {
    if (n <= 0 || employees.isEmpty()) return emptyList()

    val minHeap = MinHeap<Employee>(capacity = n, comparator = compareBy { it.salary })

    for (emp in employees) {
        if (minHeap.size < n) {
            minHeap.add(emp)
        } else {
            val currentMin = minHeap.peek()
            if (currentMin != null && emp.salary > currentMin.salary) {
                minHeap.poll()
                minHeap.add(emp)
            }
        }
    }

    return minHeap.toList().sortedByDescending { it.salary }
}
