package io.glnt.gpms.handler.dashboard.user.service

import io.glnt.gpms.common.api.CommonResult
import io.glnt.gpms.common.api.ResultCode
import io.glnt.gpms.common.utils.DateUtil
import io.glnt.gpms.exception.CustomException
import io.glnt.gpms.handler.dashboard.user.model.*
import io.glnt.gpms.handler.discount.model.reqSearchInoutDiscount
import io.glnt.gpms.service.InoutService
//import io.glnt.gpms.handler.inout.service.checkItemsAre
import io.glnt.gpms.model.criteria.ParkInCriteria
import io.glnt.gpms.model.dto.entity.InoutDiscountDTO
import io.glnt.gpms.model.entity.CorpTicketInfo
import io.glnt.gpms.model.enums.TicketType
import io.glnt.gpms.service.DiscountService
import io.glnt.gpms.service.InoutDiscountService
import io.glnt.gpms.service.ParkInQueryService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Integer.min
import kotlin.collections.ArrayList


@Service
class DashboardUserService(
    private val parkInQueryService: ParkInQueryService,
    private val inoutDiscountService: InoutDiscountService
) {
    companion object : KLogging()

    @Autowired
    private lateinit var inoutService: InoutService

    @Autowired
    private lateinit var discountService: DiscountService
    
    @Throws(CustomException::class)
    fun parkingDiscountSearchVehicle(request: reqVehicleSearch) : CommonResult {
        try {
            val data = getParkInByVehicleNo(request.vehicleNo)
//                ResultCode.SUCCESS.getCode() -> {
//                    val lists = parkins.data as? List<*>?
//                    lists!!.checkItemsAre<ParkIn>()?.filter { it.outSn == 0L }?.let { list ->
//                        list.forEach {
////                            logger.debug { "image path ${it.image!!.substring(it.image!!.indexOf("/park"))}" }
//                            data.add(
//                                resVehicleSearch(
//                                    sn = it.sn!!,
//                                    vehicleNo = it.vehicleNo!!,
//                                    inDate = DateUtil.formatDateTime(it.inDate!!, "yyyy-MM-dd HH:mm:ss"),
//                                    imImagePath = it.image?.let { if (it.contains("/park")) it.substring(it.indexOf("/park")) else null }?: kotlin.run { null }
//                                )
//                            )
//                        }
//
//                    }
//                }
            return CommonResult.data(data)
        }catch (e: CustomException){
            logger.error { "parkingDiscountSearchVehicle failed ${e.message}" }
            return CommonResult.error("parkingDiscountSearchVehicle failed ${e.message}")
        }
    }

    @Throws(CustomException::class)
    fun parkingDiscountSearchTicket(request: reqParkingDiscounTicketSearch) : CommonResult {
        try {
            val data = getParkInByVehicleNo(request.vehicleNo)
//            val parkins = inoutService.searchParkInByVehicleNo(request.vehicleNo, "")
//            val data = ArrayList<resVehicleSearch>()
//            when(parkins.code) {
//                ResultCode.SUCCESS.getCode() -> {
//                    val lists = parkins.data as? List<*>?
//                    lists!!.checkItemsAre<ParkIn>()?.filter { it.outSn == 0L }?.let { list ->
//                        list.forEach {
////                            logger.debug { "image path ${it.image!!.substring(it.image!!.indexOf("/park"))}" }
//                            data.add(
//                                resVehicleSearch(
//                                    sn = it.sn!!,
//                                    vehicleNo = it.vehicleNo!!,
//                                    inDate = DateUtil.formatDateTime(it.inDate!!, "yyyy-MM-dd HH:mm:ss"),
//                                    imImagePath = it.image!!.substring(it.image!!.indexOf("/park"))
//                                )
//                            )
//                        }
//
//                    }
//                }
//            }
            return CommonResult.data(data)
        }catch (e: CustomException){
            logger.error { "parkingDiscountSearchVehicle failed ${e.message}" }
            return CommonResult.error("parkingDiscountSearchVehicle failed ${e.message}")
        }
    }

    private fun getParkInByVehicleNo(vehicleNo: String) : ArrayList<resVehicleSearch> {
        val parkIns = parkInQueryService.findByCriteria(ParkInCriteria(vehicleNo = vehicleNo))
        val data = ArrayList<resVehicleSearch>()
        if (!parkIns.isNullOrEmpty()) {
            parkIns.filter { it.outSn == 0L }.forEach { it ->
                logger.debug { "image path ${it.image!!.substring(it.image!!.indexOf("/park"))}" }
                data.add(
                    resVehicleSearch(
                        sn = it.sn!!,
                        vehicleNo = it.vehicleNo!!,
                        inDate = DateUtil.formatDateTime(it.inDate!!, "yyyy-MM-dd HH:mm:ss"),
                        imImagePath = it.image?.let { if (it.contains("/park")) it.substring(it.indexOf("/park")) else null }?: kotlin.run { null }
                    )
                )
            }
        }
        return data
    }

//    @Throws(CustomException::class)
//    fun parkingDiscountAbleTickets(request: reqParkingDiscountAbleTicketsSearch) : CommonResult {
//        try {
//            val discountTickets = if (request.inSn == null){
//                discountService.searchCorpTicketByCorp(reqParkingDiscountSearchTicket(searchLabel = "CORPSN", searchText=request.corpSn.toString()))
//            } else {
//                discountService.getDiscountableTickets(
//                    reqDiscountableTicket(
//                        corpSn = request.corpSn,
//                        date = request.inDate,
//                        inSn = request.inSn
//                    )
//                )
//            }
//
//            when(discountTickets.code) {
//                ResultCode.SUCCESS.getCode() -> {
//                    discountTickets.data?.let {
//                        val lists = discountTickets.data as List<CorpTicketInfo>
////                        val groupedData: Map<DiscountClass, List<CorpTicket>> =
////                            lists.stream().collect(Collectors.groupingBy { it.discountClass!! })
//                        val result = ArrayList<HashMap<String, Any?>>()
//                        lists.forEach { data ->
//                            result.add(hashMapOf(
//                                "discountClassSn" to data.classSn,
//                                "discountName" to data.discountClass!!.discountNm,
//                                "dayRange" to data.discountClass!!.dayRange,
//                                "timeRange" to data.discountClass!!.timeRange,
//                                "timeTarget" to data.discountClass!!.timeTarget,
//                                "onceMax" to data.discountClass!!.disMaxNo,
//                                "dayMax" to data.discountClass!!.disMaxDay,
//                                "monthMax" to data.discountClass!!.disMaxMonth,
//                                "totalCnt" to if (request.inSn == null) data.totalQuantity else data.totalQuantity - data.useQuantity,
//                                "ableCnt" to if (request.inSn == null) data.totalQuantity - data.useQuantity else if (data.ableCnt!! > data.totalQuantity - data.useQuantity) data.totalQuantity - data.useQuantity else data.ableCnt, //,
//                                "unit" to data.discountClass!!.unitTime,
//                                "todayUse" to discountService.getTodayUseDiscountTicket(request.corpSn, data.classSn)
//                            ))
//                        }
//                        return CommonResult.data(result)
//                    }
//                    return CommonResult.data()
//                }
//                ResultCode.VALIDATE_FAILED.getCode() -> {
//                    return CommonResult.notfound("ticket not found")
//                }
//                else -> return CommonResult.error("ticket not found")
//            }
//
//        }catch (e: CustomException){
//            logger.error { "parkingDiscountAbleTickets failed ${e.message}" }
//            return CommonResult.error("parkingDiscountAbleTickets failed ${e.message}")
//        }
//    }

    @Throws(CustomException::class)
    fun parkingDiscountAddTicket(request: ArrayList<reqParkingDiscountAddTicket>): CommonResult {
        try{
            request.filter { it.cnt > 0 }.forEach { addTicket ->
                // 적합여부 확인
                // Once 가능 횟수 > Day > Month
                var useCnt = addTicket.cnt
                discountService.getAbleDiscountCnt(reqSearchInoutDiscount(ticketSn = addTicket.classSn, inSn = addTicket.inSn))?.let {
                    if (it < addTicket.cnt) return CommonResult.error("Exceeded the number of possible discounts")
                    // 보유 할인 확인
                    val data = discountService.searchCorpTicketByCorpAndDiscountClass(addTicket.corpSn, addTicket.classSn)
                    when(data.code) {
                        ResultCode.SUCCESS.getCode() -> {
                            val corps: CorpTicketInfo? = data.data as? CorpTicketInfo
                            corps?.let { ticket ->
                                if (ticket.totalQuantity-ticket.useQuantity < addTicket.cnt) return CommonResult.error("Exceeded the number of possible discounts")
                                val cnt = addTicket.cnt
                                do {
                                    discountService.getDiscountableTicketsBySn(ticket.sn!!)?.let { history ->
                                        useCnt = min(history.totalQuantity - history.useQuantity, addTicket.cnt)
                                        inoutDiscountService.save(
                                            InoutDiscountDTO(
                                                inSn = addTicket.inSn,
                                                discountType = TicketType.CORPTICKET,
                                                ticketHistSn = history.sn!!,
                                                quantity = useCnt,
                                                discountClassSn = corps.corpTicketClass!!.discountClassSn!!,
                                                corpSn = corps.corpSn,
                                                ticketClassSn = corps.corpTicketClass!!.sn!!
                                            ))

//                                        discountService.addInoutDiscount(
//                                            reqAddInoutDiscount(
//                                                inSn = addTicket.inSn,
//                                                discountType = TicketType.CORPTICKET,
//                                                ticketSn = history.sn!!,
//                                                quantity = useCnt,
//                                                discountClassSn = corps.corpTicketClass!!.discountClassSn!!,
//                                                corpSn = corps.corpSn,
//                                                ticketClassSn = corps.corpTicketClass!!.sn!!
//                                            )
//                                        )
                                        history.useQuantity = history.useQuantity.plus(useCnt)
                                        discountService.updateCorpTicketHistory(history)
                                    }
                                    addTicket.cnt = addTicket.cnt - useCnt
                                }while(addTicket.cnt > 0)
                                ticket.useQuantity = ticket.useQuantity.plus(cnt)
                                discountService.updateCorpTicketInfo(ticket)
                            }
//                                ?: run {
//                                return CommonResult.notfound("corp ticket not found")
//                            }
                        }
                        else -> return CommonResult.error("No discount available")
                    }
                }?: run {
                    return CommonResult.Companion.error("No discount available")
                }
            }
            return CommonResult.data()
        }catch (e: CustomException){
            logger.error { "parkingDiscountAbleTickets failed $e" }
            return CommonResult.error("parkingDiscountAddTicket failed ${e.message}")
        }
    }

//    @Throws(CustomException::class)
//    fun parkingDiscountSearchApplyTicket(request: reqParkingDiscountApplyTicketSearch) : CommonResult {
//        try {
//            val tickets = discountService.searchCorpTicketByCorp(reqParkingDiscountSearchTicket(searchLabel = "CORPSN", searchText=request.corpSn.toString()))
//            when(tickets.code) {
//                ResultCode.SUCCESS.getCode() -> {
////                    val result = ArrayList<InoutDiscount>()
//                    val result = ArrayList<ResDiscountTicetsApplyList>()
//                    val lists = tickets.data as List<CorpTicketInfo>
//                    lists.forEach { it ->
//                        if ( (request.discountClassSn != null && it.classSn == request.discountClassSn) || request.discountClassSn == null ) {
//                            discountService.searchInoutDiscount(
//                                reqApplyInoutDiscountSearch(
//                                    ticketSn = it.sn!!, startDate = request.startDate, endDate = request.endDate,
//                                    applyStatus = request.applyStatus
//                                )
//                            )?.let { its ->
//                                its.forEach {
//                                    result.add(
//                                        ResDiscountTicetsApplyList(
//                                            sn = it.sn!!,
//                                            vehicleNo = it.parkIn!!.vehicleNo!!,
//                                            discountType = it.discontType!!,
//                                            discountClassSn = it.discountClassSn!!,
//                                            discountNm = it.ticketHist!!.ticketInfo!!.discountClass!!.discountNm,
//                                            calcYn = it.calcYn!!,
//                                            delYn = it.delYn!!,
//                                            createDate = it.createDate!!,
//                                            quantity = it.quantity!!
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                    }
//                    return CommonResult.data(result.sortedByDescending { it.createDate })
//                }
//            }
//            return CommonResult.data()
//        }catch (e: CustomException){
//            logger.error { "parkingDiscountSearchApplyTicket failed $e" }
//            return CommonResult.error("parkingDiscountSearchApplyTicket failed ${e.message}")
//        }
//    }

    @Throws(CustomException::class)
    fun parkingDiscountCancelTicket(sn: Long) : CommonResult {
        try {
            if (discountService.cancelInoutDiscount(sn)) {
                return CommonResult.data()
            }
            return CommonResult.error("parkingDiscountCancelTicket failed $sn")

        }catch (e: CustomException){
            logger.error { "parkingDiscountCancelTicket failed $e" }
            return CommonResult.error("parkingDiscountCancelTicket failed ${e.message}")
        }
    }
}