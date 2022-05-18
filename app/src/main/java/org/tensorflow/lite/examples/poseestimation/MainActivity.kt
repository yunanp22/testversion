package org.tensorflow.lite.examples.poseestimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_practice_list.*


class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var recordFragment: RecordFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment

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

}