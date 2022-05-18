package org.tensorflow.lite.examples.poseestimation

data class Posture(
    var image : String,
    var posture : String,
    var date : String,
    var score : String
)
