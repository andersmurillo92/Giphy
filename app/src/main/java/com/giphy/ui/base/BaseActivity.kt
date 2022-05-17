package com.giphy.ui.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.giphy.R
import kotlinx.android.synthetic.main.layout_simple_toast.view.*

open class BaseActivity: AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showProgressDialog(title: String?, message: String, indeterminate: Boolean, cancelable: Boolean){
        progressDialog = ProgressDialog.show(this@BaseActivity, title, message, indeterminate, cancelable)
    }

    fun hideProgressDialog() {
        if(progressDialog.isShowing)
            progressDialog.dismiss()
    }

    fun showSimpleToast(content: String) {

        val inflater = LayoutInflater.from(this@BaseActivity)
        val layout = inflater.inflate(R.layout.layout_simple_toast, null)
        layout.tv_ad.text = content

        val mToast = Toast(this@BaseActivity)
        mToast.setGravity(Gravity.CENTER, 0, 0)
        mToast.duration = Toast.LENGTH_LONG
        mToast.view = layout
        mToast.show()
    }

    fun hasNetworkConnection(): Boolean {
        var result = false
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected)
            result = true
        return result
    }
}