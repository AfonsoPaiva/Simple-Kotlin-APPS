package ipca.example.shoppinglist.ui.register

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ipca.example.shoppinglist.TAG

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegisterViewModel : ViewModel() {

    var state = mutableStateOf(RegisterState())
        private set

    fun onEmailChange(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        state.value = state.value.copy(password = password)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        state.value = state.value.copy(confirmPassword = confirmPassword)
    }

    fun onRegisterClick(onRegisterSuccess: () -> Unit) {
        if (state.value.email.isBlank() || state.value.password.isBlank() || state.value.confirmPassword.isBlank()) {
            showError("Please fill in all fields")
            return
        }

        if (state.value.password != state.value.confirmPassword) {
            showError("Passwords don't match")
            return
        }

        state.value = state.value.copy(isLoading = true)
        val auth: FirebaseAuth = Firebase.auth

        auth.createUserWithEmailAndPassword(state.value.email, state.value.password)
            .addOnCompleteListener { task ->
                state.value = state.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    onRegisterSuccess()
                } else {
                    val errorMessage = mapFirebaseErrorToUserFriendly(task.exception?.message)
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    state.value = state.value.copy(error = errorMessage)
                }
            }
    }

    fun showError(message: String) {
        state.value = state.value.copy(error = message)
    }

    private fun mapFirebaseErrorToUserFriendly(error: String?): String {
        return when {
            error?.contains("The email address is badly formatted") == true -> "Invalid email format."
            error?.contains("email address is already in use") == true -> "User already exists."
            error?.contains("The supplied auth credential is incorrect") == true -> "Incorrect email or password."
            error?.contains("Password should be at least 6 characters") == true -> "Password must be at least 6 characters long."
            error?.contains("There is no user record") == true -> "User does not exist."
            else -> "An unknown error occurred. Please try again."
        }
    }
}