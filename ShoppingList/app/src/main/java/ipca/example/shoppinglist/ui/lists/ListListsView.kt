// File: ui/lists/ListListsView.kt
package ipca.example.shoppinglist.ui.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ipca.example.shoppinglist.Screen
import ipca.example.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ListListsView(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel: ListListsViewModel = viewModel()
    val state = viewModel.state.value

    // State to manage the selected list for deletion
    var selectedListId by remember { mutableStateOf<String?>(null) }
    var selectedListName by remember { mutableStateOf<String>("") }

    // State to manage the display of the confirmation dialog
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Trigger the getLists method to fetch user-specific lists
    LaunchedEffect(key1 = true) {
        viewModel.getLists()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Your Shopping Lists",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (state.listItemsList.isEmpty()) {
                    // Display a friendly message when no lists are found
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No shopping lists found. Tap the + button to add a new list.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.listItemsList) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Clickable area for navigation
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                navController.navigate(
                                                    Screen.ListItems.createRoute(item.docId ?: "")
                                                )
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = "List Icon",
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .size(24.dp)
                                        )
                                        Text(
                                            text = item.name ?: "Untitled List",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }

                                    // Delete Button
                                    IconButton(
                                        onClick = {
                                            selectedListId = item.docId
                                            selectedListName = item.name ?: "Untitled List"
                                            showDeleteConfirm = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete List",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        // Add List Floating Action Button (FAB) at Bottom End (Right)
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.AddList.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add List",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Sign Out Button at Bottom Start (Left)
        FloatingActionButton(
            onClick = {
                // Perform Sign Out
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                // Navigate to Login Screen and clear back stack
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = Color.Red
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Sign Out",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Confirmation Dialog for Deletion
        if (showDeleteConfirm && selectedListId != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirm = false
                    selectedListId = null
                },
                title = { Text(text = "Delete List") },
                text = { Text(text = "Are you sure you want to delete \"$selectedListName\"? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteList(selectedListId!!)
                            showDeleteConfirm = false
                            selectedListId = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            selectedListId = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

}