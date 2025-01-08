// File: ui/lists/ListListsViewModel.kt
package ipca.example.shoppinglist.ui.lists

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipca.example.shoppinglist.TAG
import ipca.example.shoppinglist.models.ListItems

data class ListListsState(
    val listItemsList: List<ListItems> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ListListsViewModel : ViewModel() {

    var state = mutableStateOf(ListListsState())
        private set

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun getLists() {
        val currentUser = auth.currentUser ?: run {
            state.value = state.value.copy(
                error = "User not logged in",
                isLoading = false
            )
            return
        }

        val userEmail = currentUser.email ?: return

        state.value = state.value.copy(isLoading = true, error = null)

        db.collection("users")
            .document(userEmail)
            .collection("lists")
            .get()
            .addOnSuccessListener { documents ->
                val listItemsList = arrayListOf<ListItems>()
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val listItem = document.toObject(ListItems::class.java)
                    listItem.docId = document.id
                    listItemsList.add(listItem)
                }
                state.value = state.value.copy(
                    listItemsList = listItemsList,
                    isLoading = false
                )
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                state.value = state.value.copy(
                    error = "Failed to fetch lists. Please try again.",
                    isLoading = false
                )
            }
    }

    fun deleteList(listId: String) {
        val currentUser = auth.currentUser ?: run {
            state.value = state.value.copy(error = "User not authenticated.")
            return
        }

        val userEmail = currentUser.email ?: run {
            state.value = state.value.copy(error = "User email not found.")
            return
        }

        db.collection("users")
            .document(userEmail)
            .collection("lists")
            .document(listId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "List deleted successfully")
                // Remove the deleted list from the current state
                val updatedList = state.value.listItemsList.filter { it.docId != listId }
                state.value = state.value.copy(listItemsList = updatedList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting list", e)
                state.value = state.value.copy(
                    error = "Failed to delete the list. Please try again."
                )
            }
    }
}