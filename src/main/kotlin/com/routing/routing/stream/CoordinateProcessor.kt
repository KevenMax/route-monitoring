package com.routing.routing.stream

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.routing.routing.domain.Coordinate
import com.routing.routing.domain.Equipment
import com.routing.routing.domain.LastCoordinate
import com.routing.routing.event.EventArrival
import com.routing.routing.repository.LastCoordinateRepository
import com.routing.routing.repository.RouteRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

const val geoFence = 0.2

@Component
class CoordinateProcessor(
        private val routeRepository: RouteRepository,
        private val lastCoordinateRepository: LastCoordinateRepository,
        private val eventArrival: EventArrival
): Observable() {

    private val log = LoggerFactory.getLogger(CoordinateProcessor::class.java)

    fun receiveCoordinate(coordinate: Coordinate){
        log.info("Coordinate Processor {}", coordinate)
        val routeOptional = routeRepository.getRouteByEquipment_Id(coordinate.equipmentId)
        if(routeOptional.isPresent){
            val route = routeOptional.get()
            val lastCoordinateOptional = lastCoordinateRepository.getLastCoordinateByEquipment_Id(coordinate.equipmentId)
            val lastCoordinate = if(lastCoordinateOptional.isPresent){
                val last = lastCoordinateOptional.get()
                val newLastCoordinate = last.copy(latitude = last.latitude, longitude = last.longitude, `when` = last.`when`)
                lastCoordinateRepository.save(newLastCoordinate)
            }else{
                val equipment = Equipment(id = coordinate.equipmentId)
                val last = LastCoordinate(null, equipment, coordinate.latitude, coordinate.longitude, coordinate.datePing, route)
                lastCoordinateRepository.save(last)
            }
            setChanged()
            notifyObservers(NotificationDto(coordinate, lastCoordinate))
        }
    }

    @Scheduled(fixedDelay = 100000, initialDelay = 10000)
    fun consumeCoordinates(){
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val coordinates = "[{\"equipmentId\":10000,\"latitude\":-3.752414,\"longitude\":-38.511576,\"datePing\":1599904800000},{\"equipmentId\":10000,\"latitude\":-3.752526,\"longitude\":-38.512504,\"datePing\":1599904920000},{\"equipmentId\":10000,\"latitude\":-3.752581,\"longitude\":-38.513015,\"datePing\":1599905040000},{\"equipmentId\":10000,\"latitude\":-3.75262,\"longitude\":-38.513433,\"datePing\":1599905160000},{\"equipmentId\":10000,\"latitude\":-3.752637,\"longitude\":-38.513635,\"datePing\":1599905220000},{\"equipmentId\":10000,\"latitude\":-3.752637,\"longitude\":-38.513635,\"datePing\":1599905280000},{\"equipmentId\":10000,\"latitude\":-3.752637,\"longitude\":-38.513635,\"datePing\":1599905340000},{\"equipmentId\":10000,\"latitude\":-3.752637,\"longitude\":-38.513635,\"datePing\":1599905400000},{\"equipmentId\":10000,\"latitude\":-3.752696,\"longitude\":-38.513927,\"datePing\":1599905520000},{\"equipmentId\":10000,\"latitude\":-3.7526674,\"longitude\":-38.5149107,\"datePing\":1599905580000},{\"equipmentId\":10000,\"latitude\":-3.7522729,\"longitude\":-38.5162625,\"datePing\":1599905640000},{\"equipmentId\":10000,\"latitude\":-3.751894,\"longitude\":-38.515098,\"datePing\":1599905700000},{\"equipmentId\":10000,\"latitude\":-3.750796,\"longitude\":-38.515302,\"datePing\":1599905760000},{\"equipmentId\":10000,\"latitude\":-3.749769,\"longitude\":-38.515491,\"datePing\":1599905820000},{\"equipmentId\":10000,\"latitude\":-3.7500412,\"longitude\":-38.5161274,\"datePing\":1599905880000},{\"equipmentId\":10000,\"latitude\":-3.7500412,\"longitude\":-38.5161274,\"datePing\":1599905940000},{\"equipmentId\":10000,\"latitude\":-3.7500412,\"longitude\":-38.5161274,\"datePing\":1599906000000},{\"equipmentId\":10000,\"latitude\":-3.7500412,\"longitude\":-38.5161274,\"datePing\":1599906060000},{\"equipmentId\":10000,\"latitude\":-3.7500412,\"longitude\":-38.5161274,\"datePing\":1599906120000},{\"equipmentId\":10000,\"latitude\":-3.7500412,\"longitude\":-38.5161274,\"datePing\":1599906180000},{\"equipmentId\":10000,\"latitude\":-3.748932,\"longitude\":-38.515718,\"datePing\":1599906240000},{\"equipmentId\":10000,\"latitude\":-3.748671,\"longitude\":-38.516117,\"datePing\":1599906300000},{\"equipmentId\":10000,\"latitude\":-3.748641,\"longitude\":-38.516856,\"datePing\":1599906360000},{\"equipmentId\":10000,\"latitude\":-3.74974,\"longitude\":-38.516425,\"datePing\":1599906480000},{\"equipmentId\":10000,\"latitude\":-3.74974,\"longitude\":-38.516425,\"datePing\":1599906540000},{\"equipmentId\":10000,\"latitude\":-3.74974,\"longitude\":-38.516425,\"datePing\":1599906600000},{\"equipmentId\":10000,\"latitude\":-3.74974,\"longitude\":-38.516425,\"datePing\":1599906660000},{\"equipmentId\":10000,\"latitude\":-3.74974,\"longitude\":-38.516425,\"datePing\":1599906720000},{\"equipmentId\":10000,\"latitude\":-3.7506319,\"longitude\":-38.5178648,\"datePing\":1599906960000},{\"equipmentId\":10000,\"latitude\":-3.7506319,\"longitude\":-38.5178648,\"datePing\":1599907020000},{\"equipmentId\":10000,\"latitude\":-3.7506319,\"longitude\":-38.5178648,\"datePing\":1599907080000},{\"equipmentId\":10000,\"latitude\":-3.7506319,\"longitude\":-38.5178648,\"datePing\":1599907140000}]"
        val listCoordinates = mapper.readValue(coordinates, Array<Coordinate>::class.java).asList()

        this.addObserver(eventArrival)
        listCoordinates.forEach { coordinate ->
            Thread.sleep(1000)
            receiveCoordinate(coordinate)
        }
    }
}