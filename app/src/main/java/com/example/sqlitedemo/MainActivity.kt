package com.example.sqlitedemo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import java.util.zip.DataFormatException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListOfDataIntoRecyclerView()
        btnAdd.setOnClickListener {
            addRecord()
            closeKeyboard()
            setupListOfDataIntoRecyclerView()
        }

    }

    private fun addRecord() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val databaseHandler: DatabaseHandler =
            DatabaseHandler(this)
        if(!name.isEmpty() && !email.isEmpty()){
            val status = databaseHandler.addEmployee(EmpModelClass(0, name, email))
            if(status > -1){
                Toast.makeText(this, "Record Saved", Toast.LENGTH_LONG).show()
                etName.text.clear()
                etEmail.text.clear()
            }
        }else{
            Toast.makeText(this, "Name or Email cannot be blank", Toast.LENGTH_LONG).show()
        }
    }
    /**
     * Method untuk mendapatkan jumlah record
     */
    private fun getItemsList(): ArrayList<EmpModelClass>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<EmpModelClass> = databaseHandler.viewEmployee()
        return empList
    }
    /**
     * Method untuk menampilkan empList ke recyclerView
     */
    private fun setupListOfDataIntoRecyclerView(){
        if(getItemsList().size > 0){
            rvItemList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            rvItemList.layoutManager = LinearLayoutManager(this)
            rvItemList.adapter = ItemAdapter(this, getItemsList())
        }else{
            rvItemList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }
    /**
     * Method untuk menampilkan dialog konfirmasi delete
     */
    fun deleteRecordAlertDialog(empModelClass: EmpModelClass){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")
        builder.setMessage("Are You Sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //Menampilkan tombol Yes
        builder.setPositiveButton("Yes") { dialog: DialogInterface, which ->
            val databaseHandler : DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(EmpModelClass(empModelClass.id, "", ""))

            if(status > -1){
                Toast.makeText(this, "Record deleted successfully", Toast.LENGTH_LONG).show()
                setupListOfDataIntoRecyclerView()
            }

            dialog.dismiss()
        }
        //Menampilkan tombol No
        builder.setNegativeButton("No") {dialog: DialogInterface, which ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        //Memastikan user menekan tombol Yes atau No
        alertDialog.setCancelable(false)
        //Menampilkan kotak dialog
        alertDialog.show()
    }
    /**
     * Method to show custom update dialog
     */
    fun updateRecordDialog(empModelClass: EmpModelClass){
        val updateDialog = Dialog(this, R.style.Theme_Dialog)

        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.etUpdateName.setText(empModelClass.name)
        updateDialog.etUpdateEmail.setText(empModelClass.email)

        updateDialog.tvUpdate.setOnClickListener {
            val name = updateDialog.etUpdateName.text.toString()
            val email = updateDialog.etUpdateEmail.text.toString()

            val databaseHandler : DatabaseHandler = DatabaseHandler(this)

            if(!name.isEmpty() && !email.isEmpty()){
                val status = databaseHandler.updateEmployee(EmpModelClass(empModelClass.id, name, email))
                if (status > -1){
                    Toast.makeText(this, "Record updated", Toast.LENGTH_LONG).show()
                    setupListOfDataIntoRecyclerView()
                    updateDialog.dismiss()
                    closeKeyboard()
                }
            }else{
                Toast.makeText(this,"Name or Email cannot be blank", Toast.LENGTH_LONG).show()
            }

        }
        updateDialog.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }
    /**
     * Method to close keyboard
     */
    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}