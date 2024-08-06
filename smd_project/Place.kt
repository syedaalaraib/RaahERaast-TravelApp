package com.laraib.smd_project

data class Place(
    var id: String = "",
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val rating: String = "",
    val eventDes: String = "",
    val imageURL: String = ""
)

 {
    constructor() : this("", "", "", "", "", "", "")
}



