package com.routing.routing.repository

import com.routing.routing.domain.Stop
import org.springframework.data.jpa.repository.JpaRepository

interface StopRepository: JpaRepository<Stop, Int>