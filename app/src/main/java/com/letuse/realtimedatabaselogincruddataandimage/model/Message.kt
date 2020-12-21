package com.letuse.firebasegit.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Message {
    var author: String? = null
    var body: String? = null
    var time: String? = null
    var key: String? = null
    var itemImageUrl : String? = null

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    constructor(author: String, body: String, time: String , key: String, itemImageUrl:String) {
        this.author = author
        this.body = body
        this.time = time
        this.key = key
        this.itemImageUrl = itemImageUrl
    }
}
