package com.mmahdihx.instagramlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody

class MainActivity : AppCompatActivity() {
    var loginBtnEnabled: Boolean = true
    var compositeDisposable: CompositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addOnTextChangeListener()
        login.setOnClickListener {
            if (loginBtnEnabled) {
                val uuidString = "e53a38b4-fec9-4869-8fbb-b630c0459c34"
                val username = username.text.toString()
                val password = passowrd.text.toString()
                val data = hashMapOf<String,String>().apply {
                    put("uuid", uuidString)
                    put("username", username)
                    put("password", password)
                    put("device_id", uuidString)
                    put("from_reg", "false")
                    put("_csrftoken", "missing")
                    put("login_attempt_count", "0")
                }
                progressbar.visibility = View.VISIBLE
                setButtonEnabled(false)
                val callback = ApiClient().apiServiceInstance()
                    .create(ApiService::class.java).loginToInstagram(data)
                callback.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        progressbar.visibility = View.GONE
                        setButtonEnabled(true)
                    }
                    .subscribe(object : SingleObserver<Response> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }
                        override fun onSuccess(t: Response) {
                            showCustomToast("status:${t.status},message:${"${t.message}"}")
                        }

                        override fun onError(e: Throwable) {
                            showCustomToast("Error : ${e.message}")
                        }

                    })
            }
        }
    }

    fun setButtonEnabled(status:Boolean){
        if (status) {
            login.alpha = 1.0f
            loginBtnEnabled = true
        }else{
            login.alpha = 0.5f
            loginBtnEnabled = false
        }
    }
    fun addOnTextChangeListener() {
        if (username.text.isNotEmpty()){
            setButtonEnabled(true)
        }
        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val x = 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (username.text.isNotEmpty()) {
                    setButtonEnabled(true)
                } else if (username.text.isEmpty()) {
                    setButtonEnabled(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val x = 0
            }

        })
    }

    fun showCustomToast(message: String) {
        val customLayout = LayoutInflater.from(this).inflate(R.layout.view_message, null)
        val messageText = customLayout.findViewById<TextView>(R.id.message)
        messageText.text = message
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.view = customLayout
        toast.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}