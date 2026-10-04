package com.bookxpert.employeemanager.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bookxpert.employeemanager.data.local.preferences.ThemeMode
import com.bookxpert.employeemanager.data.local.preferences.ThemePreferences
import com.bookxpert.employeemanager.presentation.components.ConfirmationDialog
import com.bookxpert.employeemanager.presentation.list.components.EmployeeCard
import com.bookxpert.employeemanager.presentation.list.components.FilterBottomSheet
import com.bookxpert.employeemanager.presentation.list.components.FilterCriteria
import com.bookxpert.employeemanager.presentation.list.components.SortMenu
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToTopEarners: () -> Unit,
    viewModel: EmployeeListViewModel = koinViewModel(),
    themePreferences: ThemePreferences = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeMode by themePreferences.themeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    // Undo snackbar listener
    LaunchedEffect(uiState.showUndoSnackbar) {
        if (uiState.showUndoSnackbar && uiState.undoableEmployee != null) {
            val employeeName = uiState.undoableEmployee!!.fullName
            val result = snackbarHostState.showSnackbar(
                message = "$employeeName was deleted",
                actionLabel = "UNDO",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.dismissUndoSnackbar()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Employees",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${uiState.filteredCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Top Earners Nav Action
                    IconButton(onClick = onNavigateToTopEarners) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Top Earners",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Dark/Light Theme Toggle
                    IconButton(onClick = { themePreferences.toggleDarkMode(isDark) }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sleek Search & Filter Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = "Search employees...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onSearchQueryChanged("") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Filter button with badge
                BadgedBox(
                    badge = {
                        if (uiState.filterCriteria.isFilterActive) {
                            Badge {
                                Text("${uiState.filterCriteria.activeFilterCount}")
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = { viewModel.setFilterSheetVisible(true) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (uiState.filterCriteria.isFilterActive)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (uiState.filterCriteria.isFilterActive)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Sort Dropdown
                SortMenu(
                    currentSort = uiState.sortOption,
                    onSortSelected = { viewModel.onSortOptionChanged(it) }
                )
            }

            // Employee List Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredEmployees.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.isSearchingOrFiltering)
                                "No employees match your criteria"
                            else "No employee records yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.isSearchingOrFiltering)
                                "Try adjusting your search terms or clearing applied filters."
                            else "Get started by adding your first employee to the directory.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        if (uiState.isSearchingOrFiltering) {
                            Button(
                                onClick = {
                                    viewModel.onSearchQueryChanged("")
                                    viewModel.onFilterCriteriaChanged(FilterCriteria())
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Reset Search & Filters")
                            }
                        } else {
                            Button(
                                onClick = onNavigateToAdd,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Add First Employee")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredEmployees,
                        key = { it.id }
                    ) { employee ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut()
                        ) {
                            EmployeeCard(
                                employee = employee,
                                searchQuery = uiState.searchQuery,
                                onClick = { onNavigateToDetail(employee.id) },
                                onEdit = { onNavigateToEdit(employee.id) },
                                onDelete = { viewModel.requestDeleteConfirmation(employee) }
                            )
                        }
                    }
                }
            }
        }

        // Filter Bottom Sheet
        if (uiState.isFilterSheetVisible) {
            FilterBottomSheet(
                filterCriteria = uiState.filterCriteria,
                onFilterChange = { viewModel.onFilterCriteriaChanged(it) },
                onDismiss = { viewModel.setFilterSheetVisible(false) },
                sheetState = sheetState
            )
        }

        // Delete Confirmation Dialog
        if (uiState.employeePendingDelete != null) {
            val emp = uiState.employeePendingDelete!!
            ConfirmationDialog(
                title = "Delete Employee",
                message = "Are you sure you want to delete ${emp.fullName}? You can undo this action within 5 seconds.",
                confirmText = "Delete",
                onConfirm = { viewModel.confirmDelete() },
                onDismiss = { viewModel.dismissDeleteConfirmation() }
            )
        }
    }
}
