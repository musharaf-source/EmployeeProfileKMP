package com.bookxpert.employeemanager.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.EmploymentType

enum class ActiveStatusFilter(val label: String) {
    ALL("All Status"),
    ACTIVE_ONLY("Active Only"),
    INACTIVE_ONLY("Inactive Only")
}

data class FilterCriteria(
    val selectedDepartments: Set<Department> = emptySet(),
    val statusFilter: ActiveStatusFilter = ActiveStatusFilter.ALL,
    val selectedEmploymentTypes: Set<EmploymentType> = emptySet()
) {
    val activeFilterCount: Int
        get() = selectedDepartments.size +
                (if (statusFilter != ActiveStatusFilter.ALL) 1 else 0) +
                selectedEmploymentTypes.size

    val isFilterActive: Boolean
        get() = activeFilterCount > 0
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterCriteria: FilterCriteria,
    onFilterChange: (FilterCriteria) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Employees",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (filterCriteria.isFilterActive) {
                    TextButton(onClick = { onFilterChange(FilterCriteria()) }) {
                        Text("Clear All")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Department Multi-Select Chips
            Text(
                text = "Department",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Department.entries.forEach { dept ->
                    val isSelected = filterCriteria.selectedDepartments.contains(dept)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSet = if (isSelected) {
                                filterCriteria.selectedDepartments - dept
                            } else {
                                filterCriteria.selectedDepartments + dept
                            }
                            onFilterChange(filterCriteria.copy(selectedDepartments = newSet))
                        },
                        label = { Text(dept.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Filter Chips
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActiveStatusFilter.entries.forEach { status ->
                    val isSelected = filterCriteria.statusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(filterCriteria.copy(statusFilter = status)) },
                        label = { Text(status.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Employment Type Multi-Select
            Text(
                text = "Employment Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EmploymentType.entries.forEach { type ->
                    val isSelected = filterCriteria.selectedEmploymentTypes.contains(type)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSet = if (isSelected) {
                                filterCriteria.selectedEmploymentTypes - type
                            } else {
                                filterCriteria.selectedEmploymentTypes + type
                            }
                            onFilterChange(filterCriteria.copy(selectedEmploymentTypes = newSet))
                        },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Apply Filters (${filterCriteria.activeFilterCount})",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
