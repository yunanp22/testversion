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

import android.graphics.*
import android.util.Log
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.math.max

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 8f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 12f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )


    // Draw line and point indicate body pose
    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false
    ): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.argb(100,255,255,255)
            style = Paint.Style.STROKE
        }

        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.BLUE
            textAlign = Paint.Align.LEFT
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)
        persons.forEach { person ->
            // draw person id if tracker is enable
            if (isTrackerEnabled) {
                person.boundingBox?.let {
                    val personIdX = max(0f, it.left)
                    val personIdY = max(0f, it.top)

                    originalSizeCanvas.drawText(
                        person.id.toString(),
                        personIdX,
                        personIdY - PERSON_ID_MARGIN,
                        paintText
                    )
                    originalSizeCanvas.drawRect(it, paintLine)
                }
            }
            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            person.keyPoints.forEach { point ->
                originalSizeCanvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }
        }
        return output
    }

//    // Draw line and point indicate body pose
//    fun drawBodyKeypointsByScore(
//        pose: PoseType,
//        angleDifferences: Array<FloatArray?>,
//        input: Bitmap,
//        persons: List<Person>,
//        isTrackerEnabled: Boolean = false,
//    ): Bitmap {
//        val paintCircle = Paint().apply {
//            strokeWidth = CIRCLE_RADIUS
//            color = Color.WHITE
//            style = Paint.Style.FILL
//        }
//
//        val paintLine = Paint().apply {
//            strokeWidth = LINE_WIDTH
//            color = Color.argb(100,255,255,255)
//            style = Paint.Style.STROKE
//        }
//
//        val paintText = Paint().apply {
//            textSize = PERSON_ID_TEXT_SIZE
//            color = Color.BLUE
//            textAlign = Paint.Align.LEFT
//        }
//
//        val paintBadCircle = Paint().apply {
//            strokeWidth = CIRCLE_RADIUS
//            color = Color.RED
//            style = Paint.Style.FILL
//
//        }
//
//        val paintWarningCircle = Paint().apply {
//            strokeWidth = CIRCLE_RADIUS
//            color = Color.argb(255, 239, 163, 63)
//            style = Paint.Style.FILL
//
//        }
//
//        val paintGoodCircle = Paint().apply {
//            strokeWidth = CIRCLE_RADIUS
//            color = Color.GREEN
//            style = Paint.Style.FILL
//        }
//
//        val paintBadLine = Paint().apply {
//            strokeWidth = LINE_WIDTH
//            color = Color.argb(128,255,0,0)
//            style = Paint.Style.STROKE
//        }
//
//        val paintWarningLine = Paint().apply {
//            strokeWidth = LINE_WIDTH
//            color = Color.argb(128,239,163,63)
//            style = Paint.Style.STROKE
//        }
//
//        val paintGoodLine = Paint().apply {
//            strokeWidth = LINE_WIDTH
//            color = Color.argb(128,0,255,0)
//            style = Paint.Style.STROKE
//        }
//
//        val output = input.copy(Bitmap.Config.ARGB_8888, true)
//        val originalSizeCanvas = Canvas(output)
//
//        /** 각도에 따라 선 색 바꾸기*/
//
//        fun setPointColor(pointA: PointF, pointB: PointF, paint: Paint){
//
//            originalSizeCanvas.drawCircle(
//                pointA.x,
//                pointA.y,
//                CIRCLE_RADIUS,
//                paint
//            )
//            originalSizeCanvas.drawCircle(
//                pointB.x,
//                pointB.y,
//                CIRCLE_RADIUS,
//                paint
//            )
//
//        }
//
//        fun setLineColor(angleDifference: Float, pointA: PointF, pointB: PointF): Int{
//            if(angleDifference > 10 || angleDifference < -10) {
//                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintBadLine)
//                setPointColor(pointA, pointB, paintBadCircle)
//                return 1
//            }
////            else if((angleDifference<=10&&angleDifference>5) || (angleDifference >= -10 && angleDifference < -5)) {
////                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintWarningLine)
////                setPointColor(pointA, pointB, paintWarningCircle)
////            }
//            else {
//                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintGoodLine)
//                setPointColor(pointA, pointB, paintGoodCircle)
//                return 0
//            }
//        }
//
//        persons.forEach { person ->
//            // draw person id if tracker is enable
//            if (isTrackerEnabled) {
//                person.boundingBox?.let {
//                    val personIdX = max(0f, it.left)
//                    val personIdY = max(0f, it.top)
//
//                    originalSizeCanvas.drawText(
//                        person.id.toString(),
//                        personIdX,
//                        personIdY - PERSON_ID_MARGIN,
//                        paintText
//                    )
//                    originalSizeCanvas.drawRect(it, paintLine)
//                }
//            }
//
//            /**
//             * rightElbowAngle = Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW), Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST)
//             * rightShoulderAngle = Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW), Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)
//             * rightHipAngle = Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP), Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE)
//             * right KneeAngle = Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE), Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
//             * leftKneeAngle = Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE), Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE)
//             */
//
//
//
//            if(pose == PoseType.ADDRESS) {
//                if(setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                }
//
//                if(setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                }
//
//                if(setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                }
//
//                if(setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                }
//
//
////                        setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[8].coordinate, person.keyPoints[10].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[14].coordinate, person.keyPoints[16].coordinate)
//            } else if(
//                pose == PoseType.PUSHAWAY
//                || pose == PoseType.DOWNSWING
//                || pose == PoseType.BACKSWING
//                || pose == PoseType.FORWARDSWING
//                || pose == PoseType.FOLLOWTHROUGH
//            ){
//                if(setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                }
//
//                if(setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                }
//
//                if(setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)) {
//                    setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                } else {
//                    setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                }
//
////                        setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[8].coordinate, person.keyPoints[10].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
////                        setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[14].coordinate, person.keyPoints[16].coordinate)
//                setLineColor(angleDifferences[pose.ordinal]!![4], person.keyPoints[11].coordinate, person.keyPoints[13].coordinate)
//                setLineColor(angleDifferences[pose.ordinal]!![4], person.keyPoints[13].coordinate, person.keyPoints[15].coordinate)
//            }
//
//            bodyJoints.forEach { pair ->
//
//                if(
//                    pair == Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW)
//                    || pair == Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST)
//                    || pair == Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)
//                    || pair == Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE)
//                    || pair == Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
//                    || pair == Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE)
//                    || pair == Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE)
//                ) {
//
//                }
//                else {
//                    val pointA = person.keyPoints[pair.first.position].coordinate
//                    val pointB = person.keyPoints[pair.second.position].coordinate
//                    originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
//                }
//
//            }
//
//            person.keyPoints.forEach { point ->
//
//                if(point.bodyPart == BodyPart.RIGHT_SHOULDER
//                    || point.bodyPart == BodyPart.RIGHT_ELBOW
//                    || point.bodyPart == BodyPart.RIGHT_ANKLE
//                    || point.bodyPart == BodyPart.RIGHT_HIP
//                    || point.bodyPart == BodyPart.RIGHT_WRIST
//                    || point.bodyPart == BodyPart.RIGHT_KNEE
//                    || point.bodyPart == BodyPart.LEFT_ANKLE
//                    || point.bodyPart == BodyPart.LEFT_HIP
//                    || point.bodyPart == BodyPart.LEFT_KNEE
//                )
//                {
//
//                } else {
//                    originalSizeCanvas.drawCircle(
//                        point.coordinate.x,
//                        point.coordinate.y,
//                        CIRCLE_RADIUS,
//                        paintCircle
//                    )
//                }
//
//            }
//        }
//        return output
//    }

    // Draw line and point indicate body pose
    fun drawBodyKeypointsByScore(
        pose: PoseType,
        angleDifferences: Array<FloatArray?>,
        input: Bitmap,
        persons: Person,
        isTrackerEnabled: Boolean = false,
    ): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.argb(100,255,255,255)
            style = Paint.Style.STROKE
        }

        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.BLUE
            textAlign = Paint.Align.LEFT
        }

        val paintBadCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL

        }

        val paintWarningCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.argb(255, 239, 163, 63)
            style = Paint.Style.FILL

        }

        val paintGoodCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        val paintBadLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.argb(128,255,0,0)
            style = Paint.Style.STROKE
        }

        val paintWarningLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.argb(128,239,163,63)
            style = Paint.Style.STROKE
        }

        val paintGoodLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.argb(128,0,255,0)
            style = Paint.Style.STROKE
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)

        /** 각도에 따라 선 색 바꾸기*/

        fun setPointColor(pointA: PointF, pointB: PointF, paint: Paint){

            originalSizeCanvas.drawCircle(
                pointA.x,
                pointA.y,
                CIRCLE_RADIUS,
                paint
            )
            originalSizeCanvas.drawCircle(
                pointB.x,
                pointB.y,
                CIRCLE_RADIUS,
                paint
            )

        }

        fun setLineColor(angleDifference: Float, pointA: PointF, pointB: PointF): Int{
            if(angleDifference > 10 || angleDifference < -10) {
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintBadLine)
                setPointColor(pointA, pointB, paintBadCircle)
                return 1
            }
//            else if((angleDifference<=10&&angleDifference>5) || (angleDifference >= -10 && angleDifference < -5)) {
//                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintWarningLine)
//                setPointColor(pointA, pointB, paintWarningCircle)
//            }
            else {
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintGoodLine)
                setPointColor(pointA, pointB, paintGoodCircle)
                return 0
            }
        }

//        persons.forEach { person ->
//            // draw person id if tracker is enable
//            if (isTrackerEnabled) {
//                person.boundingBox?.let {
//                    val personIdX = max(0f, it.left)
//                    val personIdY = max(0f, it.top)
//
//                    originalSizeCanvas.drawText(
//                        person.id.toString(),
//                        personIdX,
//                        personIdY - PERSON_ID_MARGIN,
//                        paintText
//                    )
//                    originalSizeCanvas.drawRect(it, paintLine)
//                }
//            }

            /**
             * rightElbowAngle = Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW), Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST)
             * rightShoulderAngle = Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW), Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)
             * rightHipAngle = Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP), Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE)
             * right KneeAngle = Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE), Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
             * leftKneeAngle = Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE), Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE)
             */
        bodyJoints.forEach { pair ->
            Log.d("TAG", "drawBodyKeypointsByScore: ${pair}")
//                if(
//                    pair == Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW)
//                    || pair == Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST)
//                    || pair == Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)
//                    || pair == Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE)
//                    || pair == Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
//                    || pair == Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE)
//                    || pair == Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE)
//                ) {
//
//                }
//                else {
            val pointA = persons.keyPoints[pair.first.position].coordinate
            val pointB = persons.keyPoints[pair.second.position].coordinate
            originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
//                }

        }

        persons.keyPoints.forEach { point ->

//                if(point.bodyPart == BodyPart.RIGHT_SHOULDER
//                    || point.bodyPart == BodyPart.RIGHT_ELBOW
//                    || point.bodyPart == BodyPart.RIGHT_ANKLE
//                    || point.bodyPart == BodyPart.RIGHT_HIP
//                    || point.bodyPart == BodyPart.RIGHT_WRIST
//                    || point.bodyPart == BodyPart.RIGHT_KNEE
//                    || point.bodyPart == BodyPart.LEFT_ANKLE
//                    || point.bodyPart == BodyPart.LEFT_HIP
//                    || point.bodyPart == BodyPart.LEFT_KNEE
//                )
//                {

//                } else {
            originalSizeCanvas.drawCircle(
                point.coordinate.x,
                point.coordinate.y,
                CIRCLE_RADIUS,
                paintCircle
            )
//                }

        }


            if(pose == PoseType.ADDRESS) {
                if(setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)
                }

                if(setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)
                }

                if(setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate)
                }

                if(setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![3], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![3], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate)
                }


//                        setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
                setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[8].coordinate, persons.keyPoints[10].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
                setLineColor(angleDifferences[pose.ordinal]!![3], persons.keyPoints[14].coordinate, persons.keyPoints[16].coordinate)
            } else if(
                pose == PoseType.PUSHAWAY
                || pose == PoseType.DOWNSWING
                || pose == PoseType.BACKSWING
                || pose == PoseType.FORWARDSWING
                || pose == PoseType.FOLLOWTHROUGH
            ){
                if(setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[6].coordinate, persons.keyPoints[8].coordinate)
                }

                if(setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![1], persons.keyPoints[6].coordinate, persons.keyPoints[12].coordinate)
                }

                if(setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate) < setLineColor(angleDifferences[pose.ordinal]!![3], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate)) {
                    setLineColor(angleDifferences[pose.ordinal]!![3], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate)
                } else {
                    setLineColor(angleDifferences[pose.ordinal]!![2], persons.keyPoints[12].coordinate, persons.keyPoints[14].coordinate)
                }

//                        setLineColor(angleDifferences[pose.ordinal]!![0], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
                setLineColor(angleDifferences[pose.ordinal]!![0], persons.keyPoints[8].coordinate, persons.keyPoints[10].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[8].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![1], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[6].coordinate, person.keyPoints[12].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![2], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
//                        setLineColor(angleDifferences[pose.ordinal]!![3], person.keyPoints[12].coordinate, person.keyPoints[14].coordinate)
                setLineColor(angleDifferences[pose.ordinal]!![3], persons.keyPoints[14].coordinate, persons.keyPoints[16].coordinate)
                setLineColor(angleDifferences[pose.ordinal]!![4], persons.keyPoints[11].coordinate, persons.keyPoints[13].coordinate)
                setLineColor(angleDifferences[pose.ordinal]!![4], persons.keyPoints[13].coordinate, persons.keyPoints[15].coordinate)
            }



        return output
    }
}
