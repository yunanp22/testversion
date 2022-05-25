package org.tensorflow.lite.examples.poseestimation

import android.graphics.*
import android.os.Bundle
import android.util.Log
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

    private lateinit var poseAngleDifferences: Array<FloatArray?>

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

        var addressResultURI = intent.getStringExtra("addressuri")
        var pushawayResultURI = intent.getStringExtra("pushawayuri")
        var downswingResultURI = intent.getStringExtra("downswinguri")
        var backswingResultURI = intent.getStringExtra("backswinguri")
        var forwardswingResultURI = intent.getStringExtra("forwardswinguri")
        var followthroughResultURI = intent.getStringExtra("followthroughuri")

        val addressBitmap = BitmapFactory.decodeFile(addressResultURI)
        val pushawayBitmap = BitmapFactory.decodeFile(pushawayResultURI)
        val downswingBitmap = BitmapFactory.decodeFile(downswingResultURI)
        val backswingBitmap = BitmapFactory.decodeFile(backswingResultURI)
        val forwardswingBitmap = BitmapFactory.decodeFile(forwardswingResultURI)
        val followthroughBitmap = BitmapFactory.decodeFile(followthroughResultURI)

        var addressAngleDifferences = intent.getFloatArrayExtra("addressAngleDifferences")
        var pushawayAngleDifferences = intent.getFloatArrayExtra("pushawayAngleDifferences")
        var downswingAngleDifferences = intent.getFloatArrayExtra("downswingAngleDifferences")
        var backswingAngleDifferences = intent.getFloatArrayExtra("backswingAngleDifferences")
        var forwardswingAngleDifferences = intent.getFloatArrayExtra("forwardswingAngleDifferences")
        var followthroughAngleDifferences = intent.getFloatArrayExtra("followthroughAngleDifferences")

        poseAngleDifferences = arrayOf(addressAngleDifferences, pushawayAngleDifferences,
            downswingAngleDifferences, backswingAngleDifferences, forwardswingAngleDifferences, followthroughAngleDifferences)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (getSelectedSpinnerItem() == 0) {
                        comment.text = "어드레스 점수: $score1"
                        if(addressBitmap != null) {
                            imageView.setImageBitmap(setBitmap(PoseType.ADDRESS, addressBitmap, poseAngleDifferences))
                        }

                    } else if (getSelectedSpinnerItem() == 1) {
                        comment.text = "푸쉬어웨이 점수: $score2"
                        if(pushawayBitmap != null) {
                            imageView.setImageBitmap(setBitmap(PoseType.PUSHAWAY, pushawayBitmap, poseAngleDifferences))
                        }
                    } else if (getSelectedSpinnerItem() == 2) {
                        comment.text = "다운스윙 점수: $score3"
                        if(downswingBitmap != null) {
                            imageView.setImageBitmap(setBitmap(PoseType.DOWNSWING, downswingBitmap, poseAngleDifferences))
                        }
                    } else if (getSelectedSpinnerItem() == 3) {
                        comment.text = "백스윙 점수: $score4"
                        if(backswingBitmap != null) {
                            imageView.setImageBitmap(setBitmap(PoseType.BACKSWING, backswingBitmap, poseAngleDifferences))
                        }
                    } else if (getSelectedSpinnerItem() == 4) {
                        comment.text = "포워드 점수: $score5"
                        if(forwardswingBitmap != null) {
                            imageView.setImageBitmap(setBitmap(PoseType.FORWARDSWING, forwardswingBitmap, poseAngleDifferences))
                        }
                    } else {
                        comment.text = "팔로우스루 점수: $score6"
                        if(followthroughBitmap != null) {
                            imageView.setImageBitmap(setBitmap(PoseType.FOLLOWTHROUGH, followthroughBitmap, poseAngleDifferences))
                        }
                    }
                }

            }


            okButton.setOnClickListener {
                finish()
            }
        }


    private fun setBitmap(pose: PoseType, bitmap: Bitmap, array: Array<FloatArray?>): Bitmap {

//            val decodedBitmap = BitmapFactory.decodeByteArray(bitmap, 0, bitmap!!.size)

            val persons = mutableListOf<Person>()

            MoveNet.create(this@ResultPopupActivity, Device.CPU, ModelType.Lightning).estimatePoses(bitmap)?.let {
                persons.addAll(it)
            }
            Log.d("TAG", "onCreate: ${persons.size}")
            return visualize(pose, persons, bitmap, array)

    }

    private fun visualize(pose: PoseType, persons: List<Person>, bitmap: Bitmap, array: Array<FloatArray?>): Bitmap {

        val outputBitmap = VisualizationUtils.drawBodyKeypointsByScore(
            pose,
            array,
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

