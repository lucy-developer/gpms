package io.glnt.gpms.model.repository

import io.glnt.gpms.model.entity.Barcode
import io.glnt.gpms.model.entity.BarcodeClass
import io.glnt.gpms.model.enums.DelYn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BarcodeClassRepository: JpaRepository<BarcodeClass, Long> {
    fun findBySn(sn: Long): Optional<BarcodeClass>
    fun findByStartLessThanEqualAndEndGreaterThanAndDelYn(start: Int, end: Int, delYn: DelYn): BarcodeClass?
}