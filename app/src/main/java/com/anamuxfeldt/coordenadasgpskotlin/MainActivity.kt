package com.anamuxfeldt.coordenadasgpskotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    var permissoesRequeridas = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION

    )

    val APP_PERMISSOES_ID = 2023
    var txtValorLatitude: TextView? = null
    var txtValorLongitude: TextView? = null

    var latitude: Double = 0.00
    var longitude: Double = 0.00
    var locationManager: LocationManager? = null

    var gpsAtivo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtValorLatitude = findViewById(R.id.txtValorLatitude)
        txtValorLongitude = findViewById(R.id.txtValorLongitude)

        locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager

        gpsAtivo = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsAtivo) {
            obterCoordenadas()
        } else {
            latitude = 0.00
            longitude = 0.00

            txtValorLatitude!!.setText(formatarGeopoint(latitude))
            txtValorLongitude!!.setText(formatarGeopoint(longitude))
        Toast.makeText(this, "Coordenadas não disponíveis", Toast.LENGTH_LONG).show()
        }

    }

    private fun obterCoordenadas() {

        val permissaoAtiva: Boolean = solicitarPermissaoParaObterLocalizacao()
        if (permissaoAtiva) {
            capturarUltimaLocalizacaoValida()
        }else{
            solicitarPermissaoParaObterLocalizacao()
        }

    }

    private fun solicitarPermissaoParaObterLocalizacao(): Boolean {

        Toast.makeText(
            this, "Verificando permissões...",
            Toast.LENGTH_LONG).show()

        val permissoesNegadas: MutableList<String> =
            ArrayList()

        var permissaoNegada: Int
        for (permissao in permissoesNegadas) {

            permissaoNegada = ContextCompat.checkSelfPermission(this@MainActivity, permissao)

            if (permissaoNegada != PackageManager.PERMISSION_GRANTED) {
                permissoesNegadas.add(permissao)
            }
        }


        return if (!permissoesNegadas.isEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                permissoesNegadas.toTypedArray(), APP_PERMISSOES_ID
            )
            false
        } else
            true
    }

    private fun capturarUltimaLocalizacaoValida() {

        @SuppressLint("MissingPermission")
        val location = locationManager!!.getLastKnownLocation(
            LocationManager.GPS_PROVIDER

        )
        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude

        } else {
            latitude = 0.00
            longitude = 0.00
        }

        txtValorLatitude!!.setText(formatarGeopoint(latitude))
        txtValorLongitude!!.setText(formatarGeopoint(longitude))
        Toast.makeText(this, "Coordenadas obtidas com sucesso", Toast.LENGTH_LONG).show()
    }

    private fun formatarGeopoint(valor: Double): String? {
        val decimalFormat = DecimalFormat("#.####")


        return decimalFormat.format(valor)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        val sydney = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(sydney).title("Celular está aqui"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.uiSettings.isZoomControlsEnabled
        mMap.setMinZoomPreference(9.5f)


    }
}