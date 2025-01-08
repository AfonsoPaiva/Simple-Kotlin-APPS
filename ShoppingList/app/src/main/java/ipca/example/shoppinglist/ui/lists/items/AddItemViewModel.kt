package ipca.example.shoppinglist.ui.lists.items

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipca.example.shoppinglist.TAG

data class AddItemState(
    val name: String = "",
    val quantity: String = "",
    val isBought: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AddItemViewModel : ViewModel() {

    var state = mutableStateOf(AddItemState())
        private set

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun onNameChange(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun onQuantityChange(quantity: String) {
        state.value = state.value.copy(quantity = quantity)
    }

    fun onIsBoughtChange(isBought: Boolean) {
        state.value = state.value.copy(isBought = isBought)
    }

    fun addItem(listId: String, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: run {
            state.value = state.value.copy(error = "User not authenticated.")
            return
        }

        val quantityInt = state.value.quantity.toIntOrNull() ?: run {
            state.value = state.value.copy(error = "Invalid quantity.")
            return
        }

        val item = mapOf(
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
            .add(item)
            .addOnSuccessListener {
                Log.d(TAG, "Item added successfully")
                state.value = state.value.copy(isLoading = false)
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding item", e)
                state.value = state.value.copy(
                    error = "Failed to add item. Please try again.",
                    isLoading = false
                )
            }
    }
}