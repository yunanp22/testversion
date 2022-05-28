package org.tensorflow.lite.examples.poseestimation

import android.graphics.*
import android.icu.text.AlphabeticIndex
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_resultpopup.*
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.MoveNet


class ResultPopupActivity: AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var okButton: Button
    private lateinit var comment: TextView
    private lateinit var imageView: ImageView
    private lateinit var commentImageView: ImageView
    private lateinit var wrongAngleDifference1: TextView
    private lateinit var wrongAngleDifference2: TextView
    private lateinit var wrongAngleDifference3: TextView
    private lateinit var wrongAngleDifference4: TextView
    private lateinit var wrongAngleDifference5: TextView
    private lateinit var feedback: TextView


    private lateinit var poseAngleDifferences: Array<FloatArray?>

    private var isTrackerEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultpopup)

        spinner = findViewById(R.id.spinner)
        okButton = findViewById(R.id.result_ok)
        comment = findViewById<TextView>(R.id.result_comment)
        commentImageView = findViewById(R.id.result_comment_image)
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
        Log.d("TAG", "score1 : ${score1}")

        var addressResultURI = intent.getStringExtra("addressuri")
        var pushawayResultURI = intent.getStringExtra("pushawayuri")
        var downswingResultURI = intent.getStringExtra("downswinguri")
        var backswingResultURI = intent.getStringExtra("backswinguri")
        var forwardswingResultURI = intent.getStringExtra("forwardswinguri")
        var followthroughResultURI = intent.getStringExtra("followthroughuri")
        Log.d("TAG", "addressResultURI : ${addressResultURI}")

        var addressBitmap = BitmapFactory.decodeFile(addressResultURI)
        var pushawayBitmap = BitmapFactory.decodeFile(pushawayResultURI)
        var downswingBitmap = BitmapFactory.decodeFile(downswingResultURI)
        var backswingBitmap = BitmapFactory.decodeFile(backswingResultURI)
        var forwardswingBitmap = BitmapFactory.decodeFile(forwardswingResultURI)
        var followthroughBitmap = BitmapFactory.decodeFile(followthroughResultURI)
        Log.d("TAG", "addressBitmap : ${addressBitmap}")


        var addressAngleDifferences = intent.getFloatArrayExtra("addressAngleDifferences")
        var pushawayAngleDifferences = intent.getFloatArrayExtra("pushawayAngleDifferences")
        var downswingAngleDifferences = intent.getFloatArrayExtra("downswingAngleDifferences")
        var backswingAngleDifferences = intent.getFloatArrayExtra("backswingAngleDifferences")
        var forwardswingAngleDifferences = intent.getFloatArrayExtra("forwardswingAngleDifferences")
        var followthroughAngleDifferences = intent.getFloatArrayExtra("followthroughAngleDifferences")
        Log.d("TAG", "addressAngleDifferences : ${addressAngleDifferences}")

        var addressPerson = intent.getParcelableExtra<Person>("addressperson")
        var pushawayPerson = intent.getParcelableExtra<Person>("pushawayperson")
        var downswingPerson = intent.getParcelableExtra<Person>("downswingperson")
        var backswingPerson = intent.getParcelableExtra<Person>("backswingperson")
        var forwardswingPerson = intent.getParcelableExtra<Person>("forwardswingperson")
        var followthroughPerson = intent.getParcelableExtra<Person>("followthroughperson")

        poseAngleDifferences = arrayOf(addressAngleDifferences, pushawayAngleDifferences,
            downswingAngleDifferences, backswingAngleDifferences, forwardswingAngleDifferences, followthroughAngleDifferences)

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

                        if(addressBitmap != null) {
                            imageView.setImageBitmap(visualize(PoseType.ADDRESS, addressPerson!!, addressBitmap, poseAngleDifferences))
                            resultImage(score1)
                            feedbackAddressAngleDiffernce(addressAngleDifferences!!)
                        }  else {
                            imageView.setImageResource(R.drawable.bowling)
                        }

                    } else if (getSelectedSpinnerItem() == 1) {
                        comment.text = "푸쉬어웨이 점수: $score2"

                        if(pushawayBitmap != null) {
                            resultImage(score2)
                            imageView.setImageBitmap(visualize(PoseType.PUSHAWAY, pushawayPerson!!, pushawayBitmap, poseAngleDifferences))
                            feedbackPushAngleDiffernce(pushawayAngleDifferences!!)
                        } else {
                            imageView.setImageResource(R.drawable.bowling)
                        }
                    } else if (getSelectedSpinnerItem() == 2) {
                        comment.text = "다운스윙 점수: $score3"

                        if(downswingBitmap != null) {
                            imageView.setImageBitmap(visualize(PoseType.DOWNSWING, downswingPerson!!, downswingBitmap, poseAngleDifferences))
                            resultImage(score3)
                            feedbackDownAngleDiffernce(downswingAngleDifferences!!)
                        } else {
                            imageView.setImageResource(R.drawable.bowling)
                        }
                    } else if (getSelectedSpinnerItem() == 3) {
                        comment.text = "백스윙 점수: $score4"

                        if(backswingBitmap != null) {
                            imageView.setImageBitmap(visualize(PoseType.BACKSWING, backswingPerson!!, backswingBitmap, poseAngleDifferences))
                            resultImage(score4)
                            feedbackBackAngleDiffernce(backswingAngleDifferences!!)
                        } else {
                            imageView.setImageResource(R.drawable.bowling)
                        }
                    } else if (getSelectedSpinnerItem() == 4) {
                        comment.text = "포워드 점수: $score5"
                        if(forwardswingBitmap != null) {
                            imageView.setImageBitmap(visualize(PoseType.FORWARDSWING, forwardswingPerson!!, forwardswingBitmap, poseAngleDifferences))
                            resultImage(score5)
                            feedbackForwardAngleDiffernce(forwardswingAngleDifferences!!)
                        } else {
                            imageView.setImageResource(R.drawable.bowling)
                        }
                    } else {
                        comment.text = "팔로우스루 점수: $score6"
                        if(followthroughBitmap != null) {
                            imageView.setImageBitmap(visualize(PoseType.FOLLOWTHROUGH, followthroughPerson!!, followthroughBitmap, poseAngleDifferences))
                            resultImage(score6)
                            feedbackFollowAngleDiffernce(followthroughAngleDifferences!!)
                        } else {
                            imageView.setImageResource(R.drawable.bowling)
                        }
                    }
                }
            }

            okButton.setOnClickListener {
                RecordFragment.resetRecordedInfo()
                MoveNet.resetInfo()
                finish()
            }
        }

    override fun onDestroy() {
        RecordFragment.resetRecordedInfo()
        MoveNet.resetInfo()
        super.onDestroy()
    }



    private fun visualize(pose: PoseType, persons: Person, bitmap: Bitmap, array: Array<FloatArray?>): Bitmap {

        val outputBitmap = VisualizationUtils.drawBodyKeypointsByScore(
            pose,
            array,
            bitmap,
            persons, false
        )

        return outputBitmap
    }


    fun getSelectedSpinnerItem(): Int {
        return spinner.selectedItemPosition
    }

    fun resultImage(score: Float){
        if(score >= 40) {
            commentImageView.setImageResource(R.drawable.result_good)
        } else if(score >= 35) {
            commentImageView.setImageResource(R.drawable.result_warning)
        } else {
            commentImageView.setImageResource(R.drawable.result_bad)
        }
    }

    fun feedbackAddressAngleDiffernce(addressAngleDifferences: FloatArray) {
        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${addressAngleDifferences[0]}"
        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${addressAngleDifferences[1]}"
        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${addressAngleDifferences[2]}"
        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${addressAngleDifferences[3]}"
        if (addressAngleDifferences!![0] >= 10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 벌어졌네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }else if (addressAngleDifferences!![0] <= -10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 좁네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }

        if (addressAngleDifferences!![1] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(addressAngleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (addressAngleDifferences!![2] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(addressAngleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }
        if (addressAngleDifferences!![3] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(addressAngleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }
        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackPushAngleDiffernce(pushawayAngleDifferences: FloatArray) {
        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${pushawayAngleDifferences!![0]}"
        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${pushawayAngleDifferences!![1]}"
        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${pushawayAngleDifferences!![2]}"
        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${pushawayAngleDifferences!![3]}"
        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${pushawayAngleDifferences!![4]}"

        if (pushawayAngleDifferences!![0] >= 10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 벌어졌네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }else if (pushawayAngleDifferences!![0] <= -10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 좁네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }

        if (pushawayAngleDifferences!![1] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(pushawayAngleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (pushawayAngleDifferences!![2] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(pushawayAngleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (pushawayAngleDifferences!![3] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(pushawayAngleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (pushawayAngleDifferences!![4] >= 10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(pushawayAngleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackDownAngleDiffernce(angleDifferences: FloatArray) {
        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${angleDifferences!![0]}"
        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${angleDifferences!![1]}"
        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${angleDifferences!![2]}"
        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${angleDifferences!![3]}"
        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${angleDifferences!![4]}"

        if (angleDifferences!![0] >= 10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 벌어졌네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }else if (angleDifferences!![0] <= -10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 좁네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }

        if (angleDifferences!![1] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![2] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![3] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![4] >= 10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if(feedback.text == null){
            feedback.text = "짝짝짝! 완벽한 자세에요!"
        }
    }

    fun feedbackBackAngleDiffernce(angleDifferences: FloatArray) {
        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${angleDifferences!![0]}"
        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${angleDifferences!![1]}"
        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${angleDifferences!![2]}"
        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${angleDifferences!![3]}"
        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${angleDifferences!![4]}"

        if (angleDifferences!![0] >= 10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 벌어졌네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }else if (angleDifferences!![0] <= -10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 좁네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }

        if (angleDifferences!![1] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![2] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![3] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![4] >= 10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }
    }

    fun feedbackForwardAngleDiffernce(angleDifferences: FloatArray) {
        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${angleDifferences!![0]}"
        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${angleDifferences!![1]}"
        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${angleDifferences!![2]}"
        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${angleDifferences!![3]}"
        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${angleDifferences!![4]}"

        if (angleDifferences!![0] >= 10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 벌어졌네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }else if (angleDifferences!![0] <= -10.0) {
            feedback.text = "오른쪽 팔꿈치가 많이 좁네요! 팔을 90도로 만들어서 팔꿈치를 옆구리에 딱 붙여주세요!\n"
        }

        if (angleDifferences!![1] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[1] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 어깨 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![2] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[2] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 골반 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![3] >= 10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[3] <= -10.0) {
            feedback.text = "${feedback.text}오른쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }

        if (angleDifferences!![4] >= 10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 벌어졌네요! 각도를 조금 줄여주세요!\n"
        }else if(angleDifferences[4] <= -10.0) {
            feedback.text = "${feedback.text}왼쪽 무릎 각도가 많이 좁네요! 각도를 조금 벌려주세요!\n"
        }
    }

    fun feedbackFollowAngleDiffernce(angleDifferences: FloatArray) {
        wrongAngleDifference1.text = "오른쪽 팔꿈치 각도 차이: ${angleDifferences!![0]}"
        wrongAngleDifference2.text = "오른쪽 어깨 각도 차이: ${angleDifferences!![1]}"
        wrongAngleDifference3.text = "오른쪽 골반 각도 차이: ${angleDifferences!![2]}"
        wrongAngleDifference4.text = "오른쪽 무릎 각도 차이: ${angleDifferences!![3]}"
        wrongAngleDifference5.text = "왼쪽 무릎 각도 차이: ${angleDifferences!![4]}"
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