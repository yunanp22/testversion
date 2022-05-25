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
                    } else if (getSelectedSpinnerItem() == 1) {
                        comment.text = "푸쉬어웨이 점수: $score2"
                    } else if (getSelectedSpinnerItem() == 2) {
                        comment.text = "다운스윙 점수: $score3"
                    } else if (getSelectedSpinnerItem() == 3) {
                        comment.text = "백스윙 점수: $score4"
                    } else if (getSelectedSpinnerItem() == 4) {
                        comment.text = "포워드 점수: $score5"
                    } else {
                        comment.text = "팔로우스루 점수: $score6"
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

