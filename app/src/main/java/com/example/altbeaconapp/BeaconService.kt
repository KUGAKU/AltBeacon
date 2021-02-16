package com.example.altbeaconapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap

class BeaconService: Service(), BootstrapNotifier {

    private var beaconManager: BeaconManager? = null
    private val uuidString: String = "FFFE2D12-1E4B-0FA4-9F28-A317AE1848DE"
    private val major = Identifier.parse("6216")
    private val minor = Identifier.parse("56833")
    private val uuid = Identifier.parse(uuidString)
    private val region = Region("gaku-yamamoto-region-background", uuid,null,null)
    private val IBEACON_FORMAT: String = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"

    override fun onCreate() {
        super.onCreate()
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_FORMAT))
        val regionBootstrap = RegionBootstrap(this, region)
        print("start BeaconService !!!")
        android.os.Debug.waitForDebugger()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun didEnterRegion(p0: Region?) {
        Log.d("","Enter Region" + p0)
        val intent = Intent(this, MainActivity().javaClass)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun didExitRegion(p0: Region?) {
        Log.d("","Exit Region" + p0)
    }

    override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
        Log.d("","Determine State: "+ p0)
    }

}