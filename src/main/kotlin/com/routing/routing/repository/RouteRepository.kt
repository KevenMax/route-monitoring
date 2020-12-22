package com.routing.routing.repository

import com.routing.routing.domain.Route
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RouteRepository: JpaRepository<Route, Int> {
    fun getRouteByEquipment_Id(id: Int): Optional<Route>
}