package com.routing.routing.stream

import com.routing.routing.domain.Coordinate
import com.routing.routing.domain.LastCoordinate

data class NotificationDto(
    val coordinate: Coordinate,
    val lastCoordinate: LastCoordinate
)
