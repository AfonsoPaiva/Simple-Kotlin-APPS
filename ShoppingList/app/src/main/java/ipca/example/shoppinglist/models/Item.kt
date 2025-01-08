package ipca.example.shoppinglist.models

data class Item(
    var docId: String? = null,
    var name: String? = null,
    var quantity: Int = 0,
    var checked: Boolean = false
)