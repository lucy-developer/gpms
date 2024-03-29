package io.glnt.gpms.model.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.glnt.gpms.model.entity.Auditable
import io.glnt.gpms.model.enums.YN
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(schema = "glnt_parking", name="tb_park_alarm_setting")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ParkAlarmSetting(
    @Id
    @Column(name = "siteid", unique = true, nullable = false)
    var siteid: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_alarm")
    var payAlarm: YN? = YN.N,

    @Column(name = "pay_limit_time")
    var payLimitTime: Int? = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "gate_alarm")
    var gateAlarm: YN? = YN.N,

    @Column(name = "gate_limit_time")
    var gateLimitTime: Int? = 0,

    @Column(name = "gate_counter_reset_time")
    var gateCounterResetTime: Int? = 0

): Auditable(), Serializable {

}
