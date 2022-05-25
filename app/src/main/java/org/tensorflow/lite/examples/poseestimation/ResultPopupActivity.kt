package org.tensorflow.lite.examples.poseestimation

import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.MoveNet

class ResultPopupActivity: AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var okButton: Button
    private lateinit var comment: TextView
    private lateinit var imageView: ImageView
    private lateinit var surfaceView: SurfaceView

//    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)

    private var isTrackerEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultpopup)

        spinner = findViewById(R.id.spinner)
        okButton = findViewById(R.id.result_ok)
        comment = findViewById<TextView>(R.id.result_comment)
        imageView = findViewById(R.id.result_posture_image)
//        surfaceView = findViewById(R.id.result_posture_surface)

        // Create an ArrayAdapter
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.pose_list, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter


        var score1 = intent.getFloatExtra("addressScore", 0.0f)
        var score2 = intent.getFloatExtra("pushawayScore", 0.0f)
        var score3 = intent.getFloatExtra("downswingScore", 0.0f)
        var score4 = intent.getFloatExtra("backswingScore", 0.0f)
        var score5 = intent.getFloatExtra("forwardswingScore", 0.0f)
        var score6 = intent.getFloatExtra("followthroughScore", 0.0f)
        var bitmap = intent.getByteArrayExtra("bitmap")

        var addressAngleDiffrences = intent.getFloatArrayExtra("addressAngleDifferences")
        var pushawayAngleDifferences = intent.getFloatArrayExtra("pushawayAngleDifferences")
        var downswingAngleDifferences = intent.getFloatArrayExtra("downswingAngleDifferences")
        var backswingAngleDifferences = intent.getFloatArrayExtra("backswingAngleDifferences")
        var forwardswingAngleDifferences = intent.getFloatArrayExtra("forwardswingAngleDifferences")
        var followthroughAngleDifferences = intent.getFloatArrayExtra("followthroughAngleDifferences")

        Log.d("add", "$addressAngleDiffrences")

        val decodedBitmap = BitmapFactory.decodeByteArray(bitmap, 0, bitmap!!.size)
//        imageView.setImageBitmap(decodedBitmap)

//        val rotateMatrix = Matrix()
//        rotateMatrix.postRotate(90.0f)
//        var decodedImage = Bitmap.createBitmap(decodedBitmap, 0,0, decodedBitmap.width, decodedBitmap.height, rotateMatrix, false)



//        var canvas = surfaceView.holder.lockCanvas()
//        surfaceView.setBackgroundColor(Color.RED)
        val persons = mutableListOf<Person>()

        MoveNet.create(this@ResultPopupActivity, Device.CPU, ModelType.Lightning).estimatePoses(decodedBitmap)?.let {
            persons.addAll(it)
        }
        Log.d("TAG", "onCreate: ${persons.size}")
            var keyPointLine = visualize(persons, decodedBitmap)
        imageView.setImageBitmap(keyPointLine)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (getSelectedSpinnerItem() == 0) {
                        comment.text = "어드레스 점수: $score1"
                        comment.text = "오른쪽 팔꿈치 각도 차이: ${addressAngleDiffrences!![0]}"
                        comment.text = "오른쪽 어깨 각도 차이: ${addressAngleDiffrences!![1]}"
                        comment.text = "오른쪽 골반 각도 차이: ${addressAngleDiffrences!![2]}"
                        comment.text = "오른쪽 무릎 각도 차이: ${addressAngleDiffrences!![3]}"
                    } else if (getSelectedSpinnerItem() == 1) {
                        comment.text = "푸쉬어웨이 점수: $score2"
                        comment.text = "오른쪽 팔꿈치 각도 차이: ${pushawayAngleDifferences!![0]}"
                        comment.text = "오른쪽 어깨 각도 차이: ${pushawayAngleDifferences!![1]}"
                        comment.text = "오른쪽 골반 각도 차이: ${pushawayAngleDifferences!![2]}"
                        comment.text = "오른쪽 무릎 각도 차이: ${pushawayAngleDifferences!![3]}"
                        comment.text = "왼쪽 무릎 각도 차이: ${pushawayAngleDifferences!![4]}"
                    } else if (getSelectedSpinnerItem() == 2) {
                        comment.text = "다운스윙 점수: $score3"
                        comment.text = "오른쪽 팔꿈치 각도 차이: ${downswingAngleDifferences!![0]}"
                        comment.text = "오른쪽 어깨 각도 차이: ${downswingAngleDifferences!![1]}"
                        comment.text = "오른쪽 골반 각도 차이: ${downswingAngleDifferences!![2]}"
                        comment.text = "오른쪽 무릎 각도 차이: ${downswingAngleDifferences!![3]}"
                        comment.text = "왼쪽 무릎 각도 차이: ${downswingAngleDifferences!![4]}"
                    } else if (getSelectedSpinnerItem() == 3) {
                        comment.text = "백스윙 점수: $score4"
                        comment.text = "오른쪽 팔꿈치 각도 차이: ${backswingAngleDifferences!![0]}"
                        comment.text = "오른쪽 어깨 각도 차이: ${backswingAngleDifferences!![1]}"
                        comment.text = "오른쪽 골반 각도 차이: ${backswingAngleDifferences!![2]}"
                        comment.text = "오른쪽 무릎 각도 차이: ${backswingAngleDifferences!![3]}"
                        comment.text = "왼쪽 무릎 각도 차이: ${backswingAngleDifferences!![4]}"
                    } else if (getSelectedSpinnerItem() == 4) {
                        comment.text = "포워드 점수: $score5"
                        comment.text = "오른쪽 팔꿈치 각도 차이: ${forwardswingAngleDifferences!![0]}"
                        comment.text = "오른쪽 어깨 각도 차이: ${forwardswingAngleDifferences!![1]}"
                        comment.text = "오른쪽 골반 각도 차이: ${forwardswingAngleDifferences!![2]}"
                        comment.text = "오른쪽 무릎 각도 차이: ${forwardswingAngleDifferences!![3]}"
                        comment.text = "왼쪽 무릎 각도 차이: ${forwardswingAngleDifferences!![4]}"
                    } else {
                        comment.text = "팔로우스루 점수: $score6"
                        comment.text = "오른쪽 팔꿈치 각도 차이: ${followthroughAngleDifferences!![0]}"
                        comment.text = "오른쪽 어깨 각도 차이: ${followthroughAngleDifferences!![1]}"
                        comment.text = "오른쪽 골반 각도 차이: ${followthroughAngleDifferences!![2]}"
                        comment.text = "오른쪽 무릎 각도 차이: ${followthroughAngleDifferences!![3]}"
                        comment.text = "왼쪽 무릎 각도 차이: ${followthroughAngleDifferences!![4]}"
                    }
                }
            }


            okButton.setOnClickListener {
                finish()
            }
        }




    private fun visualize(persons: List<Person>, bitmap: Bitmap): Bitmap {

        val outputBitmap = VisualizationUtils.drawBodyKeypointsByScore(
            bitmap,
            persons, false,
            50.0f
        )

        return outputBitmap
    }


    fun getSelectedSpinnerItem(): Int {
        return spinner.selectedItemPosition
    }
}


//
//package org.tensorflow.lite.examples.poseestimation
//
//import android.graphics.*
//import android.media.Image
//import android.os.Bundle
//import android.util.Log
//import android.view.SurfaceView
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
//import org.tensorflow.lite.examples.poseestimation.data.Device
//import org.tensorflow.lite.examples.poseestimation.data.Person
//import org.tensorflow.lite.examples.poseestimation.ml.ModelType
//import org.tensorflow.lite.examples.poseestimation.ml.MoveNet
//
//class ResultPopupActivity: AppCompatActivity() {
//
//    private lateinit var spinner: Spinner
//    private lateinit var okButton: Button
//    private lateinit var comment: TextView
//    private lateinit var imageView: ImageView
//    private lateinit var surfaceView: SurfaceView
//
////    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)
//
//    private var isTrackerEnabled = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_resultpopup)
//
//        spinner = findViewById(R.id.spinner)
//        okButton = findViewById(R.id.result_ok)
//        comment = findViewById<TextView>(R.id.result_comment)
//        imageView = findViewById(R.id.result_posture_image)
////        surfaceView = findViewById(R.id.result_posture_surface)
//
//        // Create an ArrayAdapter
//        val adapter = ArrayAdapter.createFromResource(this,
//            R.array.pose_list, android.R.layout.simple_spinner_item)
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        // Apply the adapter to the spinner
//        spinner.adapter = adapter
//
//        var score1 = intent.getFloatExtra("addressScore", 0.0f)
//        var score2 = intent.getFloatExtra("pushawayScore", 0.0f)
//        var score3 = intent.getFloatExtra("downswingScore", 0.0f)
//        var score4 = intent.getFloatExtra("backswingScore", 0.0f)
//        var score5 = intent.getFloatExtra("forwardswingScore", 0.0f)
//        var score6 = intent.getFloatExtra("followthroughScore", 0.0f)
//        var addressBitmap = intent.getByteArrayExtra("addressbitmap")
//        var pushawayBitmap = intent.getByteArrayExtra("pushawaybitmap")
//        var downswingBitmap = intent.getByteArrayExtra("downswingbitmap")
//        var backswingBitmap = intent.getByteArrayExtra("backswingbitmap")
//        var forwardswingBitmap = intent.getByteArrayExtra("forwardswingbitmap")
//        var followthroughBitmap = intent.getByteArrayExtra("followthroughbitmap")
//
//
////        imageView.setImageBitmap(setBitmap(addressBitmap))
//
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (getSelectedSpinnerItem() == 0) {
//                    comment.text = "어드레스 점수: $score1"
//                    setBitmap(addressBitmap)
//                } else if (getSelectedSpinnerItem() == 1) {
//                    comment.text = "푸쉬어웨이 점수: $score2"
//                    setBitmap(pushawayBitmap)
//                } else if (getSelectedSpinnerItem() == 2) {
//                    comment.text = "다운스윙 점수: $score3"
//                    setBitmap(downswingBitmap)
//                } else if (getSelectedSpinnerItem() == 3) {
//                    comment.text = "백스윙 점수: $score4"
//                    setBitmap(backswingBitmap)
//                } else if (getSelectedSpinnerItem() == 4) {
//                    comment.text = "포워드 점수: $score5"
//                    setBitmap(forwardswingBitmap)
//                } else {
//                    comment.text = "팔로우스루 점수: $score6"
//                    setBitmap(followthroughBitmap)
//                }
//            }
//
//        }
//
//        okButton.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun setBitmap(bitmap: ByteArray?){
//
//        if(bitmap == null){
//        }
//
//        val decodedBitmap = BitmapFactory.decodeByteArray(bitmap, 0, bitmap!!.size)
//
//        val persons = mutableListOf<Person>()
//
//        MoveNet.create(this@ResultPopupActivity, Device.CPU, ModelType.Lightning).estimatePoses(decodedBitmap)?.let {
//            persons.addAll(it)
//        }
//        Log.d("TAG", "onCreate: ${persons.size}")
//        val resultBitmap = visualize(persons, decodedBitmap)
//        imageView.setImageBitmap(resultBitmap)
//
//    }
//
//    private fun visualize(persons: List<Person>, bitmap: Bitmap): Bitmap {
//
//        val outputBitmap = VisualizationUtils.drawBodyKeypointsByScore(
//            bitmap,
//            persons, false,
//            50.0f
//        )
//
//        return outputBitmap
//    }
//
//
//    fun getSelectedSpinnerItem(): Int {
//        return spinner.selectedItemPosition
//    }
//}
//
