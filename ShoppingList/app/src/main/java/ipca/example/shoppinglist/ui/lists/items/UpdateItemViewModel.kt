package ipca.example.shoppinglist.ui.lists.items

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipca.example.shoppinglist.TAG

data class UpdateItemState(
    val name: String = "",
    val quantity: String = "",
    val isBought: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class UpdateItemViewModel : ViewModel() {

    var state = mutableStateOf(UpdateItemState())
        private set

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun fetchItem(listId: String, itemId: String) {
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
            .document(itemId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    state.value = state.value.copy(
                        name = document.getString("name") ?: "",
                        quantity = (document.getLong("quantity")?.toString()) ?: "1",
                        isBought = document.getBoolean("bought") ?: false,
                        isLoading = false
                    )
                } else {
                    state.value = state.value.copy(
                        error = "Item not found.",
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching item", e)
                state.value = state.value.copy(
                    error = "Failed to fetch item. Please try again.",
                    isLoading = false
                )
            }
    }

    fun updateItem(listId: String, itemId: String, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: run {
            state.value = state.value.copy(error = "User not authenticated.")
            return
        }

        val quantityInt = state.value.quantity.toIntOrNull() ?: run {
            state.value = state.value.copy(error = "Invalid quantity.")
            return
        }

        if (quantityInt <= 0) {
            state.value = state.value.copy(error = "Quantity must be a positive number.")
            return
        }

        val updatedItem = mapOf(
            "name" to state.value.name,
            "quantity" to quantityInt,
            "bought" to state.value.isBought
        )

        state.value = state.value.copy(isLoading = true, error = null)

        db.collection("users")
            .document(userEmail)
            .collection("lists")
            .document(listId)
            .collection("items")
            .document(itemId)
            .set(updatedItem)
            .addOnSuccessListener {
                Log.d(TAG, "Item updated successfully")
                state.value = state.value.copy(isLoading = false)
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating item", e)
                state.value = state.value.copy(
                    error = "Failed to update item. Please try again.",
                    isLoading = false
                )
            }
    }

    fun deleteItem(listId: String, itemId: String, onSuccess: () -> Unit) {
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
            .document(itemId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Item deleted successfully")
                state.value = state.value.copy(isLoading = false)
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting item", e)
                state.value = state.value.copy(
                    error = "Failed to delete item. Please try again.",
                    isLoading = false
                )
            }
    }

    fun onNameChange(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun onQuantityChange(quantity: String) {
        state.value = state.value.copy(quantity = quantity)
    }

    fun onIsBoughtChange(isBought: Boolean) {
        state.value = state.value.copy(isBought = isBought)
    }

    fun setError(error: String) {
        state.value = state.value.copy(error = error)
    }
}