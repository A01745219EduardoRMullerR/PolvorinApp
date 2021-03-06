package mx.itesm.Juanware.PolvorinApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenu : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        configMenu()

        mAuth = FirebaseAuth.getInstance()
    }


    private fun configureInitFrag(fragment_str : String) {
        when(fragment_str){
            "About"   -> putFragment(About())
            "Profile" -> putFragment(Profile())
            "Events"  -> putFragment(FragmentEventos())
            "Notif"   -> putFragment(FragNotificaciones())
            "Equipos" -> putFragment(FragEquipo(this))
            else      -> putFragment(Profile())
        }
    }

    private fun configMenu() {
        NavMenu.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.About -> {
                    configureInitFrag("About")
                }

                R.id.Profile -> {
                    println("Perfil")
                    configureInitFrag("Profile")
                }

                R.id.Events -> {
                    println("eventos")
                    //configureInitFrag("Events")
                    startEventosActivity()
                }
                R.id.Notifications -> {
                    println("Notificaciones")
                    configureInitFrag("Notif")
                }
                R.id.Teams -> {
                    println("Equipos")
                    configureInitFrag("Equipos")
                }
            }

            true

        }
    }

    private fun putFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment,frag)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun startEventosActivity(){

        val intent = Intent(this, eventosAct::class.java)
        startActivity(intent)

        putFragment(FragmentEventos())
    }

    fun hacerLogOut(v: View){
        val usuario = mAuth.currentUser
        AuthUI.getInstance().signOut(this)
        Handler().postDelayed(
                {
                    regresarLogin()
                }, 500
        )



    }

    fun regresarLogin(){
        val regresarLogin= Intent(this, Login::class.java)
        startActivity(regresarLogin)
        finish()
        println("Salio")
    }

    fun crearEquipo(v: View){
        val intentCE = Intent(this, agregarEquipo::class.java)
        startActivity(intentCE)
        finish()
    }

    /*fun crearEvento(v: View){
        val intentCE = Intent(this, agregarEvento::class.java)
        startActivity(intentCE)
        finish()
    }*/


}