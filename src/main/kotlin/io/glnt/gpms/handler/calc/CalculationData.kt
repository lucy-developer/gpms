package io.glnt.gpms.handler.calc

import io.glnt.gpms.common.utils.DateUtil
import io.glnt.gpms.handler.parkinglot.service.ParkinglotService
import io.glnt.gpms.model.entity.CgBasic
import io.glnt.gpms.model.repository.FarePolicyRepository
import io.glnt.gpms.model.entity.FarePolicy
import io.glnt.gpms.model.enums.DelYn
import io.glnt.gpms.model.enums.VehicleType
import io.glnt.gpms.model.repository.CgBasicRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

/**
 */
@Service
class CalculationData(
    // 주차장 요금 설정
    private var parkingFareInfo: List<FarePolicy>,
    private var farePolicyRepository: FarePolicyRepository
) {

    // 주차장 기본 설정
    lateinit var cgBasic: CgBasic

    @Autowired
    private lateinit var cgBasicRepository: CgBasicRepository

    /**
     * 요금 계산 시작전 필수 데이터 세팅
     * 해당 데이터들은 상속해주는 CalculationData에 세팅되어 있음
     * @param mData 객체 생성을 요청하는 곳에서 미리 만들어와야 함
     */
    @PostConstruct
    fun init() {
        cgBasicRepository.findByDelYn(DelYn.N)?: kotlin.run {
            cgBasicRepository.saveAndFlush(
                CgBasic(sn = null, serviceTime = 0, regTime = 0, dayMaxAmt = 0, effectDate = DateUtil.stringToLocalDateTime(DateUtil.nowDateTime, "yyyy-MM-dd HH:mm:ss"), delYn = DelYn.N))
        }
//        if (parkinglotService.isPaid()) {
        parkingFareInfo = farePolicyRepository.findAll()
        cgBasicRepository.findByDelYn(DelYn.N)?.let {
            cgBasic = it
        }
//        }
    }

    fun getBizHourInfoForDate(date: String, vehicleType: VehicleType): List<FarePolicy> {
        val data = parkingFareInfo.filter {
            it.delYn == DelYn.N &&
                    it.vehicleType == vehicleType &&

//                    ( it.week == DateUtil.getWeek(date) || it.week!!.contains(WeekType.ALL)) &&
                    ( it.effectDate!! <= DateUtil.beginTimeToLocalDateTime(date) &&
                            it.expireDate!! > DateUtil.lastTimeToLocalDateTime(date) )

        }

        return data.sortedBy { it.startTime }
    }

    fun getBizHourInfoForDateTime(date: String, time: String, vehicleType: VehicleType): FarePolicy {
        val data = parkingFareInfo.filter {
            it.delYn == DelYn.N &&
                    it.vehicleType == vehicleType &&
                    ( (it.startTime!! <= time && time < it.endTime!! && it.startTime!! < it.endTime!!) ||
                      (it.startTime!! > it.endTime!! && it.endTime!! > time && it.startTime!! >= time) ||
                      (it.startTime!! > it.endTime!! && it.endTime!! < time && it.startTime!! <= time) ) &&
//                    ( it.week == DateUtil.getWeek(date) || it.week!!.contains(WeekType.ALL)) &&
                    ( it.effectDate!! <= DateUtil.beginTimeToLocalDateTime(date) &&
                            it.expireDate!! > DateUtil.lastTimeToLocalDateTime(date) )

        }

        return data[0]
    }

    fun getBizHourInfoForPreDateTime(date: String, vehicleType: VehicleType, startTime: String): List<FarePolicy> {
        val data = parkingFareInfo.filter {
            it.delYn == DelYn.N &&
                    it.vehicleType == vehicleType &&
//                    ( it.week == DateUtil.getWeek(date) || it.week == WeekType.ALL) &&
                    ( it.effectDate!! <= DateUtil.beginTimeToLocalDateTime(date) &&
                            it.expireDate!! > DateUtil.lastTimeToLocalDateTime(date) ) &&
                    ( ( it.startTime!! > it.endTime!! ) && ( it.endTime!! > startTime) )
        }

        return data.sortedBy { it.startTime }
    }


}