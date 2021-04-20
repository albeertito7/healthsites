package albertperez.healthsites

import java.io.Serializable

class Post : Serializable {
    var id: String? = null
    var healthSiteId: String? = null
    var userId: String? = null
    var text: String? = null
    var timestamp: String? = null
    //var likes: Int? = 0

    constructor() {}
    constructor(
        healthSiteId: String? = null,
        userId: String? = null,
        text: String?,
        timestamp: String?
    ) {
        this.userId = userId
        this.healthSiteId = healthSiteId
        this.text = text
        this.timestamp = timestamp
    }
}