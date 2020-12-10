package io.glnt.gpms.model.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.glnt.gpms.common.utils.DateUtil
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(schema = "glnt_parking", name="tb_corpticket")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CorpTicket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn", unique = true, nullable = false)
    var sn: Long?,

    @Column(name = "flag", nullable = false)
    var flag: Int = 0,

    @Column(name = "corpId", nullable = false)
    var corpId: String,

    @Column(name = "corpSn", nullable = false)
    var corpSn: Int,

    @Column(name = "disUse", nullable = false)
    var disUse: String,

    @Column(name = "unitTime")
    var unitTime: Int?,

    @Column(name = "disMaxNo")
    var disMaxNo: Int?,

    @Column(name = "disMaxDay")
    var disMaxDay: Int?,

    @Column(name = "disMaxMonth")
    var disMaxMonth: Int?,

    @Column(name = "disPrice")
    var disPrice: Int?,

    @Column(name = "effectDate", nullable = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    var effectDate: LocalDateTime? = null,

    @Column(name = "expireDate", nullable = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    var expireDate: LocalDateTime? = DateUtil.stringToLocalDateTime("9999-12-31 23:59:59")
) : Auditable(), Serializable {

}
