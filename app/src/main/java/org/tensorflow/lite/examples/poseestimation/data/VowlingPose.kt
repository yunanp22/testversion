package org.tensorflow.lite.examples.poseestimation.data

import kotlin.math.absoluteValue

class VowlingPose {
    var correctRightElbowAngle: Double = 0.0
    var correctRightShoulderAngle: Double = 0.0
    var correctRightHipAngle: Double = 0.0
    var correctRightKneeAngle: Double = 0.0
    var correctLeftKneeAngle: Double = 0.0
    var biggestScore: Double = 0.0

    constructor(correctRightElbowAngle: Double, correctRightShoulderAngle: Double,
                correctRightHipAngle: Double, correctRightKneeAngle: Double){
        this.correctRightElbowAngle = correctRightElbowAngle
        this.correctRightShoulderAngle = correctRightShoulderAngle
        this.correctRightHipAngle = correctRightHipAngle
        this.correctRightKneeAngle = correctRightKneeAngle
        this.correctLeftKneeAngle = 0.0
    }

    constructor(correctRightElbowAngle: Double, correctRightShoulderAngle: Double,
                correctRightHipAngle: Double, correctRightKneeAngle: Double, correctLeftKneeAngle: Double){
        this.correctRightElbowAngle = correctRightElbowAngle
        this.correctRightShoulderAngle = correctRightShoulderAngle
        this.correctRightHipAngle = correctRightHipAngle
        this.correctRightKneeAngle = correctRightKneeAngle
        this.correctLeftKneeAngle = correctLeftKneeAngle
    }


    fun getScore(a1: Double, a2: Double, a3: Double, a4: Double): Double {
        return 100 - ((correctRightElbowAngle - a1).absoluteValue + (correctRightShoulderAngle - a2).absoluteValue +
                (correctRightHipAngle - a3).absoluteValue + (correctRightKneeAngle - a4))
    }

    fun getScore(a1: Double, a2: Double, a3: Double, a4: Double, a5: Double): Double {
        return 100 - ((correctRightElbowAngle - a1).absoluteValue + (correctRightShoulderAngle - a2).absoluteValue +
                (correctRightHipAngle - a3).absoluteValue + (correctRightKneeAngle - a4).absoluteValue + (correctLeftKneeAngle - a5).absoluteValue)
    }
}