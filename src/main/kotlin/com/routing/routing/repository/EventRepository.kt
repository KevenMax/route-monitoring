package com.routing.routing.repository

import com.routing.routing.domain.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository: JpaRepository<Event, Int>