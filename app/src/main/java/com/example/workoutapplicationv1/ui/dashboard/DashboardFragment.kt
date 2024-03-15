package com.example.workoutapplicationv1.ui.dashboard

// NutritionCalculatorFragment.kt


import DatabaseHelper
import FoodItem
import FoodItemAdapter
import QuantityChangeListener
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapplicationv1.R

class DashboardFragment : Fragment(), QuantityChangeListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalContainer: LinearLayout
    private lateinit var foodItemAdapter: FoodItemAdapter
    private lateinit var searchEditText: EditText
    private lateinit var totalQuantityLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        totalContainer = view.findViewById(R.id.totalContainer)
        searchEditText = view.findViewById(R.id.searchEditText)
        totalQuantityLabel = view.findViewById(R.id.totalQuantityLabel)

        val searchButton: Button = view.findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            handleSearch()
        }

        val refreshButton: Button = view.findViewById(R.id.resetButton)
        refreshButton.setOnClickListener {
        resetCalories()
        }

        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val currentProteinQuant = sharedPreferencesV2.getFloat("protein_quant", 0.0f)
        val currentCarbsQuant = sharedPreferencesV2.getFloat("carbs_quant", 0.0f)
        val currentSugarQuant = sharedPreferencesV2.getFloat("sugar_quant", 0.0f)
        val currentFiberQuant = sharedPreferencesV2.getFloat("fiber_quant", 0.0f)
        val currentCholesterolQuant = sharedPreferencesV2.getFloat("cholesterol_quant", 0.0f)
        val currentFatQuant = sharedPreferencesV2.getFloat("fat_quant", 0.0f)
        val currentCaloriesQuant = sharedPreferencesV2.getFloat("calories_quant", 0.0f)


        val proteinLabel = currentProteinQuant.toInt()
        val carbsLabel = currentCarbsQuant.toInt()
        val fiberLabel = currentFiberQuant.toInt()
        val cholesterolLabel = currentCholesterolQuant.toInt()
        val caloriesLabel = currentCaloriesQuant.toInt()
        val sugarLabel = currentSugarQuant.toInt()
        val fatLabel = currentFatQuant.toInt()

        totalQuantityLabel.text = "Nutrition Details: Carbs: $carbsLabel(g), Protein: $proteinLabel(g), Fiber: $fiberLabel(g), Sugar: $sugarLabel(g), Fat: $fatLabel(g), Cholesterol: $cholesterolLabel(mg), Calories: $caloriesLabel"

        return view
    }

    private fun handleSearch() {
        val searchQuery = searchEditText.text.toString().trim()

        // Filter the list based on the search query
        val filteredList = getFoodItems().filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        // Initialize RecyclerView with the filtered list
        foodItemAdapter = FoodItemAdapter(filteredList,this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = foodItemAdapter
    }

    private fun getFoodItems(): List<FoodItem> {
        val dbHelper = DatabaseHelper(requireContext()) // Assuming you're in a Fragment, use requireContext()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            FoodItemContract.FoodItemEntry.COLUMN_NAME,
            FoodItemContract.FoodItemEntry.COLUMN_QUANTITY,
            FoodItemContract.FoodItemEntry.COLUMN_PROTEIN,
            FoodItemContract.FoodItemEntry.COLUMN_CARBS,
            FoodItemContract.FoodItemEntry.COLUMN_SUGAR,
            FoodItemContract.FoodItemEntry.COLUMN_CHOLESTEROL,
            FoodItemContract.FoodItemEntry.COLUMN_FIBER,
            FoodItemContract.FoodItemEntry.COLUMN_FAT,
            FoodItemContract.FoodItemEntry.COLUMN_CALORIES
        )

        val cursor = db.query(
            FoodItemContract.FoodItemEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val foodItemList = mutableListOf<FoodItem>()

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_NAME))
            val quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_QUANTITY))
            val protein = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_PROTEIN))
            val carbs = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_CARBS))
            val sugar = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_SUGAR))
            val cholesterol = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_CHOLESTEROL))
            val fiber = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_FIBER))
            val fat = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_FAT))
            val calories = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodItemContract.FoodItemEntry.COLUMN_CALORIES))

            val foodItem = FoodItem(name, quantity, protein, carbs, sugar, cholesterol, fiber, fat, calories)
            foodItemList.add(foodItem)
        }

        cursor.close()
        dbHelper.close()

        return foodItemList
    }

    override fun onQuantityChanged(foodItem: FoodItem, oldQuantity: Double) {

        // Update the quantity in the database
        updateQuantityInDatabase(foodItem)
        // You may also update other UI elements or calculations here
        val newQuantity = foodItem.quantity

        val proteinValue = foodItem.protein
        val proteinDifference = (proteinValue * (newQuantity-oldQuantity))/100
        val carbsValue = foodItem.carbs
        val carbsDifference = (carbsValue * (newQuantity-oldQuantity))/100
        val sugarValue = foodItem.sugar
        val sugarDifference = (sugarValue * (newQuantity-oldQuantity))/100
        val cholesterolValue = foodItem.cholesterol
        val cholesterolDifference = (cholesterolValue * (newQuantity-oldQuantity))/100
        val fiberValue = foodItem.fiber
        val fiberDifference = (fiberValue * (newQuantity-oldQuantity))/100
        val fatValue = foodItem.fat
        val fatDifference = (fatValue * (newQuantity-oldQuantity))/100
        val caloriesValue = foodItem.calories
        val caloriesDifference = (caloriesValue * (newQuantity-oldQuantity))/100

        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val currentProteinQuant = sharedPreferencesV2.getFloat("protein_quant", 0.0f)
        val updatedProteinValue = currentProteinQuant+proteinDifference.toFloat()

        val currentCarbsQuant = sharedPreferencesV2.getFloat("carbs_quant", 0.0f)
        val updatedCarbsValue = currentCarbsQuant+carbsDifference.toFloat()

        val currentSugarQuant = sharedPreferencesV2.getFloat("sugar_quant", 0.0f)
        val updatedSugarValue = currentSugarQuant+sugarDifference.toFloat()

        val currentFiberQuant = sharedPreferencesV2.getFloat("fiber_quant", 0.0f)
        val updatedFiberValue = currentFiberQuant+fiberDifference.toFloat()

        val currentCholesterolQuant = sharedPreferencesV2.getFloat("cholesterol_quant", 0.0f)
        val updatedCholesterolValue = currentCholesterolQuant+cholesterolDifference.toFloat()

        val currentFatQuant = sharedPreferencesV2.getFloat("fat_quant", 0.0f)
        val updatedFatValue = currentFatQuant+fatDifference.toFloat()

        val currentCaloriesQuant = sharedPreferencesV2.getFloat("calories_quant", 0.0f)
        val updatedCaloriesValue = currentCaloriesQuant+caloriesDifference.toFloat()

        var editor = sharedPreferencesV2.edit()
        editor.putFloat("protein_quant", updatedProteinValue)
        editor.putFloat("carbs_quant", updatedCarbsValue)
        editor.putFloat("fiber_quant", updatedFiberValue)
        editor.putFloat("sugar_quant", updatedSugarValue)
        editor.putFloat("fat_quant", updatedFatValue)
        editor.putFloat("cholesterol_quant", updatedCholesterolValue)
        editor.putFloat("calories_quant", updatedCaloriesValue)
        editor.apply()

        val proteinLabel = updatedProteinValue.toInt()
        val carbsLabel = updatedCarbsValue.toInt()
        val fiberLabel = updatedFiberValue.toInt()
        val cholesterolLabel = updatedCholesterolValue.toInt()
        val caloriesLabel = updatedCaloriesValue.toInt()
        val sugarLabel = updatedSugarValue.toInt()
        val fatLabel = updatedFatValue.toInt()

        totalQuantityLabel.text = "Nutrition Details: Carbs: $carbsLabel(g), Protein: $proteinLabel(g), Fiber: $fiberLabel(g), Sugar: $sugarLabel(g), Fat: $fatLabel(g), Cholesterol: $cholesterolLabel(mg), Calories: $caloriesLabel"
    }

    private fun updateQuantityInDatabase(foodItem: FoodItem) {
        val dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(FoodItemContract.FoodItemEntry.COLUMN_QUANTITY, foodItem.quantity)
        }

        val selection = "${FoodItemContract.FoodItemEntry.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(foodItem.name)

        db.update(
            FoodItemContract.FoodItemEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        dbHelper.close()
    }

    private fun resetCalories() {
        val dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        // Reset all quantity fields to 0 in the database
        val values = ContentValues().apply {
            put(FoodItemContract.FoodItemEntry.COLUMN_QUANTITY, 0.0)
        }

        db.update(
            FoodItemContract.FoodItemEntry.TABLE_NAME,
            values,
            null,
            null
        )

        dbHelper.close()

        // Reset all nutrient values to 0 in SharedPreferences
        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferencesV2.edit()
        editor.putFloat("protein_quant", 0.0f)
        editor.putFloat("carbs_quant", 0.0f)
        editor.putFloat("fiber_quant", 0.0f)
        editor.putFloat("sugar_quant", 0.0f)
        editor.putFloat("fat_quant", 0.0f)
        editor.putFloat("cholesterol_quant", 0.0f)
        editor.putFloat("calories_quant", 0.0f)
        editor.apply()

        searchEditText.text.clear()

        // Clear RecyclerView and update the UI to show 0 values
        foodItemAdapter = FoodItemAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = foodItemAdapter

        // Update the UI to show 0 values
        totalQuantityLabel.text = "Nutrition Details: Carbs: 0(g), Protein: 0(g), Fiber: 0(g), Sugar: 0(g), Fat: 0(g), Cholesterol: 0(mg), Calories: 0"
    }

}