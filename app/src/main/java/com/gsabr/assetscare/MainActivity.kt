package com.gsabr.assetscare

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess



private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // Somente retrato

        setupPermissions()

        //Acesso direto à activiy revisão (retirar depois)
        //val intent = Intent(this@MainActivity, DadosActivity::class.java)
        //intent.putExtra("patrimonio", "1")
        //startActivity(intent)
        //...

        btn_buscar.setOnClickListener {
            val it = et_qrcode.text.toString()
            val intent = Intent(this@MainActivity, DadosActivity::class.java)
            intent.putExtra("patrimonio", it)
            startActivity(intent)
        }

        btn_scan.setOnClickListener{
            iv_logo.visibility = View.GONE
            tv_msg.visibility = View.VISIBLE
            scanner_view.visibility = View.VISIBLE
            btn_scan.visibility = View.GONE
            btn_cancelar.visibility = View.VISIBLE
            et_qrcode.visibility = View.GONE
            btn_buscar.visibility = View.GONE
            img_qr_instruction.visibility = View.VISIBLE

            btn_cancelar.setOnClickListener {
                iv_logo.visibility = View.VISIBLE
                tv_msg.visibility = View.INVISIBLE
                scanner_view.visibility = View.GONE
                btn_scan.visibility = View.VISIBLE
                btn_cancelar.visibility = View.GONE
                et_qrcode.visibility = View.VISIBLE
                btn_buscar.visibility = View.VISIBLE
                img_qr_instruction.visibility = View.GONE
            }
        }
        codeScanner()
    }

    private fun codeScanner(){

        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {

                runOnUiThread {

                    //tv_msg.text = it.text

                    val intent = Intent(this@MainActivity, DadosActivity::class.java)
                    intent.putExtra("patrimonio", it.text)
                    startActivity(intent)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Erro ao iniciar a câmera: ${it.message}")
                }
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume(){
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Você precisa de permissão de acesso a câmera para usar este aplicativo!", Toast.LENGTH_SHORT).show()
                }else{
                    //Successful
                }
            }
        }
    }
}