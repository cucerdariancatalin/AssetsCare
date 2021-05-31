package com.gsabr.assetscare
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_revisao.*

private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private val run = Run()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewGroupFirstScreen = arrayOf(iv_logo, btn_scan, et_qrcode, btn_buscar, btn_gerenciar, ll_ou)
        val viewGroupSecondScreen = arrayOf(scanner_view, img_qr_instruction, btn_cancelar, scanner_view, tv_msg)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // Somente retrato

        setupPermissions()

        //Acesso direto à activity revisão (retirar depois)
//        val intent = Intent(this@MainActivity, DadosActivity::class.java)
//        intent.putExtra("patrimonio", "1")
//        startActivity(intent)
        //...

        btn_gerenciar.setOnClickListener {
            val intent = Intent(this@MainActivity, GerenciarOsActivity::class.java)
            startActivity(intent)
        }

        btn_buscar.setOnClickListener {
            val it = et_qrcode.text.toString()
            val intent = Intent(this@MainActivity, DadosActivity::class.java)
            intent.putExtra("patrimonio", it)
            //et_qrcode.hideKeyboard()
            //et_codfunc.clearFocus()
            startActivity(intent)
        }

        btn_scan.setOnClickListener{
            //btn_scan.visibility = View.GONE
            run.changeView(viewGroupFirstScreen, View.GONE)
            run.changeView(viewGroupSecondScreen, View.VISIBLE)
        }

        codeScanner()

        btn_cancelar.setOnClickListener {
            run.changeView(viewGroupFirstScreen, View.VISIBLE)
            run.changeView(arrayOf(tv_msg), View.INVISIBLE)
            run.changeView(viewGroupSecondScreen, View.GONE)
        }
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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