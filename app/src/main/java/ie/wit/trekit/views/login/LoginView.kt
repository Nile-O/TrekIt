package ie.wit.trekit.views.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ie.wit.trekit.databinding.ActivityLoginBinding

class LoginView: AppCompatActivity() {
    lateinit var presenter: LoginPresenter
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = LoginPresenter(this)

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.GONE



        binding.signUp.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email == "" || password == "") {
                Snackbar.make(binding.root, "please provide email and password", Snackbar.LENGTH_LONG)
                    .show()
            }
            else {
                presenter.doSignUp(email,password)
            }
        }

        binding.logIn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email == "" || password == "") {
                Snackbar.make(binding.root, "please provide email and password", Snackbar.LENGTH_LONG)
                    .show()
            }
            else {
                presenter.doLogin(email,password)
            }
        }
    }
     fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

     fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    fun showSnackBar(message: CharSequence){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .show()
    }
}