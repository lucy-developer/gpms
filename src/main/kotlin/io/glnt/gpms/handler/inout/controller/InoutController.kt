package io.glnt.gpms.handler.inout.controller

import io.glnt.gpms.common.api.*
import io.glnt.gpms.common.configs.ApiConfig
import io.glnt.gpms.exception.CustomException
import io.glnt.gpms.service.InoutService
import io.glnt.gpms.handler.inout.model.reqAddParkIn
import io.glnt.gpms.handler.inout.model.reqAddParkOut
import io.glnt.gpms.model.dto.request.resParkInList
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/${ApiConfig.API_VERSION}/inout"]
)
@CrossOrigin(origins = arrayOf("*"), allowedHeaders = arrayOf("*"))
class InoutController {

    @Autowired
    private lateinit var inoutService: InoutService

//    @RequestMapping(value = ["/parkin"], method = [RequestMethod.POST])
//    @Throws(CustomException::class)
//    fun parkIn(@RequestBody request: reqAddParkIn) : ResponseEntity<CommonResult> {
//        val result = inoutService.parkIn(request)
//        return when(result.code){
//            ResultCode.CREATED.getCode() -> ResponseEntity(result, HttpStatus.CREATED)
//            ResultCode.SUCCESS.getCode() -> ResponseEntity(result, HttpStatus.OK)
//            else -> ResponseEntity(result, HttpStatus.BAD_REQUEST)
//        }
//    }

    @RequestMapping(value = ["/detail/{request}"], method = [RequestMethod.GET])
    @Throws(CustomException::class)
    fun getParkInOutDetail(@PathVariable request: Long) : ResponseEntity<CommonResult> {
        logger.trace { "getParkInOutDetail inseq $request" }
        val result = inoutService.getParkInOutDetail(request)
        return when (result.code) {
            ResultCode.SUCCESS.getCode() -> ResponseEntity(result, HttpStatus.CREATED)
            else -> ResponseEntity(result, HttpStatus.BAD_REQUEST)
        }
    }

    @RequestMapping(value = ["/update"], method = [RequestMethod.POST])
    @Throws(CustomException::class)
    fun updateInout(@PathVariable request: resParkInList) : ResponseEntity<CommonResult> {
        logger.trace { "update INOUT $request" }
        val result = inoutService.updateInout(request)
        if (result == null) {
            return ResponseEntity(result, HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity.ok(CommonResult.data(result))
    }

    companion object : KLogging()
}