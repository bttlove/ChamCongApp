package com.example.chamcong

import EmployeeAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore

class AllTimeActivity : AppCompatActivity() {

    private lateinit var employeeListView: ListView
    private lateinit var searchEmployeeEditText: EditText
    private lateinit var employeeListAdapter: EmployeeAdapter
    private lateinit var employeeList: MutableList<Employee>
    private lateinit var filteredEmployeeList: MutableList<Employee>
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_time)

        employeeListView = findViewById(R.id.employeeListView)
        searchEmployeeEditText = findViewById(R.id.searchEmployee)
        employeeList = mutableListOf()
        filteredEmployeeList = mutableListOf()

        // Set up the adapter
        employeeListAdapter = EmployeeAdapter(this, filteredEmployeeList)
        employeeListView.adapter = employeeListAdapter

        // Fetch employee data
        fetchEmployeeData()

        // Set up search functionality
        searchEmployeeEditText.addTextChangedListener {
            val query = it.toString()
            searchEmployees(query)
        }

        // Handle click event for each employee
        employeeListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEmployee = filteredEmployeeList[position]

            // Navigate to the TotalWorkActivity with selected employee's data
            val intent = Intent(this, TotalWorkActivity::class.java).apply {
                putExtra("EMPLOYEE_NAME", selectedEmployee.name)
                putExtra("EMPLOYEE_EMAIL", selectedEmployee.email)
                putExtra("EMPLOYEE_POSITION", selectedEmployee.position)
                putExtra("EMPLOYEE_PICTURE", selectedEmployee.picture)
            }
            startActivity(intent)
        }
    }

    private fun fetchEmployeeData() {
        firestore.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                employeeList.clear()
                for (document in result) {
                    val employee = document.toObject(Employee::class.java)
                    employee?.let { employeeList.add(it) }
                }
                filteredEmployeeList.clear()
                filteredEmployeeList.addAll(employeeList)
                employeeListAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load employees: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchEmployees(query: String) {
        filteredEmployeeList.clear()
        if (query.isEmpty()) {
            filteredEmployeeList.addAll(employeeList)
        } else {
            for (employee in employeeList) {
                if (employee.name.contains(query, true)) {
                    filteredEmployeeList.add(employee)
                }
            }
        }
        employeeListAdapter.notifyDataSetChanged()
    }
}