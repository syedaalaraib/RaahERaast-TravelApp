package com.laraib.smd_project

data class ChatRV(
    var sender: String? = null,
    var receiver: String? = null,
    var message: String? = null,
    var image: String? = null,
    var voiceNoteUrl: String? = null
)
