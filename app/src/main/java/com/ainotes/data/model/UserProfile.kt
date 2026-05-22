package com.ainotes.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val educationLevel: String = "", // "School" or "College"
    val classLevel: String = "", // e.g. "9th", "10th", "11th", "12th", "FY", "SY", "TY", "Final Year"
    val degree: String = "", // e.g. "B.Tech" if in college
    val profileCompleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "educationLevel" to educationLevel,
            "classLevel" to classLevel,
            "degree" to degree,
            "profileCompleted" to profileCompleted
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>?, uid: String): UserProfile {
            if (map == null) return UserProfile(uid = uid)
            return UserProfile(
                uid = uid,
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                educationLevel = map["educationLevel"] as? String ?: "",
                classLevel = map["classLevel"] as? String ?: "",
                degree = map["degree"] as? String ?: "",
                profileCompleted = map["profileCompleted"] as? Boolean ?: false
            )
        }
    }
}
