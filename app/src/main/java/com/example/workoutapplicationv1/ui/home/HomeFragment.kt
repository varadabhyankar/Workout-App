package com.example.workoutapplicationv1.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.workoutapplicationv1.R
import com.example.workoutapplicationv1.databinding.FragmentHomeBinding
import java.math.RoundingMode

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var editTextName: EditText
    private lateinit var spinnerGoal: Spinner
    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var textViewBMI: TextView
    private lateinit var spinnerExperience: Spinner
    private lateinit var spinnerGender: Spinner
    private lateinit var buttonGenerateSchedule: Button
    private lateinit var buttonResetProgress: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        editTextName = view.findViewById(R.id.editTextName)
        spinnerGoal = view.findViewById(R.id.spinnerGoal)
        editTextHeight = view.findViewById(R.id.editTextHeight)
        editTextWeight = view.findViewById(R.id.editTextWeight)
        textViewBMI = view.findViewById(R.id.textViewBMI)
        spinnerExperience = view.findViewById(R.id.spinnerExperience)
        spinnerGender = view.findViewById(R.id.spinnerGender)
        buttonGenerateSchedule = view.findViewById(R.id.buttonGenerateSchedule)
        buttonResetProgress = view.findViewById(R.id.buttonResetProgress)

        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val name = sharedPreferencesV2.getString("name", "")
        val height = sharedPreferencesV2.getString("height", "")
        val weight = sharedPreferencesV2.getString("weight", "")
        val bmi = sharedPreferencesV2.getString("bmi", "")
        val goal = sharedPreferencesV2.getString("goal", "Weight Loss")
        val experience = sharedPreferencesV2.getString("experience","Beginner")
        val gender = sharedPreferencesV2.getString("gender","Male")
        val goalArray = resources.getStringArray(R.array.goals)
        val goalIndex = goalArray.indexOf(goal)
        val experienceArray = resources.getStringArray(R.array.experience_levels)
        val experienceIndex = experienceArray.indexOf(experience)
        val genderArray = resources.getStringArray(R.array.gender)
        val genderIndex = genderArray.indexOf(gender)

        editTextName.setText(name)
        editTextHeight.setText(height)
        editTextWeight.setText(weight)
        textViewBMI.text = "BMI: $bmi"
        spinnerGoal.setSelection(goalIndex)
        spinnerExperience.setSelection(experienceIndex)
        spinnerGender.setSelection(genderIndex)

        buttonGenerateSchedule.setOnClickListener {
            generateSchedule()
        }

        buttonResetProgress.setOnClickListener {
            resetProgress()
        }

        return view
    }

    private fun generateSchedule() {
        val name = editTextName.text.toString()
        val goal = spinnerGoal.selectedItem.toString()
        val heightStr = editTextHeight.text.toString()
        val weightStr = editTextWeight.text.toString()
        val experience = spinnerExperience.selectedItem.toString()
        val gender = spinnerGender.selectedItem.toString()

        // Validate input
        if (name.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toFloat()
        val weight = weightStr.toFloat()

        // Calculate BMI
        var bmi = (weight * 100 * 100) / (height * height)

        // Check for unrealistic BMI values
        if (bmi > 75 || bmi < 10) {
            Toast.makeText(requireContext(), "BMI unrealistic; please enter accurate height and weight", Toast.LENGTH_SHORT).show()
            return
        }

        bmi = bmi.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()

        // Update the BMI TextView
        textViewBMI.text = "BMI: $bmi"

        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferencesV2.edit()
        editor.putString("name",name)
        editor.putString("goal",goal)
        editor.putString("experience",experience)
        editor.putString("gender",gender)
        editor.putString("height",heightStr)
        editor.putString("weight",weightStr)
        editor.putString("bmi",bmi.toString())
        editor.apply()

        val base_points : Int

        when (experience) {
            "Beginner" -> {
                base_points = if(gender == "Male"){
                    96
                } else{
                    80
                }
            }
            else -> {
                base_points = if(gender == "Male"){
                    160
                } else{
                    120
                }
            }
        }

        val bicep_points = sharedPreferencesV2.getInt("bicep_points",base_points)
        val triceps_points = sharedPreferencesV2.getInt("triceps_points",base_points)
        val back_points = sharedPreferencesV2.getInt("back_points",base_points)
        val chest_points = sharedPreferencesV2.getInt("chest_points",base_points)
        val shoulder_points = sharedPreferencesV2.getInt("shoulder_points",base_points)
        val legs_points = sharedPreferencesV2.getInt("legs_points",base_points)
        val core_points = sharedPreferencesV2.getInt("core_points",base_points)
        val cardio_points = sharedPreferencesV2.getInt("cardio_points",base_points)

        generateScheduleHelper(experience,gender,bicep_points,triceps_points,chest_points,shoulder_points,back_points,legs_points,core_points,cardio_points)
    }

    private fun resetProgress() {

        val experience = spinnerExperience.selectedItem.toString()
        val gender = spinnerGender.selectedItem.toString()
        val base_points : Int

        when (experience) {
            "Beginner" -> {
                base_points = if(gender == "Male"){
                    96
                } else{
                    80
                }
            }
            else -> {
                base_points = if(gender == "Male"){
                    160
                } else{
                    120
                }
            }
        }
        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferencesV2.edit()
        editor.putInt("bicep_points",base_points)
        editor.putInt("triceps_points",base_points)
        editor.putInt("shoulder_points",base_points)
        editor.putInt("chest_points",base_points)
        editor.putInt("back_points",base_points)
        editor.putInt("legs_points",base_points)
        editor.putInt("core_points",base_points)
        editor.putInt("cardio_points",base_points)
        editor.apply()
        generateScheduleHelper(experience,gender,base_points,base_points,base_points,base_points,base_points,base_points,base_points,base_points)
    }

    fun generateScheduleHelper(experience:String,gender:String,bicep_points:Int,triceps_points:Int,chest_points:Int,shoulder_points:Int,back_points:Int,legs_points:Int,core_points:Int,cardio_points:Int){
        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferencesV2.edit()

        var index:Int
        var last_index:Int
        var arr_size:Int
        var temp_arr:Array<String>
        var temp_cost_arr:IntArray

        if(experience=="Beginner"){
            //Exercise 1
            temp_arr = resources.getStringArray(R.array.cardio_exercises)
            temp_cost_arr = resources.getIntArray(R.array.cardio_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise1",temp_arr[index])
            editor.putString("reps1",getReps(temp_arr[index],temp_cost_arr[index],cardio_points))
            //Exercise 2
            temp_arr = resources.getStringArray(R.array.abs_exercises)
            temp_cost_arr = resources.getIntArray(R.array.abs_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise2",temp_arr[index])
            editor.putString("reps2",getReps(temp_arr[index],temp_cost_arr[index],core_points))
            //Exercise 5
            temp_arr = resources.getStringArray(R.array.bicep_exercises)
            temp_cost_arr = resources.getIntArray(R.array.bicep_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise5",temp_arr[index])
            editor.putString("reps5",getReps(temp_arr[index],temp_cost_arr[index],bicep_points))
            //Exercise 6
            temp_arr = resources.getStringArray(R.array.bicep_exercises)
            temp_cost_arr = resources.getIntArray(R.array.bicep_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise6",temp_arr[index])
            editor.putString("reps6",getReps(temp_arr[index],temp_cost_arr[index],bicep_points))
            //Exercise 7
            temp_arr = resources.getStringArray(R.array.back_exercises)
            temp_cost_arr = resources.getIntArray(R.array.back_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise7",temp_arr[index])
            editor.putString("reps7",getReps(temp_arr[index],temp_cost_arr[index],back_points))
            //Exercise 8
            temp_arr = resources.getStringArray(R.array.back_exercises)
            temp_cost_arr = resources.getIntArray(R.array.back_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise8",temp_arr[index])
            editor.putString("reps8",getReps(temp_arr[index],temp_cost_arr[index],back_points))
            //Exercise 9
            temp_arr = resources.getStringArray(R.array.cardio_exercises)
            temp_cost_arr = resources.getIntArray(R.array.cardio_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise9",temp_arr[index])
            editor.putString("reps9",getReps(temp_arr[index],temp_cost_arr[index],cardio_points))
            //Exercise 10
            temp_arr = resources.getStringArray(R.array.cardio_v2_exercises)
            temp_cost_arr = resources.getIntArray(R.array.cardio_v2_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise10",temp_arr[index])
            editor.putString("reps10",getReps(temp_arr[index],temp_cost_arr[index],cardio_points))
            //Exercise 13
            temp_arr = resources.getStringArray(R.array.chest_exercises)
            temp_cost_arr = resources.getIntArray(R.array.chest_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise13",temp_arr[index])
            editor.putString("reps13",getReps(temp_arr[index],temp_cost_arr[index],chest_points))
            //Exercise 14
            temp_arr = resources.getStringArray(R.array.chest_exercises)
            temp_cost_arr = resources.getIntArray(R.array.chest_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise14",temp_arr[index])
            editor.putString("reps14",getReps(temp_arr[index],temp_cost_arr[index],chest_points))
            //Exercise 15
            temp_arr = resources.getStringArray(R.array.shoulder_exercises)
            temp_cost_arr = resources.getIntArray(R.array.shoulder_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise15",temp_arr[index])
            editor.putString("reps15",getReps(temp_arr[index],temp_cost_arr[index],shoulder_points))
            //Exercise 16
            temp_arr = resources.getStringArray(R.array.triceps_exercises)
            temp_cost_arr = resources.getIntArray(R.array.triceps_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise16",temp_arr[index])
            editor.putString("reps16",getReps(temp_arr[index],temp_cost_arr[index],triceps_points))
            //Exercise 17
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise17",temp_arr[index])
            editor.putString("reps17",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 18
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            last_index = index
            editor.putString("exercise18",temp_arr[index])
            editor.putString("reps18",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 19
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise19",temp_arr[index])
            editor.putString("reps19",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 20
            temp_arr = resources.getStringArray(R.array.abs_exercises)
            temp_cost_arr = resources.getIntArray(R.array.abs_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise20",temp_arr[index])
            editor.putString("reps20",getReps(temp_arr[index],temp_cost_arr[index],core_points))
            //Exercise 21
            temp_arr = resources.getStringArray(R.array.cardio_exercises)
            temp_cost_arr = resources.getIntArray(R.array.cardio_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise21",temp_arr[index])
            editor.putString("reps21",getReps(temp_arr[index],temp_cost_arr[index],cardio_points))
            //Exercise 22
            temp_arr = resources.getStringArray(R.array.cardio_v2_exercises)
            temp_cost_arr = resources.getIntArray(R.array.cardio_v2_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise22",temp_arr[index])
            editor.putString("reps22",getReps(temp_arr[index],temp_cost_arr[index],cardio_points))
            for(iter in (1..24)){
                editor.putString("percent_done_$iter","0")
            }
            editor.apply()
        }
        else{

            //Exercise 1
            temp_arr = resources.getStringArray(R.array.back_exercises)
            temp_cost_arr = resources.getIntArray(R.array.back_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise1",temp_arr[index])
            editor.putString("reps1",getReps(temp_arr[index],temp_cost_arr[index],back_points))
            //Exercise 2
            temp_arr = resources.getStringArray(R.array.back_exercises)
            temp_cost_arr = resources.getIntArray(R.array.back_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise2",temp_arr[index])
            editor.putString("reps2",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 3
            temp_arr = resources.getStringArray(R.array.bicep_exercises)
            temp_cost_arr = resources.getIntArray(R.array.bicep_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise3",temp_arr[index])
            editor.putString("reps3",getReps(temp_arr[index],temp_cost_arr[index],bicep_points))
            //Exercise 4
            temp_arr = resources.getStringArray(R.array.abs_exercises)
            temp_cost_arr = resources.getIntArray(R.array.abs_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise4",temp_arr[index])
            editor.putString("reps4",getReps(temp_arr[index],temp_cost_arr[index],core_points))
            //Exercise 5
            temp_arr = resources.getStringArray(R.array.chest_exercises)
            temp_cost_arr = resources.getIntArray(R.array.chest_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise5",temp_arr[index])
            editor.putString("reps5",getReps(temp_arr[index],temp_cost_arr[index],chest_points))
            //Exercise 6
            temp_arr = resources.getStringArray(R.array.chest_exercises)
            temp_cost_arr = resources.getIntArray(R.array.chest_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise6",temp_arr[index])
            editor.putString("reps6",getReps(temp_arr[index],temp_cost_arr[index],chest_points))
            //Exercise 7
            temp_arr = resources.getStringArray(R.array.shoulder_exercises)
            temp_cost_arr = resources.getIntArray(R.array.shoulder_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise7",temp_arr[index])
            editor.putString("reps7",getReps(temp_arr[index],temp_cost_arr[index],shoulder_points))
            //Exercise 8
            temp_arr = resources.getStringArray(R.array.triceps_exercises)
            temp_cost_arr = resources.getIntArray(R.array.triceps_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise8",temp_arr[index])
            editor.putString("reps8",getReps(temp_arr[index],temp_cost_arr[index],triceps_points))
            //Exercise 9
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise9",temp_arr[index])
            editor.putString("reps9",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 10
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            last_index = index
            editor.putString("exercise10",temp_arr[index])
            editor.putString("reps10",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 11
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise11",temp_arr[index])
            editor.putString("reps11",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 12
            temp_arr = resources.getStringArray(R.array.cardio_v2_exercises)
            temp_cost_arr = resources.getIntArray(R.array.cardio_v2_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise12",temp_arr[index])
            editor.putString("reps12",getReps(temp_arr[index],temp_cost_arr[index],cardio_points))
            //Exercise 13
            temp_arr = resources.getStringArray(R.array.bicep_exercises)
            temp_cost_arr = resources.getIntArray(R.array.bicep_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise13",temp_arr[index])
            editor.putString("reps13",getReps(temp_arr[index],temp_cost_arr[index],bicep_points))
            //Exercise 14
            temp_arr = resources.getStringArray(R.array.bicep_exercises)
            temp_cost_arr = resources.getIntArray(R.array.bicep_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise14",temp_arr[index])
            editor.putString("reps14",getReps(temp_arr[index],temp_cost_arr[index],bicep_points))
            //Exercise 15
            temp_arr = resources.getStringArray(R.array.back_exercises)
            temp_cost_arr = resources.getIntArray(R.array.back_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise15",temp_arr[index])
            editor.putString("reps15",getReps(temp_arr[index],temp_cost_arr[index],back_points))
            //Exercise 16
            temp_arr = resources.getStringArray(R.array.back_exercises)
            temp_cost_arr = resources.getIntArray(R.array.back_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise16",temp_arr[index])
            editor.putString("reps16",getReps(temp_arr[index],temp_cost_arr[index],back_points))
            //Exercise 17
            temp_arr = resources.getStringArray(R.array.triceps_exercises)
            temp_cost_arr = resources.getIntArray(R.array.triceps_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise17",temp_arr[index])
            editor.putString("reps17",getReps(temp_arr[index],temp_cost_arr[index],triceps_points))
            //Exercise 18
            temp_arr = resources.getStringArray(R.array.triceps_exercises)
            temp_cost_arr = resources.getIntArray(R.array.triceps_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise18",temp_arr[index])
            editor.putString("reps18",getReps(temp_arr[index],temp_cost_arr[index],triceps_points))
            //Exercise 19
            temp_arr = resources.getStringArray(R.array.shoulder_exercises)
            temp_cost_arr = resources.getIntArray(R.array.shoulder_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise19",temp_arr[index])
            editor.putString("reps19",getReps(temp_arr[index],temp_cost_arr[index],shoulder_points))
            //Exercise 20
            temp_arr = resources.getStringArray(R.array.chest_exercises)
            temp_cost_arr = resources.getIntArray(R.array.chest_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise20",temp_arr[index])
            editor.putString("reps20",getReps(temp_arr[index],temp_cost_arr[index],chest_points))
            //Exercise 21
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            last_index=index
            editor.putString("exercise21",temp_arr[index])
            editor.putString("reps21",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 22
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            last_index = index
            editor.putString("exercise22",temp_arr[index])
            editor.putString("reps22",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 23
            temp_arr = resources.getStringArray(R.array.leg_exercises)
            temp_cost_arr = resources.getIntArray(R.array.leg_costs)
            arr_size = temp_arr.size
            while(index==last_index){
                index = (0..arr_size-1).random()
            }
            editor.putString("exercise23",temp_arr[index])
            editor.putString("reps23",getReps(temp_arr[index],temp_cost_arr[index],legs_points))
            //Exercise 24
            temp_arr = resources.getStringArray(R.array.abs_exercises)
            temp_cost_arr = resources.getIntArray(R.array.abs_costs)
            arr_size = temp_arr.size
            index = (0..arr_size-1).random()
            editor.putString("exercise24",temp_arr[index])
            editor.putString("reps24",getReps(temp_arr[index],temp_cost_arr[index],core_points))

            for(iter in (1..24)){
                editor.putString("percent_done_$iter","0")
            }
            editor.apply()
        }
    }

    fun getReps(exercise_type:String,exercise_points:Int,points:Int):String{
        if(exercise_type=="Jogging" || exercise_type=="Cycling" || exercise_type == "Skipping"){
            if(exercise_type=="Skipping"){
                return (points/exercise_points).toString()
            }
            else if(points<100){
                return "15 mins"
            }
            else if(points<150){
                return "20 mins"
            }
            else if(points<175){
                return "30 mins"
            }
            else{
                return "40 mins"
            }
        }
        else{
            var count:Int = points/exercise_points
            val reps:Int
            if(count<14){
                count = count
                reps = 1
            }
            else if(count<26){
                count = count/2
                reps = 2
            }
            else if(count<39){
                count = count/3
                reps = 3
            }
            else{
                count = count/4
                reps = 4
            }
            return count.toString()+"X"+reps.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}