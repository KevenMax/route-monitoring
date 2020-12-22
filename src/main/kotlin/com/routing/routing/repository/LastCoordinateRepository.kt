package com.routing.routing.repository

import com.routing.routing.domain.LastCoordinate
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LastCoordinateRepository: JpaRepository<LastCoordinate, Int>{
    fun getLastCoordinateByEquipment_Id(id: Int): Optional<LastCoordinate>
}