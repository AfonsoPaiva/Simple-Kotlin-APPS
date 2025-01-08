package ipca.example.shoppinglist.ui.lists.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ipca.example.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun UpdateItemView(
    modifier: Modifier = Modifier,
    listId: String,
    itemId: String,
    navController: NavController,
    onItemUpdated: () -> Unit
) {
    val viewModel: UpdateItemViewModel = viewModel()
    val state = viewModel.state.value

    LaunchedEffect(key1 = listId, key2 = itemId) {
        viewModel.fetchItem(listId, itemId)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Update Item",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Item Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = state.quantity,
                onValueChange = { viewModel.onQuantityChange(it) },
                label = { Text("Quantity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Checkbox(
                    checked = state.isBought,
                    onCheckedChange = { viewModel.onIsBoughtChange(it) }
                )
                Text(text = "Bought", style = MaterialTheme.typography.bodyLarge)
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Save Changes Button
                Button(
                    onClick = {
                        if (state.name.isNotBlank() && state.quantity.isNotBlank()) {
                            viewModel.updateItem(listId, itemId) {
                                onItemUpdated()
                            }
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Item",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes")
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Delete Button
                OutlinedButton(
                    onClick = {
                        // Optional: Add a confirmation dialog here
                        viewModel.deleteItem(listId, itemId) {
                            onItemUpdated()
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Item")
                }
            }
        }
    }
}