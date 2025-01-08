package ipca.example.shoppinglist.ui.lists.items

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipca.example.shoppinglist.TAG

data class ListItemsState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Item(
    val docId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val bought: Boolean = false
)

class ListItemsViewModel : ViewModel() {

    var state = mutableStateOf(ListItemsState())
        private set

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun getItems(listId: String) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: run {
            state.value = state.value.copy(error = "User not authenticated.")
            return
        }

        state.value = state.value.copy(isLoading = true, error = null)

        db.collection("users")
            .document(userEmail)
            .collection("lists")
            .document(listId)
            .collection("items")
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.map { document ->
                    Item(
                        docId = document.id,
                        name = document.getString("name") ?: "",
                        quantity = (document.getLong("quantity")?.toInt()) ?: 1,
                        bought = document.getBoolean("bought") ?: false
                    )
                }
                state.value = state.value.copy(
                    items = items,
                    isLoading = false
                )
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting items", e)
                state.value = state.value.copy(
                    error = "Failed to fetch items. Please try again.",
                    isLoading = false
                )
            }
    }

    fun updateItemBoughtStatus(listId: String, item: Item, newStatus: Boolean) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: run {
            state.value = state.value.copy(error = "User not authenticated.")
            return
        }

        db.collection("users")
            .document(userEmail)
            .collection("lists")
            .document(listId)
            .collection("items")
            .document(item.docId)
            .update("bought", newStatus)
            .addOnSuccessListener {
                // Update local state
                val updatedItems = state.value.items.map {
                    if (it.docId == item.docId) it.copy(bought = newStatus) else it
                }
                state.value = state.value.copy(items = updatedItems)
                Log.d(TAG, "Item updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating item", e)
                state.value = state.value.copy(
                    error = "Failed to update item. Please try again."
                )
            }
    }
}