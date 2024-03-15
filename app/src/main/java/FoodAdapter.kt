import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapplicationv1.R

// FoodItemAdapter.kt

interface QuantityChangeListener {
    fun onQuantityChanged(foodItem: FoodItem, oldQuantity: Double)
}

class FoodItemAdapter(
    private val foodItemList: List<FoodItem>,
    private val quantityChangeListener: QuantityChangeListener
) : RecyclerView.Adapter<FoodItemAdapter.ViewHolder>() {

    // ViewHolder class with views for each item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val quantityEditText: EditText = itemView.findViewById(R.id.quantityEditText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = foodItemList[position]

        // Bind data to views
        holder.itemName.text = currentItem.name
        holder.quantityEditText.setText(currentItem.quantity.toString())

        holder.quantityEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newQuantity = holder.quantityEditText.text.toString().toDoubleOrNull() ?: 0.0
                val oldQuantity = currentItem.quantity
                currentItem.quantity = newQuantity
                quantityChangeListener.onQuantityChanged(currentItem, oldQuantity)
            }
        }
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }
}