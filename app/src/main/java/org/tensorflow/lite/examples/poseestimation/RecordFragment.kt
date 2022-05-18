/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.*
import java.util.*

class RecordFragment : Fragment() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"

        fun newInstance() : RecordFragment {
            return RecordFragment()
        }
    }




    //영상 촬영 관련 변수들 선언

    private lateinit var safeContext: Context
    private lateinit var recordButton: ImageView
    private lateinit var closeButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // keep screen on while app is running
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvScore = view.rootView.findViewById(R.id.tvScore)
        tvTime =  view.rootView.findViewById(R.id.tvTime)
        tvFPS =  view.rootView.findViewById(R.id.tvFps)
        tvPoseName =  view.rootView.findViewById(R.id.tvPoseName)
        surfaceView =  view.rootView.findViewById(R.id.surfaceView)
        imgPose =  view.rootView.findViewById(R.id.imgPose)

        if (!isCameraPermissionGranted()) {
            requestPermission()
        }


        recordButton = view.findViewById(R.id.record_button)
        closeButton = view.findViewById(R.id.close_button)

        //닫기 버튼 클릭 리스너 설정
        closeButton.setOnClickListener {
            //홈 프레그먼트로 이동
            val navigation: BottomNavigationView = view.rootView.findViewById(R.id.bottom_nav)
            navigation.selectedItemId = R.id.menu_home
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결해주는 파트
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)

        return view
    }

//    private lateinit var relativeOrientation: OrientationLiveData



    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    private var num1 : MediaPlayer? = null
    private var num2 : MediaPlayer? = null
    private var num3 : MediaPlayer? = null
    private var num4 : MediaPlayer? = null
    private var num5 : MediaPlayer? = null
    private var address : MediaPlayer? = null
    private var push : MediaPlayer? = null
    private var down : MediaPlayer? = null
    private var back : MediaPlayer? = null
    private var forward : MediaPlayer? = null
    private var follow : MediaPlayer? = null

    /** Default device is CPU */
    private var device = Device.CPU

    var timerTask: Timer? = null
    var time = 0



    private lateinit var imgPose: ImageView
    private lateinit var tvScore: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvFPS: TextView
    private lateinit var tvPoseName: TextView
    private var cameraSource: CameraSource? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
//                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
//                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }


    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return  activity?.checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        num1 = MediaPlayer.create(this.safeContext, R.raw.one)
        num2 = MediaPlayer.create(this.safeContext, R.raw.two)
        num3 = MediaPlayer.create(this.safeContext, R.raw.three)
        num4 = MediaPlayer.create(this.safeContext, R.raw.four)
        num5 = MediaPlayer.create(this.safeContext, R.raw.five)
        address = MediaPlayer.create(this.safeContext, R.raw.address)
        push = MediaPlayer.create(this.safeContext, R.raw.push_away)
        back = MediaPlayer.create(this.safeContext, R.raw.back_swing)
        follow = MediaPlayer.create(this.safeContext, R.raw.follow_throw)
        forward = MediaPlayer.create(this.safeContext, R.raw.forward)
        down = MediaPlayer.create(this.safeContext, R.raw.down_swing)



//        tvPoseName.setText(String.format(getString(R.string.tfe_pe_tv_poseName, poseArray[1])))

        timerTask = kotlin.concurrent.timer(period = 100) {
            time++
            val sec = time / 10
            val fiveSec = time % 70
            if (fiveSec == 14 && sec <= 42) {
                num4?.start()
            } else if (fiveSec == 28 && sec <= 42) {
                num3?.start()
            } else if (fiveSec == 42 && sec <= 42) {
                num2?.start()
            } else if (fiveSec == 56 && sec <= 42) {
                num1?.start()
            }

            activity?.runOnUiThread {
                tvTime?.text = "시간: ${sec}"
                if (sec == 0) {
                    address?.start()
                    imgPose.setImageResource(R.drawable.pose1)
                    imgPose.visibility = View.VISIBLE
                    tvPoseName.text = getString(R.string.tv_poseName, "어드레스")
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(0).toString())
                }

                if(sec == 2){
                    imgPose.visibility = View.INVISIBLE
                }
                if (sec == 7) {
                    push?.start()
                    imgPose.setImageResource(R.drawable.pose2)
                    imgPose.visibility = View.VISIBLE
                    tvPoseName.text = getString(R.string.tv_poseName, "푸시어웨이")
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(0).toString())
                }
                if(sec == 9){
                    imgPose.visibility = View.INVISIBLE
                }
                if (sec == 14) {
                    down?.start()
                    imgPose.setImageResource(R.drawable.pose3)
                    imgPose.visibility = View.VISIBLE
                    tvPoseName.text = getString(R.string.tv_poseName, "다운스윙")
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(1).toString())
                }
                if(sec == 16){
                    imgPose.visibility = View.INVISIBLE
                }
                if (sec == 21) {
                    back?.start()
                    imgPose.setImageResource(R.drawable.pose4)
                    imgPose.visibility = View.VISIBLE
                    tvPoseName.text = getString(R.string.tv_poseName, "백스윙")
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(2).toString())
                }
                if(sec == 23){
                    imgPose.visibility = View.INVISIBLE
                }
                if (sec == 28) {
                    forward?.start()
                    imgPose.setImageResource(R.drawable.pose5)
                    imgPose.visibility = View.VISIBLE
                    tvPoseName.text = getString(R.string.tv_poseName, "포워드스윙")
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(3).toString())
                }
                if(sec == 30){
                    imgPose.visibility = View.INVISIBLE
                }
                if (sec == 35) {
                    follow?.start()
                    imgPose.setImageResource(R.drawable.pose6)
                    imgPose.visibility = View.VISIBLE
                    tvPoseName.text = getString(R.string.tv_poseName, "팔로스루")
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(4).toString())
                }
                if(sec == 37){
                    imgPose.visibility = View.INVISIBLE
                }
                if(sec==42){
                    Log.i("time", sec.toString())
                    Log.i("test", MoveNet.getBiggestScore(5).toString())
                }
            }
        }

            if (isCameraPermissionGranted()) {

                if (cameraSource == null) {
                    cameraSource =
                        CameraSource(surfaceView, object : CameraSource.CameraSourceListener {

                            override fun onFPSListener(fps: Int) {
                                tvFPS.text = getString(R.string.tv_fps, fps)
                            }

                            override fun onDetectedInfo(
                                personScore: Float?,
                                poseLabels: List<Pair<String, Float>>?

                            ) {
                                tvScore.text =
                                    getString(R.string.tv_score, personScore ?: 0f)
//
                            }

                        }).apply {
                            prepareCamera()
                        }
                    lifecycleScope.launch(Dispatchers.Main) {
                        cameraSource?.initCamera()
                    }
                }
                createPoseEstimator()
            }

    }


    private fun createPoseEstimator() {
        cameraSource?.setDetector(MoveNet.create(this.safeContext, device, ModelType.Lightning))
    }


    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this.safeContext,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this.safeContext, message, Toast.LENGTH_LONG).show()
    }

//    /**
//     * Shows an error message dialog.
//     */
//    class ErrorDialog : DialogFragment() {
//
//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
//            AlertDialog.Builder(activity)
//                .setMessage(requireArguments().getString(ARG_MESSAGE))
//                .setPositiveButton(android.R.string.ok) { _, _ ->
//                    // do nothing
//                }
//                .create()
//
//        companion object {
//
//            @JvmStatic
//            private val ARG_MESSAGE = "message"
//
//            @JvmStatic
//            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
//                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
//            }
//        }
//    }
}
