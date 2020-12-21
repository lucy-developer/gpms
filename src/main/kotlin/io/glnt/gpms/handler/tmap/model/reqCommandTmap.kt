package io.glnt.gpms.handler.tmap.model

import io.glnt.gpms.model.enums.SetupOption

data class reqCommandFacilities(
    var facilitiesId: String,
    var BLOCK: String?,
    var gateId: String?
)

data class reqCommandProfileSetup(
    var parkingSpotStatusnotiCycle: String?=null,
    var facilitiesStatusNotiCycle: String?=null,
    var gateList: ArrayList<gateInfo>?=null,
    var messageList: ArrayList<messageInfo>?=null
)

data class gateInfo(
    var gateId: String,
    var takeAction: String?,
    var seasonTicketTakeAction: String?,
    var whiteListTakeAction: String?
)

data class messageInfo(
    var messageType: String?,
    var message: String?
)

data class reqCommandGateTakeActionSetup(
    var gateList: ArrayList<gateList>
)

data class gateList(
    var gateId: String,
    var takeActionType: String,
    var setupOption: SetupOption,
    var vehicleList: ArrayList<vehicleList>
)

data class vehicleList(
    var vehicleNumber: String,
    var messageType: String,
    var startDateTime: String,
    var endDateTime: String

)

data class reqCommandVehicleListSearchResponse(
    var result: String,
    var error: String? = null,
    var vehicleList: ArrayList<vehicleSearchList>? = null
)

data class vehicleSearchList(
    var vehicleNumber: String,
    var inVehicleDateTime: String,
    var inVehicleImageId: String? = null,
    var parkingLocation: String? = null
)