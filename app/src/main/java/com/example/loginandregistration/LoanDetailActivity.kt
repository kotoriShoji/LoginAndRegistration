package com.example.loginandregistration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.example.loginandregistration.databinding.ActivityLoanDetailBinding
import java.util.*

class LoanDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoanDetailBinding
    var loanIsEditable = false
    lateinit var loan : LoanData

    companion object {
        const val EXTRA_LOAN = "loan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loan = intent.getParcelableExtra<LoanData>(EXTRA_LOAN) ?: LoanData()
        binding.textViewLoanDetailName.text = loan.name
        binding.textViewLoanDetailAmountPaid.text = "$${loan.repaid}"
        binding.textViewLoanDetailDate.text = dateFormater(loan.dateLent)
        binding.textViewLoanDetailDateLastRepaid.text = dateFormater(loan.dateRepaid)
        binding.textViewLoanDetailDescription.text = loan.description
        binding.textViewLoanDetailAmountOwed.text = "$${loan.balanceRemaining()}"
        binding.textViewLoanDetailInitialLoan.text = "$${loan.amount}"
        if(loan.isRepaid) {
            binding.imageViewLoanDetailStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.textViewLoanDetailName.context, R.drawable.ic_baseline_check_24))
        }
        else {
            binding.imageViewLoanDetailStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.textViewLoanDetailName.context, R.drawable.ic_baseline_clear_24))
        }
    }

    fun dateFormater(date: Date): String {
        return "${date.month}/${date.date}/${date.year + 1900} ${date.hours}:${date.minutes}"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_loan_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_item_loan_detail_edit -> {
                toggleEditable()
                true
            }
            R.id.menu_item_loan_detail_delete -> {
                deleteFromBackendless()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteFromBackendless() {
        Backendless.Data.of(LoanData::class.java).remove( loan,
            object : AsyncCallback<Long?> {
                override fun handleResponse(response: Long?) {
                    // Person has been deleted. The response is the
                    // time in milliseconds when the object was deleted
                    Toast.makeText(this@LoanDetailActivity, "${loan.name} Deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun handleFault(fault: BackendlessFault) {
                    Log.d("BirthdayDetail", "handleFault: ${fault.message}")
                }
            })
    }

    private fun toggleEditable() {
        if (loanIsEditable) {
            loanIsEditable = false
            binding.buttonLoanDetailSave.isEnabled = false
            binding.buttonLoanDetailSave.visibility = View.GONE
            binding.imageViewLoanDetailStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.textViewLoanDetailName.context, R.drawable.ic_baseline_clear_24))
            binding.editTextLoanDetailName.inputType = InputType.TYPE_NULL
            binding.editTextLoanDetailName.isEnabled = false
            binding.editTextLoanDetailAmountRepaid.inputType = InputType.TYPE_NULL
            binding.editTextLoanDetailAmountRepaid.isEnabled = false
            binding.editTextLoanDetailInitialLoan.inputType = InputType.TYPE_NULL
            binding.editTextLoanDetailInitialLoan.isEnabled = false
            //binding.checkBoxLoanDetailIsFullyRepaid.isClickable = false
        } else {
            loanIsEditable = false
            binding.buttonLoanDetailSave.isEnabled = true
            binding.buttonLoanDetailSave.visibility = View.VISIBLE
            binding.imageViewLoanDetailStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.textViewLoanDetailName.context, R.drawable.ic_baseline_check_24))
            binding.editTextLoanDetailName.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            binding.editTextLoanDetailName.isEnabled = true
            binding.editTextLoanDetailAmountRepaid.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            binding.editTextLoanDetailAmountRepaid.isEnabled = true
            binding.editTextLoanDetailInitialLoan.inputType = InputType.TYPE_NUMBER_VARIATION_NORMAL
            binding.editTextLoanDetailInitialLoan.isEnabled = true
            //binding.checkBoxLoanDetailIsFullyRepaid.isClickable = true
        }
    }
}