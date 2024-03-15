package com.example.workoutapplicationv1.ui.notifications

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.workoutapplicationv1.R
import com.example.workoutapplicationv1.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var buttonDoneForTheWeek: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        buttonDoneForTheWeek = view.findViewById(R.id.buttonDoneForTheWeek)

        var exercise_text_box: TextView?
        var reps_text_box: TextView?
        var percent_done_text_box: EditText?
        var exercise_text: String?
        var reps_text: String?

        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        for(i in (1..24)){
            exercise_text_box = view?.findViewById<TextView>(resources.getIdentifier("exercise$i", "id", requireContext().packageName))
            reps_text_box = view?.findViewById<TextView>(resources.getIdentifier("reps$i", "id", requireContext().packageName))
            exercise_text = sharedPreferencesV2.getString("exercise$i","N/A")
            percent_done_text_box = view.findViewById<EditText>(resources.getIdentifier("percent_done_$i", "id", requireContext().packageName))
            reps_text = sharedPreferencesV2.getString("reps$i","N/A")
            exercise_text_box?.text = exercise_text
            reps_text_box?.text = reps_text
            percent_done_text_box?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not needed
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Not needed
                }
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        onPercentDoneChanged("percent_done_$i",s.toString())
                    }
                }
            })
            percent_done_text_box?.setText(sharedPreferencesV2.getString("percent_done_$i","0"))
        }
        val experience = sharedPreferencesV2.getString("experience","Beginner")
        if(experience=="Beginner"){
            val viewsToHide = listOf(3, 4, 11, 12, 23, 24)
            for (i in viewsToHide) {
                val exerciseView = view.findViewById<View>(resources.getIdentifier("exercise$i", "id", requireContext().packageName))
                val repsView = view.findViewById<View>(resources.getIdentifier("reps$i", "id", requireContext().packageName))
                val percentDoneView = view.findViewById<View>(resources.getIdentifier("percent_done_$i", "id", requireContext().packageName))

                exerciseView?.visibility = View.INVISIBLE
                repsView?.visibility = View.INVISIBLE
                percentDoneView?.visibility = View.INVISIBLE
            }
        }

        buttonDoneForTheWeek.setOnClickListener {
            regenerateSchedule()
        }

        return view
    }

    private fun regenerateSchedule(){

        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val experience:String? = sharedPreferencesV2.getString("experience","Beginner")
        val gender: String? = sharedPreferencesV2.getString("gender","Male")
        val base_points:Int

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

        var bicep_points = sharedPreferencesV2.getInt("bicep_points",base_points)
        var triceps_points = sharedPreferencesV2.getInt("triceps_points",base_points)
        var back_points = sharedPreferencesV2.getInt("back_points",base_points)
        var chest_points = sharedPreferencesV2.getInt("chest_points",base_points)
        var shoulder_points = sharedPreferencesV2.getInt("shoulder_points",base_points)
        var legs_points = sharedPreferencesV2.getInt("legs_points",base_points)
        var core_points = sharedPreferencesV2.getInt("core_points",base_points)
        var cardio_points = sharedPreferencesV2.getInt("cardio_points",base_points)

        var editTextPercent: EditText?
        var temp_flag = false
        var bicep_flag = false
        var triceps_flag = false
        var chest_flag = false
        var legs_flag = false
        var back_flag = false
        var abs_flag = false
        var cardio_flag = false
        var shoulder_flag = false

        for(i in (1..24)){
            temp_flag = false
            editTextPercent = view?.findViewById<EditText>(resources.getIdentifier("percent_done_$i", "id", requireContext().packageName))
            if(editTextPercent == null){
                temp_flag = true
            }
            else{
                if(editTextPercent.text.isEmpty() || editTextPercent.text.toString().toFloat()<100){
                    temp_flag = true
                }
            }

            
            if(experience=="Beginner"){
                if(temp_flag){
                    when (i) {
                        1, 9, 21, 22, 10 -> {
                            cardio_flag = true
                        }
                        2, 20 -> {
                            abs_flag = true
                        }
                        5, 6 -> {
                            bicep_flag = true
                        }
                        16 -> {
                            triceps_flag = true
                        }
                        15 -> {
                            shoulder_flag = true
                        }
                        17, 18, 19 -> {
                            legs_flag = true
                        }
                        13, 14 -> {
                            chest_flag = true
                        }
                        7, 8 -> {
                            back_flag = true
                        }
                    }
                }
            }
            else{
                if(temp_flag){
                    when (i) {
                        12 -> {
                            cardio_flag = true
                        }
                        4, 24 -> {
                            abs_flag = true
                        }
                        3, 13, 14 -> {
                            bicep_flag = true
                        }
                        8, 17, 18 -> {
                            triceps_flag = true
                        }
                        7, 19 -> {
                            shoulder_flag = true
                        }
                        9, 10, 11, 21, 22, 23 -> {
                            legs_flag = true
                        }
                        5, 6, 20 -> {
                            chest_flag = true
                        }
                        1, 2, 15, 16 -> {
                            back_flag = true
                        }
                    }
                }
            }
        }
        if(bicep_flag==false && bicep_points<400){
            bicep_points = (1.2*bicep_points).toInt()
        }
        if(triceps_flag==false && triceps_points<400){
            triceps_points = (1.2*triceps_points).toInt()
        }
        if(shoulder_flag==false && shoulder_points<400){
            shoulder_points = (1.2*shoulder_points).toInt()
        }
        if(chest_flag==false && chest_points<400){
            chest_points = (1.2*chest_points).toInt()
        }
        if(legs_flag==false && legs_points<400){
            legs_points = (1.2*legs_points).toInt()
        }
        if(abs_flag==false && core_points<400){
            core_points = (1.2*core_points).toInt()
        }
        if(back_flag==false && back_points<400){
            back_points = (1.2*back_points).toInt()
        }
        if(cardio_flag==false && cardio_points<400){
            cardio_points = (1.2*cardio_points).toInt()
        }

        val editor = sharedPreferencesV2.edit()
        editor.putInt("bicep_points",bicep_points)
        editor.putInt("triceps_points",triceps_points)
        editor.putInt("shoulder_points",shoulder_points)
        editor.putInt("chest_points",chest_points)
        editor.putInt("back_points",back_points)
        editor.putInt("legs_points",legs_points)
        editor.putInt("core_points",core_points)
        editor.putInt("cardio_points",cardio_points)
        editor.apply()

        var percent_done_text_box: EditText?
        for (k in (1..24)) {
            percent_done_text_box = view?.findViewById<EditText>(resources.getIdentifier("percent_done_$k", "id", requireContext().packageName))
            percent_done_text_box?.setText("0")
        }

        val temp_exp = experience ?: ""
        val temp_gender = gender ?: ""

        generateScheduleHelper(temp_exp,temp_gender,bicep_points,triceps_points,chest_points,shoulder_points,back_points,legs_points,core_points,cardio_points)
        reloadFragment()
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
            editor.apply()
        }
    }

    fun getReps(exercise_type:String,exercise_points:Int,points:Int):String{
        if(exercise_type=="Jogging" || exercise_type=="Cycling"){
            if(points<100){
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

    private fun reloadFragment() {
        parentFragmentManager.beginTransaction().apply {
            detach(this@NotificationsFragment)
            commit()
        }

        parentFragmentManager.beginTransaction().apply {
            attach(this@NotificationsFragment)
            commit()
        }
    }

    private fun onPercentDoneChanged(text:String,value:String){
        val sharedPreferencesV2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferencesV2.edit()
        editor.putString(text,value)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}