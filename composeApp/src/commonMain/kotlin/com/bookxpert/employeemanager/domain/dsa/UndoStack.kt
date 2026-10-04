package com.bookxpert.employeemanager.domain.dsa

import com.bookxpert.employeemanager.domain.model.Employee

/**
 * Stack for managing undo operations on deleted employees.
 * Time complexity: O(1) push / pop
 * Space complexity: O(k) bounded to max depth (default 10)
 */
class UndoStack(
    private val maxCapacity: Int = 10
) {
    private val stack = ArrayDeque<Employee>()

    val size: Int get() = stack.size
    val canUndo: Boolean get() = stack.isNotEmpty()

    // Push deleted record onto stack; drops oldest record if exceeds depth
    fun push(employee: Employee) {
        if (stack.size >= maxCapacity) {
            stack.removeFirst()
        }
        stack.addLast(employee)
    }

    // Pop most recently deleted record for re-insertion
    fun pop(): Employee? {
        return if (stack.isNotEmpty()) stack.removeLast() else null
    }

    fun peek(): Employee? = stack.lastOrNull()

    fun clear() {
        stack.clear()
    }
}
