package com.giphy.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.giphy.ui.main.MainActivity
import com.giphy.R
import com.google.android.material.textfield.TextInputLayout
import com.giphy.ui.base.BaseActivity
import com.giphy.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity: BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.hide()
        initializeTextChangedListeners()
        initializeClickListeners()
    }

    private fun initializeTextChangedListeners(){
        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                updateLoginButtonState()
            }
        })

        binding.emailEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            } else {
                showInputLayoutError(binding.inputLayoutEmail, null, false)
            }
        }

        binding.passwordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) = Unit

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().isEmpty()) {
                    showInputLayoutError(binding.inputLayoutPassword, getString(R.string.error_empty_field), true)
                } else {
                    showInputLayoutError(binding.inputLayoutPassword, null, false)
                }
                updateLoginButtonState()
            }
        })
    }

    private fun initializeClickListeners(){
        binding.loginButton.setOnClickListener {
            hideKeyboard(this)
            if (hasNetworkConnection()) {
                showProgressDialog(getString(R.string.message_title_login),getString(R.string.message_please_wait), indeterminate = false, cancelable = false)
                val mHandler1 = Handler()
                mHandler1.postDelayed({
                    hideProgressDialog()
                    goToActivity(MainActivity::class.java, arrayOf(Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                }, 1750)
            } else {
                showSimpleToast(resources.getString(R.string.message_not_connected))
            }
        }
    }

    private fun <T: Activity>goToActivity(classType: Class<T>, flags: Array<Int>) {
        if (flags.isEmpty())
            startActivity(Intent(this, classType))
        else {
            val intent = Intent(this, classType)
            flags.forEachIndexed { index, _ ->
                intent.addFlags(flags[index])
            }
            startActivity(intent)
        }
    }

    //region Form validations

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "(?=^.{5,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return password.length >= 5 && matcher.matches()
    }

    private fun validateEmail(): Boolean {
        val email = binding.emailEt.text.toString().trim()

        if(email.isEmpty()) {
            showInputLayoutError(binding.inputLayoutEmail, getString(R.string.error_empty_field), true)
            return false
        } else if (!isValidEmail(email)) {
            showInputLayoutError(binding.inputLayoutEmail, getString(R.string.error_wrong_email), true)
            return false
        }

        binding.inputLayoutEmail.isErrorEnabled = false
        return true
    }

    private fun updateLoginButtonState(){
        binding.loginButton.isEnabled = binding.emailEt.text?.length != 0
                && isValidEmail(binding.emailEt.text.toString())
                && binding.passwordEt.text?.length != 0
                && isValidPassword(binding.passwordEt.text.toString())
    }

    private fun showInputLayoutError(inputLayout: TextInputLayout, error: String?, isEnabled: Boolean){
        inputLayout.error = error
        inputLayout.isErrorEnabled = isEnabled
    }
    
    //endregion
}
