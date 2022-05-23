package org.tensorflow.lite.examples.poseestimation.data

import kotlin.math.absoluteValue

class VowlingPose {
    var correctRightElbowAngle: Float = 0.0f
    var correctRightShoulderAngle: Float = 0.0f
    var correctRightHipAngle: Float = 0.0f
    var correctRightKneeAngle: Float = 0.0f
    var correctLeftKneeAngle: Float = 0.0f
    var biggestScore: Float = 0.0f

    constructor(correctRightElbowAngle: Float, correctRightShoulderAngle: Float,
                correctRightHipAngle: Float, correctRightKneeAngle: Float){
        this.correctRightElbowAngle = correctRightElbowAngle
        this.correctRightShoulderAngle = correctRightShoulderAngle
        this.correctRightHipAngle = correctRightHipAngle
        this.correctRightKneeAngle = correctRightKneeAngle
        this.correctLeftKneeAngle = 0.0f
    }

    constructor(correctRightElbowAngle: Float, correctRightShoulderAngle: Float,
                correctRightHipAngle: Float, correctRightKneeAngle: Float, correctLeftKneeAngle: Float){
        this.correctRightElbowAngle = correctRightElbowAngle
        this.correctRightShoulderAngle = correctRightShoulderAngle
        this.correctRightHipAngle = correctRightHipAngle
        this.correctRightKneeAngle = correctRightKneeAngle
        this.correctLeftKneeAngle = correctLeftKneeAngle
    }

    fun getREA(): Float {
        return this.correctRightElbowAngle
    }

    fun getRSA(): Float {
        return this.correctRightShoulderAngle
    }

    fun getRHA(): Float {
        return this.correctRightHipAngle
    }

    fun getRKA(): Float {
        return this.correctRightKneeAngle
    }

    fun getLKA(): Float {
        return this.correctLeftKneeAngle
    }



//    fun getScore(a1: Double, a2: Double, a3: Double, a4: Double): Double {
//        return 100 - (correctRightElbowAngle - a1 + correctRightShoulderAngle - a2 +
//                correctRightHipAngle - a3 + correctRightKneeAngle - a4).absoluteValue
//    }

    fun getScore(a1: Float, a2: Float, a3: Float, a4: Float): Float {
        return 100 - ((correctRightElbowAngle - a1).absoluteValue + (correctRightShoulderAngle - a2).absoluteValue +
                (correctRightHipAngle - a3).absoluteValue + (correctRightKneeAngle - a4))
    }

//    fun getScore(a1: Double, a2: Double, a3: Double, a4: Double, a5: Double): Double {
//        return 100 - (correctRightElbowAngle - a1 + correctRightShoulderAngle - a2 +
//                correctRightHipAngle - a3 + correctRightKneeAngle - a4 + correctLeftKneeAngle - a5).absoluteValue
//    }

    fun getScore(a1: Float, a2: Float, a3: Float, a4: Float, a5: Float): Float {
        return 100 - ((correctRightElbowAngle - a1).absoluteValue + (correctRightShoulderAngle - a2).absoluteValue +
                (correctRightHipAngle - a3).absoluteValue + (correctRightKneeAngle - a4).absoluteValue + (correctLeftKneeAngle - a5).absoluteValue)
    }
}