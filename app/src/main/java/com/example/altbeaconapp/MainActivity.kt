package com.example.altbeaconapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.altbeacon.beacon.*
import org.altbeacon.beacon.service.BeaconService

class MainActivity : AppCompatActivity(), BeaconConsumer {

    private var beaconManager: BeaconManager? = null
    // uuidの指定
    private val uuidString: String = "FFFE2D12-1E4B-0FA4-9F28-A317AE1848DE"
    private val major = Identifier.parse("6216")
    private val minor = Identifier.parse("56833")
    private val uuid = Identifier.parse(uuidString)
    // ビーコンのフォーマット設定
    private val IBEACON_FORMAT: String = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    private val region = Region("gaku-yamamoto-region", uuid,null,null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 端末のBLE対応チェック
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 未対応の場合、Toast表示
            showToast("このデバイスはBLE未対応です", Toast.LENGTH_LONG)
        } else {
            showToast("このデバイスはBLE対応です", Toast.LENGTH_LONG)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission()
        }
        beaconManager = BeaconManager.getInstanceForApplication(this)
        // BeaconParseを設定
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_FORMAT))
        startService(Intent(this,BeaconService().javaClass))
    }

    override fun onResume() {
        super.onResume()
        beaconManager!!.bind(this@MainActivity)
        Log.d("onResume","call onResume")
    }

    override fun onPause() {
        super.onPause()
        //beaconManager!!.unbind(this@MainActivity)
        //Log.d(" onPause","call  onPause")
    }

    /**************************************************
     * BeaconConsumer内のメソッドをoverride
     **************************************************/
    override fun onBeaconServiceConnect() {
        Log.d("","onBeaconServiceConnect")
        // なんか知らないけど２重で登録されてしまうのでここで一旦削除
        beaconManager?.removeAllMonitorNotifiers()
        beaconManager?.removeAllRangeNotifiers()
        beaconManager?.rangedRegions?.forEach {region ->
            beaconManager?.stopRangingBeaconsInRegion(region)
        }
        //BeaconManagerクラスのモニタリング設定
        beaconManager?.addMonitorNotifier(monitorNotifier)
        //BeaconManagerクラスのレンジング設定
        beaconManager?.addRangeNotifier(rangeNotifier)
        //領域監視の開始
        startMonitoringBeacons()
        //距離測定の開始
        startRangingBeaconsInRegion()
    }

    private fun startMonitoringBeacons(){
        Log.d("","startMonitoringBeacons")
        try {
            beaconManager?.startMonitoringBeaconsInRegion(region)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun startRangingBeaconsInRegion(){
        Log.d("","startRangingBeaconsInRegion")
        try {
            beaconManager?.startRangingBeaconsInRegion(region)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private val monitorNotifier = object : MonitorNotifier {
        override fun didEnterRegion(p0: Region?) {
            Log.d("","didEnterRegion")
            Log.d("",p0.toString())
        }
        override fun didExitRegion(p0: Region?) {
            Log.d("","didExitRegion")
            Log.d("",p0.toString())
        }
        override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
            Log.d("","didDetermineStateForRegion")
        }
    }

    private val rangeNotifier = RangeNotifier { beacons, region ->
        for (beacon in beacons) {
            Log.d("beacon",beacon.toString())
            Log.d("region",region.toString())
        }
    }


    // トースト表示のメソッド
    fun showToast(text: String, length: Int) {
        // トーストの生成と表示
        var toast: Toast = Toast.makeText(this, text, length)
        toast.show()
    }
    // パーミッションの許可チェック
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission() {
        // パーミッション未許可の時
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可ダイアログの表示
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }
    }
    fun viewUpdate(major: Int?, minor: Int?) {
        // Viewの取得
        var majorTextView: TextView = findViewById(R.id.major) as TextView
        var minorTextView: TextView = findViewById(R.id.minor) as TextView
        majorTextView.text = "major:" + major
        minorTextView.text = "minor:" + minor
    }
}