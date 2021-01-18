package io.glnt.gpms.handler.product.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.glnt.gpms.model.enums.TicketType
import java.time.LocalDate
import java.time.LocalDateTime

data class reqSearchProduct(
    var vehicleNo: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd") var from: LocalDate? = null,
    @JsonFormat(pattern = "yyyy-MM-dd") var to: LocalDate? = null
)

data class reqCreateProduct(
    var vehicleNo: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") var startDate: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") var endDate: LocalDateTime,
    var userId: String? = null,
    var gateId: MutableSet<String>? = mutableSetOf("ALL"),
    var ticktType: TicketType? = TicketType.SEASONTICKET
)
