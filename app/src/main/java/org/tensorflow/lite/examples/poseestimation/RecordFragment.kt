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
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.data.VowlingPose
import org.tensorflow.lite.examples.poseestimation.ml.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class RecordFragment : Fragment() {

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

        fun newInstance() : RecordFragment {
            return RecordFragment()
        }

        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480

        private const val RECORDER_VIDEO_BITRATE: Int = 10_000_000

    }

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

    /*
    녹화코드
     */
    /** File where the recording will be saved */
    private lateinit var outputFile: File

    private lateinit var recorderSurface: Surface

    /** Saves the video recording */
    private lateinit var recorder: MediaRecorder

    /** Creates a [MediaRecorder] instance using the provided [Surface] as input */
    private fun createRecorder(surface: Surface) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setVideoSize(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        setVideoFrameRate(30)
        setOutputFile(outputFile.absolutePath)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        setInputSurface(surface)
    }

    /** Creates a [File] named with the current date and time */
    private fun createFile(context: Context, extension: String): File {

        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "VID_${sdf.format(Date())}.$extension")
//            return  File(context.getExternalFilesDir(null), "VID_${sdf.format(Date())}.$extension")
    }

    private lateinit var characteristics: CameraCharacteristics

    //영상 촬영 관련 변수들 선언

    private lateinit var safeContext: Context
    private lateinit var recordButton: ImageView
    private lateinit var closeButton: ImageView
    private lateinit var bottomSheet: LinearLayoutCompat

    private var isRecording = false

    private lateinit var relativeOrientation: OrientationLiveData

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


    /** 자세별 점수 */
    private var addressScore: Float = 0.0f
    private var pushawayScore: Float = 0.0f
    private var downswingScore: Float = 0.0f
    private var backswingScore: Float = 0.0f
    private var forwardswingScore: Float = 0.0f
    private var followthroughScore: Float = 0.0f

    /** 자세별 각도 차이*/
    private var addressAngleDifferences = FloatArray(4)
    private var pushawayAngleDifferences = FloatArray(5)
    private var downswingAngleDifferences = FloatArray(5)
    private var backswingAngleDifferences = FloatArray(5)
    private var forwardswingAngleDifferences = FloatArray(5)
    private var followthroughAngleDifferences = FloatArray(5)

    var resultBitmap: Bitmap? = null

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

        //Bottom Sheet 위치 설정(MainActivity의 Bottom Navigation Bar와 위치가 충돌되는 문제 예방)
        bottomSheet = view.rootView.findViewById<LinearLayoutCompat>(R.id.score_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.isGestureInsetBottomIgnored = true

        // 권한을 다 얻었다면 카메라를 시작함
        if (allPermissionsGranted()) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }


        recordButton = view.findViewById(R.id.record_button)
        closeButton = view.findViewById(R.id.close_button)

        //촬영 버튼 클릭 리스너 설정
        recordButton.setOnClickListener {
            if (isRecording) {
                showToast("촬영이 완료되었습니다.")
                recorder.stop()
                imgPose.visibility = View.INVISIBLE
                timerTask?.cancel()
                time = 0
                tvTime.text = "시간: 0"

                // Broadcasts the media file to the rest of the system
                MediaScannerConnection.scanFile(
                    surfaceView.context, arrayOf(outputFile.absolutePath), null, null
                )

                val intent = Intent(safeContext, ResultPopupActivity::class.java)
                intent.putExtra("addressScore", addressScore)
                intent.putExtra("pushawayScore", pushawayScore)
                intent.putExtra("downswingScore", downswingScore)
                intent.putExtra("backswingScore", backswingScore)
                intent.putExtra("forwardswingScore", forwardswingScore)
                intent.putExtra("followthroughScore", followthroughScore)
                val stream = ByteArrayOutputStream()
                resultBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)


                intent.putExtra("bitmap", stream.toByteArray())
                startActivity(intent)


                recordButton.setImageResource(R.drawable.ic_record_btn)
            } else {
//                MoveNet.getBitmap().clear()
//                MoveNet.getRightAlbowAngles()[0].clear()
//                MoveNet.getRightHipAngles()[0].clear()
//                MoveNet.getRightKneeAngles()[0].clear()
//                MoveNet.getRightShoulderAngles()[0].clear()


                outputFile = createFile(safeContext, "mp4")
                recorder = createRecorder(recorderSurface)

//                    requestedOrientation =
//                        ActivityInfo.SCREEN_ORIENTATION_LOCKED

                relativeOrientation.value?.let { recorder.setOrientationHint(it) }
                recorder.prepare()
                recorder.start()

                timerTask = kotlin.concurrent.timer(period = 100) {
                    time++
                    Log.d(TAG, "onViewCreated: $time")
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
                        }

                        if(sec == 2){
                            imgPose.visibility = View.INVISIBLE
                        }

                        if (sec == 7) {
                            /** 정확도가 가장 높았던 각도*/
                            val pose_address = VowlingPose(90.0f, 0.0f, 160.0f, 160.0f)
                            val listSize = MoveNet.getRightElbowAngles()[0].size
                            var maxScore = pose_address.getScore(MoveNet.getRightElbowAngles()[0][0], MoveNet.getRightShoulderAngles()[0][0], MoveNet.getRightHipAngles()[0][0], MoveNet.getRightKneeAngles()[0][0])
                            var maxScoreIndex = 0
                            var count = 1
                            for(i in 1 until listSize) {
                                if(maxScore < pose_address.getScore(MoveNet.getRightElbowAngles()[0][i], MoveNet.getRightShoulderAngles()[0][i], MoveNet.getRightHipAngles()[0][i], MoveNet.getRightKneeAngles()[0][i])) {
                                    maxScore = pose_address.getScore(MoveNet.getRightElbowAngles()[0][i], MoveNet.getRightShoulderAngles()[0][i], MoveNet.getRightHipAngles()[0][i], MoveNet.getRightKneeAngles()[0][i])
                                    maxScoreIndex = count
                                }
                                count++
                            }
                            addressScore = maxScore
                            resultBitmap = MoveNet.getBitmap()[maxScoreIndex]

                            addressAngleDifferences[0] = getAngleDifference(pose_address.getREA(), MoveNet.getRightElbowAngles()[0][maxScoreIndex])
                            addressAngleDifferences[1] = getAngleDifference(pose_address.getRSA(), MoveNet.getRightElbowAngles()[0][maxScoreIndex])
                            addressAngleDifferences[2] = getAngleDifference(pose_address.getRHA(), MoveNet.getRightElbowAngles()[0][maxScoreIndex])
                            addressAngleDifferences[3] = getAngleDifference(pose_address.getRKA(), MoveNet.getRightElbowAngles()[0][maxScoreIndex])

                            Log.i("score : ", addressScore.toString())

                            push?.start()
                            imgPose.setImageResource(R.drawable.pose2)
                            imgPose.visibility = View.VISIBLE
                            tvPoseName.text = getString(R.string.tv_poseName, "푸시어웨이")
                        }

                        if(sec == 9){
                            imgPose.visibility = View.INVISIBLE
                        }

                        if (sec == 14) {
                            val pose_pushaway = VowlingPose(105.0f, 15.0f, 150.0f, 150.0f, 150.0f)
                            val listSize = MoveNet.getRightElbowAngles()[1].size
                            var maxScore = pose_pushaway.getScore(MoveNet.getRightElbowAngles()[1][0], MoveNet.getRightShoulderAngles()[1][0], MoveNet.getRightHipAngles()[1][0], MoveNet.getRightKneeAngles()[1][0], MoveNet.getLeftKneeAngles()[0][0])
                            var maxScoreIndex = 0
                            var count = 1
                            for(i in 1 until listSize) {
                                if(maxScore < pose_pushaway.getScore(MoveNet.getRightElbowAngles()[1][i], MoveNet.getRightShoulderAngles()[1][i], MoveNet.getRightHipAngles()[1][i], MoveNet.getRightKneeAngles()[1][i], MoveNet.getLeftKneeAngles()[0][i])) {
                                    maxScore = pose_pushaway.getScore(MoveNet.getRightElbowAngles()[1][i], MoveNet.getRightShoulderAngles()[1][i], MoveNet.getRightHipAngles()[1][i], MoveNet.getRightKneeAngles()[1][i], MoveNet.getLeftKneeAngles()[0][i])
                                    maxScoreIndex = count
                                }
                                count++
                            }
                            pushawayScore = maxScore
                            resultBitmap = MoveNet.getBitmap()[maxScoreIndex]

                            pushawayAngleDifferences[0] = getAngleDifference(pose_pushaway.getREA(), MoveNet.getRightElbowAngles()[1][maxScoreIndex])
                            pushawayAngleDifferences[1] = getAngleDifference(pose_pushaway.getRSA(), MoveNet.getRightElbowAngles()[1][maxScoreIndex])
                            pushawayAngleDifferences[2] = getAngleDifference(pose_pushaway.getRHA(), MoveNet.getRightElbowAngles()[1][maxScoreIndex])
                            pushawayAngleDifferences[3] = getAngleDifference(pose_pushaway.getRKA(), MoveNet.getRightElbowAngles()[1][maxScoreIndex])
                            pushawayAngleDifferences[4] = getAngleDifference(pose_pushaway.getLKA(), MoveNet.getRightElbowAngles()[0][maxScoreIndex])

                            down?.start()
                            imgPose.setImageResource(R.drawable.pose3)
                            imgPose.visibility = View.VISIBLE
                            tvPoseName.text = getString(R.string.tv_poseName, "다운스윙")
                        }

                        if(sec == 16){
                            imgPose.visibility = View.INVISIBLE
                        }

                        if (sec == 21) {
                            val pose_downswing = VowlingPose(180.0f, 10.0f, 170.0f, 150.0f, 150.0f)
                            val listSize = MoveNet.getRightElbowAngles()[2].size
                            var maxScore = pose_downswing.getScore(MoveNet.getRightElbowAngles()[2][0], MoveNet.getRightShoulderAngles()[2][0], MoveNet.getRightHipAngles()[2][0], MoveNet.getRightKneeAngles()[2][0], MoveNet.getLeftKneeAngles()[1][0])
                            var maxScoreIndex = 0
                            var count = 1
                            for(i in 1 until listSize) {
                                if(maxScore < pose_downswing.getScore(MoveNet.getRightElbowAngles()[2][i], MoveNet.getRightShoulderAngles()[2][i], MoveNet.getRightHipAngles()[2][i], MoveNet.getRightKneeAngles()[2][i], MoveNet.getLeftKneeAngles()[1][i])) {
                                    maxScore = pose_downswing.getScore(MoveNet.getRightElbowAngles()[2][i], MoveNet.getRightShoulderAngles()[2][i], MoveNet.getRightHipAngles()[2][i], MoveNet.getRightKneeAngles()[2][i], MoveNet.getLeftKneeAngles()[1][i])
                                    maxScoreIndex = count
                                }
                                count++
                            }
                            downswingScore = maxScore
                            resultBitmap = MoveNet.getBitmap()[maxScoreIndex]

                            downswingAngleDifferences[0] = getAngleDifference(pose_downswing.getREA(), MoveNet.getRightElbowAngles()[2][maxScoreIndex])
                            downswingAngleDifferences[1] = getAngleDifference(pose_downswing.getRSA(), MoveNet.getRightElbowAngles()[2][maxScoreIndex])
                            downswingAngleDifferences[2] = getAngleDifference(pose_downswing.getRHA(), MoveNet.getRightElbowAngles()[2][maxScoreIndex])
                            downswingAngleDifferences[3] = getAngleDifference(pose_downswing.getRKA(), MoveNet.getRightElbowAngles()[2][maxScoreIndex])
                            downswingAngleDifferences[4] = getAngleDifference(pose_downswing.getLKA(), MoveNet.getRightElbowAngles()[1][maxScoreIndex])

                            back?.start()
                            imgPose.setImageResource(R.drawable.pose4)
                            imgPose.visibility = View.VISIBLE
                            tvPoseName.text = getString(R.string.tv_poseName, "백스윙")
                        }

                        if(sec == 23){
                            imgPose.visibility = View.INVISIBLE
                        }

                        if (sec == 28) {
                            val pose_backswing = VowlingPose(180.0f, 60.0f, 110.0f, 130.0f, 130.0f)
                            val listSize = MoveNet.getRightElbowAngles()[3].size
                            var maxScore = pose_backswing.getScore(MoveNet.getRightElbowAngles()[3][0], MoveNet.getRightShoulderAngles()[3][0], MoveNet.getRightHipAngles()[3][0], MoveNet.getRightKneeAngles()[3][0], MoveNet.getLeftKneeAngles()[2][0])
                            var maxScoreIndex = 0
                            var count = 1
                            for(i in 1 until listSize) {
                                if(maxScore < pose_backswing.getScore(MoveNet.getRightElbowAngles()[3][i], MoveNet.getRightShoulderAngles()[3][i], MoveNet.getRightHipAngles()[3][i], MoveNet.getRightKneeAngles()[3][i], MoveNet.getLeftKneeAngles()[2][i])) {
                                    maxScore = pose_backswing.getScore(MoveNet.getRightElbowAngles()[3][i], MoveNet.getRightShoulderAngles()[3][i], MoveNet.getRightHipAngles()[3][i], MoveNet.getRightKneeAngles()[3][i], MoveNet.getLeftKneeAngles()[2][i])
                                    maxScoreIndex = count
                                }
                                count++
                            }
                            backswingScore = maxScore
                            resultBitmap = MoveNet.getBitmap()[maxScoreIndex]

                            backswingAngleDifferences[0] = getAngleDifference(pose_backswing.getREA(), MoveNet.getRightElbowAngles()[3][maxScoreIndex])
                            backswingAngleDifferences[1] = getAngleDifference(pose_backswing.getRSA(), MoveNet.getRightElbowAngles()[3][maxScoreIndex])
                            backswingAngleDifferences[2] = getAngleDifference(pose_backswing.getRHA(), MoveNet.getRightElbowAngles()[3][maxScoreIndex])
                            backswingAngleDifferences[3] = getAngleDifference(pose_backswing.getRKA(), MoveNet.getRightElbowAngles()[3][maxScoreIndex])
                            backswingAngleDifferences[4] = getAngleDifference(pose_backswing.getLKA(), MoveNet.getRightElbowAngles()[2][maxScoreIndex])

                            forward?.start()
                            imgPose.setImageResource(R.drawable.pose5)
                            imgPose.visibility = View.VISIBLE
                            tvPoseName.text = getString(R.string.tv_poseName, "포워드스윙")
                        }

                        if(sec == 30){
                            imgPose.visibility = View.INVISIBLE
                        }

                        if (sec == 35) {
                            val pose_forwardswing = VowlingPose(180.0f, 30.0f, 175.0f, 170.0f, 80.0f)
                            val listSize = MoveNet.getRightElbowAngles()[4].size
                            var maxScore = pose_forwardswing.getScore(MoveNet.getRightElbowAngles()[4][0], MoveNet.getRightShoulderAngles()[4][0], MoveNet.getRightHipAngles()[4][0], MoveNet.getRightKneeAngles()[4][0], MoveNet.getLeftKneeAngles()[3][0])
                            var maxScoreIndex = 0
                            var count = 1
                            for(i in 1 until listSize) {
                                if(maxScore < pose_forwardswing.getScore(MoveNet.getRightElbowAngles()[4][i], MoveNet.getRightShoulderAngles()[4][i], MoveNet.getRightHipAngles()[4][i], MoveNet.getRightKneeAngles()[4][i], MoveNet.getLeftKneeAngles()[3][i])) {
                                    maxScore = pose_forwardswing.getScore(MoveNet.getRightElbowAngles()[4][i], MoveNet.getRightShoulderAngles()[4][i], MoveNet.getRightHipAngles()[4][i], MoveNet.getRightKneeAngles()[4][i], MoveNet.getLeftKneeAngles()[3][i])
                                    maxScoreIndex = count
                                }
                                count++
                            }
                            forwardswingScore = maxScore
                            resultBitmap = MoveNet.getBitmap()[maxScoreIndex]

                            forwardswingAngleDifferences[0] = getAngleDifference(pose_forwardswing.getREA(), MoveNet.getRightElbowAngles()[4][maxScoreIndex])
                            forwardswingAngleDifferences[1] = getAngleDifference(pose_forwardswing.getRSA(), MoveNet.getRightElbowAngles()[4][maxScoreIndex])
                            forwardswingAngleDifferences[2] = getAngleDifference(pose_forwardswing.getRHA(), MoveNet.getRightElbowAngles()[4][maxScoreIndex])
                            forwardswingAngleDifferences[3] = getAngleDifference(pose_forwardswing.getRKA(), MoveNet.getRightElbowAngles()[4][maxScoreIndex])
                            forwardswingAngleDifferences[4] = getAngleDifference(pose_forwardswing.getLKA(), MoveNet.getRightElbowAngles()[3][maxScoreIndex])

                            follow?.start()
                            imgPose.setImageResource(R.drawable.pose6)
                            imgPose.visibility = View.VISIBLE
                            tvPoseName.text = getString(R.string.tv_poseName, "팔로스루")
                        }

                        if(sec == 37){
                            imgPose.visibility = View.INVISIBLE
                        }

                        if(sec==42){
                            val pose_followthrough = VowlingPose(160.0f, 160.0f, 175.0f, 180.0f, 100.0f)
                            val listSize = MoveNet.getRightElbowAngles()[5].size
                            var maxScore = pose_followthrough.getScore(MoveNet.getRightElbowAngles()[5][0], MoveNet.getRightShoulderAngles()[5][0], MoveNet.getRightHipAngles()[5][0], MoveNet.getRightKneeAngles()[5][0], MoveNet.getLeftKneeAngles()[4][0])
                            var maxScoreIndex = 0
                            var count = 1
                            for(i in 1 until listSize) {
                                if(maxScore < pose_followthrough.getScore(MoveNet.getRightElbowAngles()[5][i], MoveNet.getRightShoulderAngles()[5][i], MoveNet.getRightHipAngles()[5][i], MoveNet.getRightKneeAngles()[5][i], MoveNet.getLeftKneeAngles()[4][i])) {
                                    maxScore = pose_followthrough.getScore(MoveNet.getRightElbowAngles()[5][i], MoveNet.getRightShoulderAngles()[5][i], MoveNet.getRightHipAngles()[5][i], MoveNet.getRightKneeAngles()[5][i], MoveNet.getLeftKneeAngles()[4][i])
                                    maxScoreIndex = count
                                }
                                count++
                            }
                            followthroughScore = maxScore
                            resultBitmap = MoveNet.getBitmap()[maxScoreIndex]

                            followthroughAngleDifferences[0] = getAngleDifference(pose_followthrough.getREA(), MoveNet.getRightElbowAngles()[5][maxScoreIndex])
                            followthroughAngleDifferences[1] = getAngleDifference(pose_followthrough.getRSA(), MoveNet.getRightElbowAngles()[5][maxScoreIndex])
                            followthroughAngleDifferences[2] = getAngleDifference(pose_followthrough.getRHA(), MoveNet.getRightElbowAngles()[5][maxScoreIndex])
                            followthroughAngleDifferences[3] = getAngleDifference(pose_followthrough.getRKA(), MoveNet.getRightElbowAngles()[5][maxScoreIndex])
                            followthroughAngleDifferences[4] = getAngleDifference(pose_followthrough.getLKA(), MoveNet.getRightElbowAngles()[4][maxScoreIndex])
                        }
                    }
                }

                Log.d(TAG, "Recording started")

                recordButton.setImageResource(R.drawable.ic_record_btn_red)

                showToast("촬영이 시작되었습니다.")

            }

            isRecording = !isRecording
        }

        //닫기 버튼 클릭 리스너 설정
        closeButton.setOnClickListener {
            //홈 프레그먼트로 이동
            val navigation: BottomNavigationView = view.rootView.findViewById(R.id.bottom_nav)
            navigation.selectedItemId = R.id.menu_home
        }

    }

    private fun getAngleDifference(correctAngle: Float, myAngle: Float): Float{
        return myAngle - correctAngle
    }

    private fun getNearValue(targetList: Array<ArrayList<Float>>,
                             value: Float, p1: Int): Float {
        var temp: Float
        var min = Float.MAX_VALUE
        var nearValue = 0.0f

        for (j in 0 .. 9) {
            Log.d(TAG, "getNearValue: ${targetList[p1][j]}")
        }
        val size = targetList[p1].size
        for (i in 0 until size) {
            temp = abs(targetList[p1][i] - value)
            if (min > temp) {
                min = temp
                nearValue = targetList[p1][i]
            }
        }
        return nearValue
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
        timerTask?.cancel()
        timerTask = null
        time = 0
        super.onPause()
    }

    override fun onDestroy() {
        cameraSource?.close()
        cameraSource = null
        recorder.reset()
        recorder.release()
        timerTask?.cancel()
        timerTask = null
        time = 0
        super.onDestroy()
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

        if (isCameraPermissionGranted()) {

            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {

//                            override fun onFPSListener(fps: Int) {
////                                tvFPS.text = getString(R.string.tv_fps, fps)
////                                tvTime.text = getString(R.string.tv_time, time)
//                            }

                        override fun onTimeListener(time: Int) {
                            tvFPS.text = getString(R.string.tv_time, time)
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
                        // Used to rotate the output media to match device orientation
                        relativeOrientation = OrientationLiveData(requireContext(), prepareCamera()).apply {
                            observe(viewLifecycleOwner, Observer {
                                    orientation -> Log.d(TAG, "Orientation changed: $orientation")
                            })
                        }
                        recorderSurface = getRecordingSurface()
                        setOrientation(relativeOrientation)

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

    //요구한 모든 권한을 획득 했는지 확인
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            safeContext, it) == PackageManager.PERMISSION_GRANTED
    }

    //카메라 및 저장 권한 획득 여부에 따른 결과 처리 함수
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openCamera()
            } else {
                Toast.makeText(safeContext,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
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
