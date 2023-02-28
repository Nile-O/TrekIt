package ie.wit.trekit.views.login

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import ie.wit.trekit.activities.MainActivity
import ie.wit.trekit.activities.MountainListActivity
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainFireStore

class LoginPresenter (val view: LoginView) {
    private lateinit var loginIntentLauncher : ActivityResultLauncher<Intent>
    var app: MainApp = view.application as MainApp
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var fireStore: MountainFireStore? = null
    init{
        registerLoginCallback()
        if (app.mountains is MountainFireStore) {
            fireStore = app.mountains as MountainFireStore
        }
    }

    fun doLogin(email: String, password: String) {
        view.showProgress()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                if (fireStore != null) {
                    fireStore!!.fetchMountains {
                        view?.hideProgress()
                        val launcherIntent = Intent(view, MainActivity::class.java)
                        loginIntentLauncher.launch(launcherIntent)
                    }
                } else {
                    view?.hideProgress()
                    val launcherIntent = Intent(view, MainActivity::class.java)
                    loginIntentLauncher.launch(launcherIntent)
                }
            }else {
                view?.hideProgress()
                view.showSnackBar("Login failed: ${task.exception?.message}")
            }
            view.hideProgress()
        }
    }

    fun doSignUp(email: String, password: String) {
        view.showProgress()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                val launcherIntent = Intent(view, MainActivity::class.java)
                loginIntentLauncher.launch(launcherIntent)
    } else {
                view.showSnackBar("Login failed: ${task.exception?.message}")
            }
            view.hideProgress()
        }
    }

    private fun registerLoginCallback() {
        loginIntentLauncher = view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {}
    }
}
