package com.routing.routing.domain

import com.routing.routing.domain.enum.EventType
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Event(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null,
        val eventType: EventType,
        val `when`: Date = Date(),
        val stopId: Int

)