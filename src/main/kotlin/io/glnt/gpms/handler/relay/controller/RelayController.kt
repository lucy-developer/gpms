package io.glnt.gpms.handler.relay.controller

import io.glnt.gpms.common.configs.ApiConfig
import io.glnt.gpms.handler.relay.model.reqRelayHealthCheck
import io.glnt.gpms.handler.relay.service.RelayService
import io.glnt.gpms.handler.tmap.model.reqApiTmapCommon
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/${ApiConfig.API_VERSION}/relay"]
)
@CrossOrigin(origins = arrayOf("*"), allowedHeaders = arrayOf("*"))
class RelayController {
    @Autowired
    private lateinit var relayService: RelayService

    @RequestMapping(value=["/health_check"], method=[RequestMethod.POST])
    fun healthCheck(@RequestBody request: reqRelayHealthCheck) {
        logger.info { "healthCheck category $request" }
        relayService.facilitiesHealthCheck(request)
    }

    @RequestMapping(value=["/failure_alarm"], method=[RequestMethod.POST])
    fun failureAlarm(@RequestBody request: reqRelayHealthCheck) {
        logger.info { "healthCheck category $request" }
        relayService.failureAlarm(request)
    }

    @RequestMapping(value=["/status_noti"], method=[RequestMethod.POST])
    fun statusNoti(@RequestBody request: reqRelayHealthCheck) {
        logger.info { "statusNoti category $request" }
        relayService.statusNoti(request)
    }

    @RequestMapping(value=["/payment_health"], method=[RequestMethod.GET])
    fun paymentHealthCheck() {
        logger.info { "paymentHealthCheck " }
        relayService.paymentHealthCheck()
    }

    @RequestMapping(value=["/paystation/result/{facilityId}"], method=[RequestMethod.POST])
    fun resultPayment(@RequestBody request: reqApiTmapCommon, @PathVariable facilityId: String) {
        logger.info { "resultPayment $request " }
        relayService.resultPayment(request, facilityId)
    }

    @RequestMapping(value = ["/paystation/search/vehicle/{facilityId}"], method = [RequestMethod.POST])
    fun searchCarNumber(@RequestBody request: reqApiTmapCommon, @PathVariable facilityId: String) {
        logger.info { "searchCarNumber $request " }
        relayService.searchCarNumber(request, facilityId)
    }

    @RequestMapping(value = ["/paystation/request/{facilityId}"], method = [RequestMethod.POST])
    fun requestPayment(@RequestBody request: reqApiTmapCommon, @PathVariable facilityId: String) {
        logger.info { "requestPayment $request " }
        relayService.searchCarNumber(request, facilityId)
    }

    companion object : KLogging()
}