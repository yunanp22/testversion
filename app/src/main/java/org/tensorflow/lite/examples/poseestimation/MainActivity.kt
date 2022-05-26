package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_practice_list.*


class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var recordFragment: RecordFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val TAG = "Main"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bottomNavBar : BottomNavigationView = findViewById(R.id.bottom_nav)

        //첫 화면을 홈 프레그먼트로 설정
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, homeFragment).commit()

        recordFragment = RecordFragment.newInstance()
        historyFragment = HistoryFragment.newInstance()
        settingsFragment = SettingsFragment.newInstance()

        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@MainActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //bottom nav bar 리스너 설정
        bottomNavBar.setOnItemSelectedListener {  item ->
            when(item.itemId) {
                //홈 탭 클릭 시
                R.id.menu_home -> {

                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, homeFragment).commit()
                    true
                }
                //촬영 탭 클릭 시
                R.id.menu_record -> {

                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top).replace(R.id.fragments_frame, recordFragment).addToBackStack(null).commit()
                    true
                }
                R.id.menu_history -> {

                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, historyFragment).commit()
                    true
                }
                R.id.menu_settings -> {

                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, settingsFragment).commit()
                    true
                }
                else -> false
            }

        }
        setSupportActionBar(toolbar)
    }

    //요구한 모든 권한을 획득 했는지 확인
    private fun allPermissionsGranted() = MainActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            applicationContext, it) == PackageManager.PERMISSION_GRANTED
    }

}