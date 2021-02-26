package io.glnt.gpms.handler.relay.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.glnt.gpms.common.api.CommonResult
import io.glnt.gpms.common.api.ResultCode
import io.glnt.gpms.common.utils.DateUtil
import io.glnt.gpms.common.utils.JSONUtil
import io.glnt.gpms.exception.CustomException
import io.glnt.gpms.handler.facility.model.reqPayData
import io.glnt.gpms.handler.facility.model.reqPaymentResponse
import io.glnt.gpms.handler.facility.model.reqPaymentResult
import io.glnt.gpms.handler.facility.model.reqVehicleSearchList
import io.glnt.gpms.handler.facility.service.FacilityService
import io.glnt.gpms.handler.inout.model.reqAddParkOut
import io.glnt.gpms.handler.inout.service.InoutService
import io.glnt.gpms.handler.inout.service.checkItemsAre
import io.glnt.gpms.handler.parkinglot.service.ParkinglotService
import io.glnt.gpms.handler.relay.model.FacilitiesFailureAlarm
import io.glnt.gpms.handler.relay.model.FacilitiesStatusNoti
import io.glnt.gpms.handler.relay.model.paystationvehicleListSearch
import io.glnt.gpms.handler.relay.model.reqRelayHealthCheck
import io.glnt.gpms.handler.tmap.model.*
import io.glnt.gpms.handler.tmap.service.TmapSendService
import io.glnt.gpms.model.entity.Failure
import io.glnt.gpms.model.entity.ParkAlarmSetting
import io.glnt.gpms.model.entity.ParkIn
import io.glnt.gpms.model.entity.VehicleListSearch
import io.glnt.gpms.model.enums.checkUseStatus
import io.glnt.gpms.model.repository.FailureRepository
import io.glnt.gpms.model.repository.ParkAlarmSetttingRepository
import io.glnt.gpms.model.repository.ParkFacilityRepository
import io.glnt.gpms.model.repository.VehicleListSearchRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class RelayService {
    companion object : KLogging()

    lateinit var parkAlarmSetting: ParkAlarmSetting

//    lateinit var failureList: ArrayList<Failure>

    @Autowired
    private lateinit var tmapSendService: TmapSendService

    @Autowired
    private lateinit var parkinglotService: ParkinglotService

    @Autowired
    private lateinit var facilityService: FacilityService

    @Autowired
    private lateinit var inoutService: InoutService

    @Autowired
    private lateinit var parkAlarmSettingRepository: ParkAlarmSetttingRepository

    @Autowired
    private lateinit var failureRepository: FailureRepository

    @Autowired
    private lateinit var parkingFacilityRepository: ParkFacilityRepository

    @Autowired
    private lateinit var vehicleListSearchRepository: VehicleListSearchRepository

    fun fetchParkAlarmSetting(parkId: String) {
        parkAlarmSettingRepository.findBySiteid(parkId)?.let { it ->
            parkAlarmSetting = it
        }
    }

    fun facilitiesHealthCheck(request: reqRelayHealthCheck) {
        logger.info { "facilitiesHealthCheck $request" }
        try {
            if (parkinglotService.isTmapSend())
                tmapSendService.sendHealthCheckRequest(request, "")

            request.facilitiesList.forEach { facility ->
                facilityService.updateHealthCheck(facility.facilitiesId, facility.status!!)
            }

            if (parkAlarmSetting.payAlarm == checkUseStatus.Y && parkAlarmSetting.payLimitTime!! > 0) {
                paymentHealthCheck()
            }

        } catch (e: CustomException){
            logger.error { "facilitiesHealthCheck failed ${e.message}" }
        }

    }

    fun statusNoti(request: reqRelayHealthCheck) {
        logger.info { "statusNoti $request" }
        try {
            val result = ArrayList<FacilitiesStatusNoti>()

            request.facilitiesList.forEach { facility ->
                val data = facilityService.updateStatusCheck(facility.facilitiesId, facility.status!!)
                if (data != null) {
                    result.add(FacilitiesStatusNoti(facilitiesId = facility.facilitiesId, STATUS = facility.status!!))
                    // close 상태 수신 시 error 상태 check
                    if (facility.status == "DOWN") {
                        saveFailure(
                            Failure(
                                sn = null,
                                issueDateTime = LocalDateTime.now(),
//                                        expireDateTime = LocalDateTime.now(),
                                facilitiesId = facility.facilitiesId,
                                fName = parkinglotService.getFacility(facility.facilitiesId)!!.fname,
                                failureCode = "crossingGateLongTimeOpen",
                                failureType = "NORMAL"
                            )
                        )
                    } else {
                        saveFailure(
                            Failure(
                                sn = null,
                                issueDateTime = LocalDateTime.now(),
//                                        expireDateTime = LocalDateTime.now(),
                                facilitiesId = facility.facilitiesId,
                                fName = parkinglotService.getFacility(facility.facilitiesId)!!.fname,
                                failureCode = "crossingGateBarDamageDoubt",
                                failureType = "NORMAL"
                            )
                        )
                    }
                }
            }

            if (parkinglotService.isTmapSend() && result.isNotEmpty())
                tmapSendService.sendFacilitiesStatusNoti(reqTmapFacilitiesStatusNoti(facilitiesList = result), null)

        } catch (e: CustomException){
            logger.error { "statusNoti failed ${e.message}" }
        }
    }

    fun failureAlarm(request: reqRelayHealthCheck) {
        logger.info { "failureAlarm $request" }
        try {
            request.facilitiesList.forEach { failure ->
                parkinglotService.getFacility(facilityId = failure.facilitiesId)?.let { facility ->
                    // 정산기 정상, 비정상
                    saveFailure(
                        Failure(sn = null,
                            issueDateTime = LocalDateTime.now(),
//                                        expireDateTime = LocalDateTime.now(),
                            facilitiesId = failure.facilitiesId,
                            fName = facility.fname,
                            failureCode = failure.failureAlarm,
                            failureType = failure.status)
                    )
//                    }
//                    if (failure.failureAlarm == "crossingGateBarDamageDoubt") {
//                            // 차단기
//                            saveFailure(
//                                Failure(sn = null,
//                                    issueDateTime = LocalDateTime.now(),
////                                        expireDateTime = LocalDateTime.now(),
//                                    facilitiesId = failure.facilitiesId,
//                                    fName = facility.fname,
//                                    failureCode = failure.failureAlarm,
//                                    failureType = failure.failureAlarm)
//                            )
//                        } else {
//                            // 정산기
//                            if (facility.category == "PAYSTATION") {
//                                if (failure.healthStatus == "Normal") {
//                                    facilityService.updateHealthCheck(failure.facilitiesId, failure.healthStatus!!)
//                                } else {
//                                    facilityService.updateHealthCheck(failure.facilitiesId, failure.failureAlarm!!)
//                                }
//                                saveFailure(
//                                    Failure(sn = null,
//                                        issueDateTime = LocalDateTime.now(),
////                                        expireDateTime = LocalDateTime.now(),
//                                        facilitiesId = facility.facilitiesId,
//                                        fName = facility.fname,
//                                        failureCode = failure.failureAlarm!!,
//                                        failureType = failure.healthStatus)
//                                )
//                            }
//                        }
//                    }
                    if (parkinglotService.isTmapSend() && failure.status != "normal")
                        tmapSendService.sendFacilitiesFailureAlarm(FacilitiesFailureAlarm(facilitiesId = failure.facilitiesId, failureAlarm = failure.failureAlarm!!), null)
                }
            }

        } catch (e: CustomException){
            logger.error { "failureAlarm failed ${e.message}" }
        }
    }

    fun paymentHealthCheck() {
        logger.info { "paymentHealthCheck" }
        try {
            val result = ArrayList<FacilitiesFailureAlarm>()
            parkinglotService.getFacilityByCategory("PAYSTATION")?.let { facilities ->
                facilities.forEach { facility ->
                    inoutService.lastSettleData(facility.facilitiesId!!)?.let { out ->
                        //정산기 마지막 페이 시간 체크
                        if (DateUtil.diffHours(
                                DateUtil.stringToLocalDateTime(out.approveDatetime!!, "yyyy-MM-dd HH:mm:ss"),
                                LocalDateTime.now()) > parkAlarmSetting.payLimitTime!!) {
                            if (parkinglotService.isTmapSend() && result.isNotEmpty())
                                tmapSendService.sendFacilitiesFailureAlarm(FacilitiesFailureAlarm(facilitiesId = facility.facilitiesId!!, failureAlarm = "dailyUnAdjustment"), null)
                            saveFailure(
                                Failure(sn = null,
                                    issueDateTime = LocalDateTime.now(),
//                                    expireDateTime = LocalDateTime.now(),
                                    facilitiesId = facility.facilitiesId,
                                    fName = facility.fname,
                                    failureCode = "dailyUnAdjustment",
                                    failureType = "dailyUnAdjustment")
                            )
                        } else {
                            saveFailure(
                                Failure(sn = null,
                                        issueDateTime = LocalDateTime.now(),
//                                        expireDateTime = LocalDateTime.now(),
                                        facilitiesId = facility.facilitiesId,
                                        fName = facility.fname,
                                        failureCode = "dailyUnAdjustment",
                                        failureType = "NORMAL")
                            )
                        }
                    } ?: run{

                    }
                }
            }
        }catch  (e: CustomException){
            logger.error { "paymentHealthCheck failed ${e.message}" }
        }
    }

    fun saveFailure(request: Failure) {
        logger.info { "saveFailure $request" }
        try {
            if (request.failureType!!.toUpperCase() == "NORMAL") {
                failureRepository.findTopByFacilitiesIdAndFailureCodeAndExpireDateTimeIsNullOrderByIssueDateTimeDesc(
                    request.facilitiesId!!,
                    request.failureCode!!
                )?.let {
                    it.expireDateTime = LocalDateTime.now()
                    failureRepository.save(it)
                }
            } else {
                failureRepository.findTopByFacilitiesIdAndFailureCodeAndExpireDateTimeIsNullOrderByIssueDateTimeDesc(
                    request.facilitiesId!!,
                    request.failureCode!!
                )?.let { it ->
                    it.failureFlag = it.failureFlag!! + 1
                    it.expireDateTime = null
                    failureRepository.save(it)
                } ?: run {
                    failureRepository.save(request)
                }
            }
        }catch (e: CustomException){
            logger.error { "saveFailure failed ${e.message}" }
        }
    }

    @Throws(CustomException::class)
    fun resultPayment(request: reqApiTmapCommon, facilityId: String){
        logger.info { "resultPayment request $request facilityId $facilityId" }
//        request.contents = JSONUtil.getJsObject(request.contents)
        val contents = JSONUtil.readValue(JSONUtil.getJsObject(request.contents).toString(), reqPaymentResult::class.java)
        facilityService.sendPaystation(
            reqPaymentResponse(
                chargingId = contents.transactionId,
                vehicleNumber = contents.vehicleNumber
            ),
            gate = parkingFacilityRepository.findByFacilitiesId(facilityId)!!.gateId,
            requestId = request.requestId!!,
            type = "paymentResponse"
        )
        val result = inoutService.paymentResult(contents, request.requestId!!, parkingFacilityRepository.findByFacilitiesId(facilityId)!!.gateId)
    }

    @Throws(CustomException::class)
    fun searchCarNumber(request: reqApiTmapCommon, facilityId: String){
        logger.info { "searchCarNumber request $request facilityId $facilityId" }
//        request.contents = JSONUtil.getJsObject(request.contents)
        val contents = JSONUtil.readValue(JSONUtil.getJsObject(request.contents).toString(), reqSendVehicleListSearch::class.java)
        request.requestId = parkinglotService.generateRequestId()

        if (parkinglotService.isTmapSend()) {
            // table db insert
            val requestId = parkinglotService.generateRequestId()
            vehicleListSearchRepository.save(VehicleListSearch(requestId = requestId, facilityId = facilityId))
            tmapSendService.sendTmapInterface(request, requestId, "vehicleListSearch")
        } else {
            val parkins = inoutService.searchParkInByVehicleNo(contents.vehicleNumber, "")
            val data = ArrayList<paystationvehicleListSearch>()

             when(parkins.code) {
                ResultCode.SUCCESS.getCode() -> {
                    val lists = parkins.data as? List<*>?
                    lists!!.checkItemsAre<ParkIn>()?.filter { it.outSn == 0L }?.let { list ->
                        list.forEach {
                            data.add(
                                paystationvehicleListSearch(
                                    vehicleNumber = it.vehicleNo!!,
                                    inVehicleDateTime = DateUtil.formatDateTime(it.inDate!!, "yyyy-MM-dd HH:mm:ss")))
                        }

                    }
                }
            }

            facilityService.sendPaystation(
                reqVehicleSearchList(
                    vehicleList = data,
                    result = "SUCCESS"
                ),
                gate = parkingFacilityRepository.findByFacilitiesId(facilityId)!!.gateId,
                requestId = request.requestId!!,
                type = "vehicleListSearchResponse"
            )
        }
    }

    @Throws(CustomException::class)
    fun requestAdjustment(request: reqApiTmapCommon, facilityId: String){
        logger.info { "requestAdjustment request $request facilityId $facilityId" }
        try {
            val contents = JSONUtil.readValue(JSONUtil.getJsObject(request.contents).toString(), reqAdjustmentRequest::class.java)
            request.requestId = parkinglotService.generateRequestId()

            if (parkinglotService.isTmapSend()) {
                // table db insert
//                val requestId = parkinglotService.generateRequestId()
//                vehicleListSearchRepository.save(VehicleListSearch(requestId = requestId, facilityId = facilityId))
//                tmapSendService.sendTmapInterface(request, requestId, "vehicleListSearch")
            } else {
                val gateId = parkingFacilityRepository.findByFacilitiesId(facilityId)!!.gateId
                inoutService.parkOut(reqAddParkOut(vehicleNo = contents.vehicleNumber,
                                                   facilitiesId = parkingFacilityRepository.findByGateIdAndCategory(gateId, "LPR")!![0].facilitiesId!!,
                                                   date = LocalDateTime.now(),
                                                   resultcode = "0",
                                                   uuid = JSONUtil.generateRandomBasedUUID()))
            }

        }catch (e: CustomException){
            logger.error { "saveFailure failed ${e.message}" }
        }
    }


//    fun searchCarNumber(request: reqSendVehicleListSearch): CommonResult? {
//        logger.info { "searchCarNumber request $request" }
//        if (parkinglotService.parkSite.tmapSend == "ON") {
//            // table db insert
//            val requestId = parkinglotService.generateRequestId()
//            vehicleListSearchRepository.save(VehicleListSearch(requestId = requestId, facilityId = request.facilityId))
//            tmapSendService.sendTmapInterface(request, requestId, "vehicleListSearch")
//        } else {
//            return inoutService.searchParkInByVehicleNo(request.vehicleNumber, "")
//        }
//        return null
//    }

//    fun <T : Any> readValue(any: String, valueType: Class<T>): T {
//        val data = JSONUtil.getJSONObject(any)
//        val factory = JsonFactory()
//        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
//        return jacksonObjectMapper().readValue(data.toString(), valueType)
//    }
}

