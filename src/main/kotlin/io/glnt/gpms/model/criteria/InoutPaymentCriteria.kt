package io.glnt.gpms.model.criteria

import com.fasterxml.jackson.annotation.JsonFormat
import io.glnt.gpms.model.enums.ResultType
import java.io.Serializable
import java.time.LocalDate

data class InoutPaymentCriteria(
    @JsonFormat( shape = JsonFormat.Shape.ANY, pattern = "yyyy-MM-dd")
    var fromDate: LocalDate? = null,

    @JsonFormat( shape = JsonFormat.Shape.ANY, pattern = "yyyy-MM-dd")
    var toDate: LocalDate? = null,

    var vehicleNo: String? = null,

    var resultType: ResultType? = null,

    var limit: Int? = null
): Serializable {
    constructor(other: InoutPaymentCriteria) :
        this(
            other.fromDate,
            other.toDate,
            other.vehicleNo,
            other.resultType
        )

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
