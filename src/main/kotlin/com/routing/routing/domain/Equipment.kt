package com.routing.routing.domain

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Equipment(
        @Id
        val id: Int,
        val name: String? = null,

)