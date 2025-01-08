package ipca.example.shoppinglist.ui.lists

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth // **Correct Import**
import com.google.firebase.firestore.ktx.firestore // **Correct Import**
import com.google.firebase.ktx.Firebase // **Correct Import**
import ipca.example.shoppinglist.TAG
import ipca.example.shoppinglist.models.ListItems

data class AddListState(
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AddListViewModel : ViewModel() {

    private val addListTag = "AddList" // Custom tag for filtering logs

    var state = mutableStateOf(AddListState())
        private set

    fun onNameChange(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun addList() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        val userEmail = currentUser?.email
        if (userEmail == null) {
            Log.e(addListTag, "Error: User is not logged in or email is null")
            state.value = state.value.copy(error = "User not authenticated.")
            return
        }

        if (state.value.name.isBlank()) {
            Log.e(addListTag, "Error: List name is empty")
            state.value = state.value.copy(error = "List name cannot be empty.")
            return
        }

        val listItems = ListItems(
            "",
            state.value.name,
            arrayListOf(userEmail) // Save the user's email in the list
        )

        Log.d(addListTag, "Attempting to add list with name: ${state.value.name} for user: $userEmail")

        state.value = state.value.copy(isLoading = true, error = null)

        db.collection("users") // Create a "users" collection
            .document(userEmail) // Use the email as the document ID
            .collection("lists") // Add the list under "lists" sub-collection
            .add(listItems)
            .addOnSuccessListener { documentReference ->
                Log.d(addListTag, "List successfully added with ID: ${documentReference.id}")
                state.value = state.value.copy(isLoading = false)
            }
            .addOnFailureListener { e ->
                Log.e(addListTag, "Error adding list", e)
                state.value = state.value.copy(
                    error = "Failed to add list. Please try again.",
                    isLoading = false
                )
            }
    }
}