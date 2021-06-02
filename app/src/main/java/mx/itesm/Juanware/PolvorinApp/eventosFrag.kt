package mx.itesm.Juanware.PolvorinApp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_eventos.*
import kotlinx.android.synthetic.main.fragment_eventos.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

lateinit var arrEventos: MutableList<Evento>
private lateinit var mAuth: FirebaseAuth
class eventosFrag : Fragment(), clickListenerEventos {


    private lateinit var baseDatos: FirebaseDatabase
    var rango = 10.0
    var isMyEvents = false
    var isParticipo = false
    var usuario = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseDatos = FirebaseDatabase.getInstance()
        arrEventos = mutableListOf()

        mAuth = FirebaseAuth.getInstance()
        this.usuario = mAuth.currentUser.uid


        //grabarEnBD(1,  "Evento1")
        //grabarEnBD(2, "Evento2")
        //grabarEnBD(3, "Evento3")
        //grabarEnBD(4, "Evento4")
        println("onCreate")

    }


    fun loadPreferences() {
        val pref = activity?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        //val prefMyEvents = activity?.getSharedPreferences("myEvents", Context.MODE_PRIVATE)
        //val prefParticipo = activity?.getSharedPreferences("participo", Context.MODE_PRIVATE)
        if (pref != null) {
             this.rango = (pref.getInt("DIST_KEY",10) * 1000).toDouble()

             if (pref.getInt("MY_EVENTS_KEY",0) == 1) this.isMyEvents = true
             if (pref.getInt("PARTICIPO_KEY",0) == 1) this.isParticipo = true

        }

    }



    fun leerDatos(rvTarjetas: RecyclerView) {
        val baseDatos = FirebaseDatabase.getInstance()
        val referencia = baseDatos.getReference("/Eventos/")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {

                arrEventos.clear()
                for (registro in snapshot.children) {
                    val dist = FloatArray(3)

                    val latEvento = (registro.child("latEvento").value).toString().toDouble()
                    val longEvento = (registro.child("longEvento").value).toString().toDouble()
                    Location.distanceBetween(latitud, longitude, latEvento, longEvento, dist)

                    if (dist[0] <= rango) {


                        val tiempoActual = LocalDateTime.now()
                        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        tiempoActual.format(formato)
                        val dia = (registro.child("fechaHora").child(("0")).value).toString().toInt()
                        val mes = (registro.child("fechaHora").child(("1")).value).toString().toInt()
                        val anio = (registro.child("fechaHora").child(("2")).value).toString().toInt()
                        val hora = (registro.child("fechaHora").child(("3")).value).toString().toInt()
                        val minuto = (registro.child("fechaHora").child(("4")).value).toString().toInt()
                        val stringFechaHora = "$dia/$mes/$anio $hora:$minuto" as CharSequence


                        val tiempo = LocalDateTime.of(anio, mes, dia, hora, minuto)
                        tiempo.format(formato)

                        if (tiempo.isAfter(tiempoActual)){

                            val participantes = (registro.child("participantes").value) as List<String>
                            val idCreadorEvento = (registro.child("idCreadorEvento").value).toString()

                            if (isParticipo){

                                if(isMyEvents){
                                    if(idCreadorEvento.equals(usuario)){
                                        val nombreEvento = (registro.child("nombreEvento").value).toString()
                                        val descripcionEvento =
                                            (registro.child("descripcionEvento").value).toString()
                                        val tipoEvento = (registro.child("tipoEvento").value).toString()
                                        val maxParticiantes =
                                            (registro.child("maxParticipantes").value).toString().toInt()

                                        val nombreParticipantes = (registro.child("nombreParticipantes").value)
                                        val idEvento = (registro.child("idEvento").value).toString()

                                        var participantesActuales =
                                            registro.child("participantes").childrenCount.toInt()
                                        val fechaHora = (registro.child("fechaHora").value)

                                        //println("participantes hasta ahora: $participantesActuales")
                                        //println(participantes)

                                        val evento = Evento(
                                            nombreEvento, descripcionEvento,
                                            tipoEvento, idCreadorEvento, latEvento,
                                            longEvento, maxParticiantes, participantes as ArrayList<String>,
                                            idEvento, nombreParticipantes as ArrayList<String>,
                                            fechaHora as ArrayList<Int>)
                                        arrEventos.add(evento)
                                    }
                                }

                                else if(participantes.contains(usuario)){
                                    val nombreEvento = (registro.child("nombreEvento").value).toString()
                                    val descripcionEvento =
                                        (registro.child("descripcionEvento").value).toString()
                                    val tipoEvento = (registro.child("tipoEvento").value).toString()
                                    val maxParticiantes =
                                        (registro.child("maxParticipantes").value).toString().toInt()

                                    val nombreParticipantes = (registro.child("nombreParticipantes").value)
                                    val idEvento = (registro.child("idEvento").value).toString()

                                    var participantesActuales =
                                        registro.child("participantes").childrenCount.toInt()
                                    val fechaHora = (registro.child("fechaHora").value)

                                    //println("participantes hasta ahora: $participantesActuales")
                                    //println(participantes)

                                    val evento = Evento(
                                        nombreEvento, descripcionEvento,
                                        tipoEvento, idCreadorEvento, latEvento,
                                        longEvento, maxParticiantes, participantes as ArrayList<String>,
                                        idEvento, nombreParticipantes as ArrayList<String>,
                                        fechaHora as ArrayList<Int>)
                                    arrEventos.add(evento)
                                }


                            }

                            else if (isMyEvents && !isParticipo){
                                if(idCreadorEvento.equals(usuario)){
                                    val nombreEvento = (registro.child("nombreEvento").value).toString()
                                    val descripcionEvento =
                                        (registro.child("descripcionEvento").value).toString()
                                    val tipoEvento = (registro.child("tipoEvento").value).toString()
                                    val maxParticiantes =
                                        (registro.child("maxParticipantes").value).toString().toInt()

                                    val nombreParticipantes = (registro.child("nombreParticipantes").value)
                                    val idEvento = (registro.child("idEvento").value).toString()

                                    var participantesActuales =
                                        registro.child("participantes").childrenCount.toInt()
                                    val fechaHora = (registro.child("fechaHora").value)

                                    //println("participantes hasta ahora: $participantesActuales")
                                    //println(participantes)

                                    val evento = Evento(
                                        nombreEvento, descripcionEvento,
                                        tipoEvento, idCreadorEvento, latEvento,
                                        longEvento, maxParticiantes, participantes as ArrayList<String>,
                                        idEvento, nombreParticipantes as ArrayList<String>,
                                        fechaHora as ArrayList<Int>)
                                    arrEventos.add(evento)
                                }
                            }

                            else{
                                val nombreEvento = (registro.child("nombreEvento").value).toString()
                                val descripcionEvento =
                                    (registro.child("descripcionEvento").value).toString()
                                val tipoEvento = (registro.child("tipoEvento").value).toString()
                                val idCreadorEvento = (registro.child("idCreadorEvento").value).toString()
                                val maxParticiantes =
                                    (registro.child("maxParticipantes").value).toString().toInt()

                                val nombreParticipantes = (registro.child("nombreParticipantes").value)
                                val idEvento = (registro.child("idEvento").value).toString()

                                var participantesActuales =
                                    registro.child("participantes").childrenCount.toInt()
                                val fechaHora = (registro.child("fechaHora").value)

                                //println("participantes hasta ahora: $participantesActuales")
                                //println(participantes)

                                val evento = Evento(
                                    nombreEvento, descripcionEvento,
                                    tipoEvento, idCreadorEvento, latEvento,
                                    longEvento, maxParticiantes, participantes as ArrayList<String>,
                                    idEvento, nombreParticipantes as ArrayList<String>,
                                    fechaHora as ArrayList<Int>)
                                arrEventos.add(evento)
                            }

                        }




                    }


                }
                (rvTarjetas.adapter as AdaptadorEventos).notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
                println("ERROR EN BASE DE DATOS")
            }
        })
    }


    private fun configurarRV(rvTarjetas: RecyclerView){
        println("setup RB")
        val layoutManager = LinearLayoutManager(this.context)
        rvTarjetas.layoutManager = layoutManager
        var adaptador  = AdaptadorEventos(arrEventos)
        rvTarjetas.adapter=adaptador
        adaptador.listener = this
        adaptador.notifyDataSetChanged()
        println("configurarRv")
        leerDatos(rvTarjetas)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crearEventobutton.setOnClickListener{
            activity?.let {
                val intent = Intent(it, agregarEvento::class.java)
                it.startActivity(intent)
            }
        }
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("ONCREATEVIEW")

        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_eventos, container, false)
        var rvTarjetas: RecyclerView = view.findViewById(R.id.recyclerViewTarjetas)


        loadPreferences()

        configurarRV(rvTarjetas)
        return view
    }

    override fun clicked(position: Int) {
        println("Click en un evento ${position}")
        activity?.let {
            val intent = Intent(it, DetallesEvento::class.java)
            intent.putExtra("EVENTO", position)
            it.startActivity(intent)
        }
    }
    override fun onStop(){
        super.onStop()
    }
}

