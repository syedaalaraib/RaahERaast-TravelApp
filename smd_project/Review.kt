package com.laraib.smd_project


data class Review(
    var reviewText: String = "",
    var rating: Float = 0f,
    var placeName: String = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", 0f, "")
}


