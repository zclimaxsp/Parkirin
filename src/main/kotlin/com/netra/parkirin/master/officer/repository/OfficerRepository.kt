package com.netra.parkirin.master.officer.repository

import com.netra.parkirin.master.officer.model.Officer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface OfficerRepository : ReactiveCrudRepository<Officer, UUID>, OfficerQueryRepository {
    fun findAllByDeletedAtIsNull(): Flux<Officer>
    fun findByUserIdAndDeletedAtIsNull(userId: UUID): Mono<Officer>
}
