package com.routing.routing.event

import com.routing.routing.domain.Event
import com.routing.routing.domain.enum.EventType
import com.routing.routing.repository.EventRepository
import com.routing.routing.repository.RouteRepository
import com.routing.routing.repository.StopRepository
import com.routing.routing.stream.NotificationDto
import com.routing.routing.stream.geoFence
import com.routing.routing.utils.haversineDistance
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class EventArrival(
        private val routeRepository: RouteRepository,
        private val stopRepository: StopRepository,
        private val eventRepository: EventRepository
): Observer {
    private val log = LoggerFactory.getLogger(EventArrival::class.java)

    fun processorCoordinate(notificationDto: NotificationDto){
        val lastCoordinate = notificationDto.lastCoordinate
        val coordinate = notificationDto.coordinate
        val isSamePlace = (lastCoordinate.latitude == coordinate.latitude && lastCoordinate.longitude == coordinate.longitude)
        if(isSamePlace){
            val route = routeRepository.getRouteByEquipment_Id(coordinate.equipmentId).get()
            route.stops.filter { stop ->
                stop.arrivalAt == null &&
                        haversineDistance(coordinate.latitude, coordinate.longitude, stop.latitude, stop.longitude) <= geoFence
            }.map { stop ->
                val updatedStop = stop.copy(arrivalAt = coordinate.datePing)
                stopRepository.save(updatedStop)
                log.info("Driver arrive at stop {}", stop)
                val newEvent = Event(null, EventType.ARRIVE, Date(), stop.id)
                eventRepository.save(newEvent)
            }
        }
    }

    override fun update(o: Observable?, notificationDto: Any?) {
        if(o != null && notificationDto != null){
            processorCoordinate(notificationDto as NotificationDto)
        }
    }

}