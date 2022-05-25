package org.tensorflow.lite.examples.poseestimation

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.MoveNet

class ResultPopupActivity: AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var okButton: Button
    private lateinit var comment: TextView
    private lateinit var imageView: ImageView
    private lateinit var wrongAngleDifference1: TextView
    private lateinit var wrongAngleDifference2: TextView
    private lateinit var wrongAngleDifference3: TextView
    private lateinit var wrongAngleDifference4: TextView
    private lateinit var wrongAngleDifference5: TextView
    private lateinit var feedback: TextView
    private lateinit var surfaceView: SurfaceView

//    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)

    private var isTrackerEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultpopup)

        spinner = findViewById(R.id.spinner)
        okButton = findViewById(R.id.result_ok)
        comment = findViewById<TextView>(R.id.result_comment)
        wrongAngleDifference1 = findViewById<TextView>(R.id.result_wrongAngle1)
        wrongAngleDifference2 = findViewById<TextView>(R.id.result_wrongAngle2)
        wrongAngleDifference3 = findViewById<TextView>(R.id.result_wrongAngle3)
        wrongAngleDifference4 = findViewById<TextView>(R.id.result_wrongAngle4)
        wrongAngleDifference5 = findViewById<TextView>(R.id.result_wrongAngle5)
        feedback = findViewById<TextView>(R.id.feedback)
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

                    wrongAngleDifference1.text = null
                    wrongAngleDifference2.text = null
                    wrongAngleDifference3.text = null
                    wrongAngleDifference4.text = null
                    wrongAngleDifference5.text = null
                    feedback.text = null
                    if (getSelectedSpinnerItem() == 0) {
                        comment.text = "어드레스 점수: $score1"
                        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${addressAngleDiffrences!![0]}\n오른쪽 어깨 각도 차이: ${addressAngleDiffrences!![1]}\n오른쪽 어깨 각도 차이: ${addressAngleDiffrences!![2]}\nwrongAngleDifference4.text = \"오른쪽 무릎 각도 차이: ${addressAngleDiffrences!![3]}\""
//                        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${addressAngleDiffrences!![1]}"
//                        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${addressAngleDiffrences!![2]}"
//                        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${addressAngleDiffrences!![3]}"
                        //각도가 많이 다를 상황에서의 피드백
                        feedbackAddressAngleDiffernce(addressAngleDiffrences)
                    } else if (getSelectedSpinnerItem() == 1) {
                        comment.text = "푸쉬어웨이 점수: $score2"
                        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${pushawayAngleDifferences!![0]}"
                        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${pushawayAngleDifferences!![1]}"
                        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${pushawayAngleDifferences!![2]}"
                        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${pushawayAngleDifferences!![3]}"
                        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${pushawayAngleDifferences!![4]}"

                        feedbackPushAngleDiffernce(pushawayAngleDifferences)
                    } else if (getSelectedSpinnerItem() == 2) {
                        comment.text = "다운스윙 점수: $score3"
                        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${downswingAngleDifferences!![0]}"
                        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${downswingAngleDifferences!![1]}"
                        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${downswingAngleDifferences!![2]}"
                        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${downswingAngleDifferences!![3]}"
                        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${downswingAngleDifferences!![4]}"
                        feedbackDownAngleDiffernce(downswingAngleDifferences)
                    } else if (getSelectedSpinnerItem() == 3) {
                        comment.text = "백스윙 점수: $score4"
                        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${backswingAngleDifferences!![0]}"
                        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${backswingAngleDifferences!![1]}"
                        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${backswingAngleDifferences!![2]}"
                        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${backswingAngleDifferences!![3]}"
                        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${backswingAngleDifferences!![4]}"
                        feedbackBackAngleDiffernce(backswingAngleDifferences)
                    } else if (getSelectedSpinnerItem() == 4) {
                        comment.text = "포워드 점수: $score5"
                        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${forwardswingAngleDifferences!![0]}"
                        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${forwardswingAngleDifferences!![1]}"
                        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${forwardswingAngleDifferences!![2]}"
                        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${forwardswingAngleDifferences!![3]}"
                        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${forwardswingAngleDifferences!![4]}"
                        feedbackForwardAngleDiffernce(forwardswingAngleDifferences)
                    } else {
                        comment.text = "팔로우스루 점수: $score6"
                        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${followthroughAngleDifferences!![0]}"
                        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${followthroughAngleDifferences!![1]}"
                        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${followthroughAngleDifferences!![2]}"
                        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${followthroughAngleDifferences!![3]}"
                        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${followthroughAngleDifferences!![4]}"
                        feedbackFollowAngleDiffernce(followthroughAngleDifferences)
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

    fun feedbackAddressAngleDiffernce(addressAngleDifferences: FloatArray) {
        if (addressAngleDifferences!![0] >= 10.0 || addressAngleDifferences[0] <= -10.0) {
            feedback.text = "어드레스 자세에서 오른쪽 팔꿈치가 많이 벌어졌어요.. 이러면 공이 뭐 어떻게 되요\n"
        }
        if (addressAngleDifferences!![1] >= 10.0 || addressAngleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}어드레스 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (addressAngleDifferences!![2] >= 10.0 || addressAngleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}어드레스 오른쪽 골반 각도가 잘못됐당\n"
        }
        if (addressAngleDifferences!![3] >= 10.0 || addressAngleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}어드레스 오른쪽 무릎 각도가 잘못됐당\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackPushAngleDiffernce(pushawayAngleDifferences: FloatArray) {
        if (pushawayAngleDifferences!![0] >= 10.0 || pushawayAngleDifferences[0] <= -10.0) {
            feedback.text = "푸시 자세에서 오른쪽 팔꿈치가 많이 벌어졌어요.. 이러면 공이 뭐 어떻게 되요\n"
        }
        if (pushawayAngleDifferences!![1] >= 10.0 || pushawayAngleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}푸시 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (pushawayAngleDifferences!![2] >= 10.0 || pushawayAngleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}푸시 오른쪽 골반 각도가 잘못됐당\n"
        }
        if (pushawayAngleDifferences!![3] >= 10.0 || pushawayAngleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}푸시 오른쪽 무릎 각도가 잘못됐당\n"
        }
        if (pushawayAngleDifferences!![4] >= 10.0 || pushawayAngleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}푸시 왼쪽 무릎 각도가 잘못됐당\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackDownAngleDiffernce(angleDifferences: FloatArray) {
        if (angleDifferences!![0] >= 10.0 || angleDifferences[0] <= -10.0) {
            feedback.text = "다운스윙 자세에서 오른쪽 팔꿈치가 많이 벌어졌어요.. 이러면 공이 뭐 어떻게 되요\n"
        }
        if (angleDifferences!![1] >= 10.0 || angleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}다운스윙 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (angleDifferences!![2] >= 10.0 || angleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}다운스윙 오른쪽 골반 각도가 잘못됐당\n"
        }
        if (angleDifferences!![3] >= 10.0 || angleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}다운스윙 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (angleDifferences!![4] >= 10.0 || angleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}다운스윙 왼쪽 무릎 각도가 잘못됐당\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackBackAngleDiffernce(AngleDifferences: FloatArray) {
        if (AngleDifferences!![0] >= 10.0 || AngleDifferences[0] <= -10.0) {
            feedback.text = "백스윙 자세에서 오른쪽 팔꿈치가 많이 벌어졌어요.. 이러면 공이 뭐 어떻게 되요\n"
        }
        if (AngleDifferences!![1] >= 10.0 || AngleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}백스윙 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (AngleDifferences!![2] >= 10.0 || AngleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}백스윙 오른쪽 골반 각도가 잘못됐당\n"
        }
        if (AngleDifferences!![3] >= 10.0 || AngleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}백스윙 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (AngleDifferences!![4] >= 10.0 || AngleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}백스윙 푸시 왼쪽 무릎 각도가 잘못됐당\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackForwardAngleDiffernce(angleDifferences: FloatArray) {
        if (angleDifferences!![0] >= 10.0 || angleDifferences[0] <= -10.0) {
            feedback.text = "포워드 자세에서 오른쪽 팔꿈치가 많이 벌어졌어요.. 이러면 공이 뭐 어떻게 되요\n"
        }
        if (angleDifferences!![1] >= 10.0 || angleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}포워드 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (angleDifferences!![2] >= 10.0 || angleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}포워드 오른쪽 골반 각도가 잘못됐당\n"
        }
        if (angleDifferences!![3] >= 10.0 || angleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}포워드 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (angleDifferences!![4] >= 10.0 || angleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}포워드 왼쪽 무릎 각도가 잘못됐당\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackFollowAngleDiffernce(angleDifferences: FloatArray) {
        if (angleDifferences!![0] >= 10.0 || angleDifferences[0] <= -10.0) {
            feedback.text = "팔로스루 자세에서 오른쪽 팔꿈치가 많이 벌어졌어요.. 이러면 공이 뭐 어떻게 되요\n"
        }
        if (angleDifferences!![1] >= 10.0 || angleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}팔로스로 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (angleDifferences!![2] >= 10.0 || angleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}팔로스로 오른쪽 골반 각도가 잘못됐당\n"
        }
        if (angleDifferences!![3] >= 10.0 || angleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}팔로스로 오른쪽 어깨 각도가 잘못됐당\n"
        }
        if (angleDifferences!![4] >= 10.0 || angleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}팔로스로 왼쪽 무릎 각도가 잘못됐당\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
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
