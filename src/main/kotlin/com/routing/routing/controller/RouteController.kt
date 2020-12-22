package com.routing.routing.controller

import com.routing.routing.domain.Route
import com.routing.routing.repository.RouteRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/routes")
class RouteController(
        private val routeRepository: RouteRepository
) {

    @PostMapping
    fun createRoute(@RequestBody route: Route){
        routeRepository.save(route)
    }

    @DeleteMapping("/{id}")
    fun deleteRoute(@PathVariable id: Int){
        routeRepository.deleteById(id)
    }

    @GetMapping
    fun getRoutes(): ResponseEntity<List<Route>>{
        return ResponseEntity(routeRepository.findAll(), HttpStatus.OK)
    }
}