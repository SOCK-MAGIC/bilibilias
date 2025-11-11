package com.imcys.bilibilias.core.domain.model

// UserContentId
sealed interface UgcId {
    data class Aid(val id: String) : UgcId {
        override fun toString(): String = "av$id"
    }

    data class Bvid(val id: String) : UgcId {
        override fun toString(): String = id
    }
}

// ProfessionalContentId
sealed interface PgcId {
    data class Ep(val id: String) : PgcId {
        override fun toString(): String = "ep$id"
    }

    data class Ss(val id: String) : PgcId {
        override fun toString(): String = "ss$id"
    }
}