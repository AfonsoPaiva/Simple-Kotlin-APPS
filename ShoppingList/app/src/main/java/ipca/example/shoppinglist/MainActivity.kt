// File: MainActivity.kt
package ipca.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ipca.example.shoppinglist.ui.lists.AddListView
import ipca.example.shoppinglist.ui.lists.ListListsView
import ipca.example.shoppinglist.ui.lists.items.AddItemView
import ipca.example.shoppinglist.ui.lists.items.ListItemsView
import ipca.example.shoppinglist.ui.lists.items.UpdateItemView
import ipca.example.shoppinglist.ui.register.RegisterView
import ipca.example.shoppinglist.ui.login.LoginView
import ipca.example.shoppinglist.ui.theme.ShoppingListTheme

const val TAG = "ShoppingList"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) {
                            LoginView(
                                modifier = Modifier.padding(innerPadding),
                                onLoginSuccess = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }
                        composable(Screen.Register.route) {
                            RegisterView(
                                modifier = Modifier.padding(innerPadding),
                                onRegisterSuccess = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Home.route) {
                            ListListsView(
                                navController = navController
                            )
                        }
                        composable(Screen.AddList.route) {
                            AddListView(navController = navController)
                        }
                        composable(
                            route = Screen.ListItems.route,
                            arguments = listOf(navArgument("listId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val listId = backStackEntry.arguments?.getString("listId") ?: ""
                            ListItemsView(
                                modifier = Modifier.padding(innerPadding),
                                listId = listId,
                                navController = navController
                            )
                        }
                        composable(
                            route = Screen.AddItem.route,
                            arguments = listOf(navArgument("listId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val listId = backStackEntry.arguments?.getString("listId") ?: ""
                            AddItemView(
                                listId = listId,
                                onItemAdded = {
                                    navController.popBackStack()
                                },
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable(
                            route = Screen.UpdateItem.route,
                            arguments = listOf(
                                navArgument("listId") { type = NavType.StringType },
                                navArgument("itemId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val listId = backStackEntry.arguments?.getString("listId") ?: ""
                            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                            UpdateItemView(
                                listId = listId,
                                itemId = itemId,
                                navController = navController,
                                onItemUpdated = {
                                    navController.popBackStack()
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
                LaunchedEffect(Unit) {
                    val auth = Firebase.auth
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddList : Screen("add_list")
    object ListItems : Screen("list_items/{listId}") {
        fun createRoute(listId: String) = "list_items/$listId"
    }
    object AddItem : Screen("add_item/{listId}") {
        fun createRoute(listId: String) = "add_item/$listId"
    }
    object UpdateItem : Screen("update_item/{listId}/{itemId}") {
        fun createRoute(listId: String, itemId: String) = "update_item/$listId/$itemId"
    }
}