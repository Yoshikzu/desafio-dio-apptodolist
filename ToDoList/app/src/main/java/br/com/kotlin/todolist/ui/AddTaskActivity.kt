package br.com.kotlin.todolist.ui

import android.app.Activity
import android.app.AlertDialog
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.kotlin.todolist.databinding.ActivityAddTaskBinding
import br.com.kotlin.todolist.datasource.TaskDataSource
import br.com.kotlin.todolist.extensions.format
import br.com.kotlin.todolist.extensions.text
import br.com.kotlin.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.concurrent.timer

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilHour.text = it.hour
                binding.tilDate.text = it.date
            }

        }

        insertListeners()
    }

    private fun insertListeners(){
        binding.tilDate.editText?.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager,"DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timerPicker.addOnPositiveButtonClickListener {
                val minute = if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute
                val hour = if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour
                
                binding.tilHour.text = "$hour:$minute"
            }
            timerPicker.show(supportFragmentManager,null)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                id = intent.getIntExtra(TASK_ID,0)
            )
            if (validate_fields()){
                TaskDataSource.insertTask(task)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    companion object{
        const val TASK_ID = "task_id"
    }

    private fun validate_fields(): Boolean  {
        val msg = AlertDialog.Builder(this)
        msg.setTitle("Atenção")
        if (binding.tilTitle.text.isBlank() || binding.tilTitle.text.isEmpty()){
            msg.setMessage("Preencha o Título")
            msg.show()
            return false
        }

        if(binding.tilDate.text.isBlank() || binding.tilDate.text.isEmpty()){
            msg.setMessage("Preencha a Data")
            msg.show()
            return false
        }
        if(binding.tilHour.text.isBlank() || binding.tilHour.text.isEmpty()){
            msg.setMessage("Preencha a Hora")
            msg.show()
            return false
        }
        return true
    }

}