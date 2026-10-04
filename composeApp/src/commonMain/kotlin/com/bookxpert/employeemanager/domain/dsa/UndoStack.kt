package com.bookxpert.employeemanager.domain.dsa

import com.bookxpert.employeemanager.domain.model.Employee

/**
 * Stack to undo deleted employees
 * Time complexity: O(1) push and pop
 * Space complexity: O(k) bounded by max depth
 */
class UndoStack(
    private val maxCapacity: Int = 10
) {
    private val stack = ArrayDeque<Employee>()

    val size: Int get() = stack.size
    val canUndo: Boolean get() = stack.isNotEmpty()

    // Add deleted employee to stack
    fun push(employee: Employee) {
        if (stack.size >= maxCapacity) {
            stack.removeFirst()
        }
        stack.addLast(employee)
    }

    // Get last deleted employee
    fun pop(): Employee? {
        return if (stack.isNotEmpty()) stack.removeLast() else null
    }

    fun peek(): Employee? = stack.lastOrNull()

    fun clear() {
        stack.clear()
    }
}
