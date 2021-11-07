package com.example.rsesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    val USER_KEY = "Users"
    val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)

    var firebaseAuth: FirebaseAuth? = null
    var login:TextInputLayout? = null
    var password: TextInputLayout? = null

    var sign_in_button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        Toast.makeText(this, "Войдите в свой аккаунт", Toast.LENGTH_SHORT).show()

        login = findViewById(R.id.name_form)
        password = findViewById(R.id.edit_password)
        sign_in_button = findViewById(R.id.sign_in)


        sign_in_button?.setOnClickListener{
            if(!login?.editText?.text.toString().isEmpty() && !password?.editText?.text.toString().isEmpty())
            {
                firebaseAuth?.signInWithEmailAndPassword(login?.editText?.text.toString(), password?.editText?.text.toString())?.addOnCompleteListener(object :
                    OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if(task.isSuccessful)
                        {
                            Toast.makeText(this@LoginActivity, "Вход выполнен", Toast.LENGTH_SHORT).show()
                            intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else Toast.makeText(applicationContext, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            else Toast.makeText(applicationContext, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
        }

    }
}