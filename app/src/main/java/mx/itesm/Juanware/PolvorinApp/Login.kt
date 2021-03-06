package mx.itesm.Juanware.PolvorinApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class Login : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val RC_SIGN_IN = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        val usuario = mAuth.currentUser
        if (usuario != null) {
            //ya esta firmado

            val intMainMenu = Intent(this, MainMenu::class.java)
            startActivity(intMainMenu)
            finish()
            println("hay usuario")
        }else {
            println("No hay usuario")
        }
    }

    fun autenticar(v: View){
        autenticar()
    }


    fun autenticar(){
        val proveedores = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(proveedores)
                        .build(),
                RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val response = FirebaseAuth.getInstance().currentUser
            when(resultCode){
                RESULT_OK -> {
                    //Para recibir usuario
                    //val usuario = FirebaseAuth.getInstance().currentUser

                    val intMainMenu = Intent(this, MainMenu::class.java)
                    startActivity(intMainMenu)
                    println("Estoy en AactivityResult")
                    finish()

                }
                RESULT_CANCELED -> {
                    println("Cancelado (back)")
                }
                else -> {
                    val response = IdpResponse.fromResultIntent(data)
                    println("Error: ${response?.error?.errorCode}")
                }
            }
        }
    }
}