package io.glnt.gpms.io.glnt.gpms.handler.dashboard.common.service

import io.glnt.gpms.common.api.CommonResult
import io.glnt.gpms.exception.CustomException
import io.glnt.gpms.handler.dashboard.common.model.reqParkingDiscountSearchTicket
import io.glnt.gpms.handler.discount.model.reqSearchDiscount
import io.glnt.gpms.handler.discount.service.DiscountService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DashboardCommService {
    companion object : KLogging()

    @Autowired
    lateinit var discountService: DiscountService

    @Throws(CustomException::class)
    fun parkingDiscountSearchTicket(request: reqParkingDiscountSearchTicket): CommonResult {
        try {
            val searchItem = reqSearchDiscount()
            if (request.searchLabel == "CORPID") searchItem.corpId = request.searchText
            return discountService.getByCorp(searchItem)
        }catch (e: CustomException){
            logger.error { "parkingDiscountSearchTicket failed ${e.message}" }
            return CommonResult.error("parkingDiscountSearchTicket failed ${e.message}")
        }
    }
}