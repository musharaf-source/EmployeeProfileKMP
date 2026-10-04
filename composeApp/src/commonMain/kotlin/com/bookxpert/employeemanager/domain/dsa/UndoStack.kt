package com.bookxpert.employeemanager.domain.dsa

import com.bookxpert.employeemanager.domain.model.Employee

/**
 * LIFO stack structure for managing employee deletion undos.
 *
 * Algorithmic Details:
 * - push: O(1) amortized
 * - pop: O(1)
 * - Space Complexity: O(K) bounded by [maxCapacity] (default = 10).
 */
class UndoStack(
    private val maxCapacity: Int = 10
) {
    private val stack = ArrayDeque<Employee>()

    val size: Int get() = stack.size
    val canUndo: Boolean get() = stack.isNotEmpty()

    /**
     * Pushes a deleted employee record. Drops oldest item when maxCapacity is exceeded.
     */
    fun push(employee: Employee) {
        if (stack.size >= maxCapacity) {
            stack.removeFirst()
        }
        stack.addLast(employee)
    }

    /**
     * Pops and returns the most recently deleted employee record.
     */
    fun pop(): Employee? {
        return if (stack.isNotEmpty()) stack.removeLast() else null
    }

    fun peek(): Employee? = stack.lastOrNull()

    fun clear() {
        stack.clear()
    }
}
