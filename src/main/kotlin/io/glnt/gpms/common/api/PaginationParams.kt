package io.glnt.gpms.common.api

data class PaginationParams(val page: Int, val pageSize: Int)
data class PaginationQuery(
    val page: Int?,
    val pageSize: Int?
) {
    companion object {
        fun default() = PaginationQuery(null, null)
    }
}
