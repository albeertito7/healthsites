package albertperez.healthsites

import java.io.Serializable

class SupportMessage : Serializable {
    var userId: String? = null
    var text: String? = null
    var timestamp: String? = null

    constructor() {}
    constructor(
        userId: String? = null,
        text: String? = null,
        timestamp: String? = null
    ) {
        this.userId = userId
        this.text = text
        this.timestamp = timestamp
    }
}
