package io.glnt.gpms.model.repository

import io.glnt.gpms.model.entity.Corp
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CorpRepository: JpaRepository<Corp, Long>, JpaSpecificationExecutor<Corp> {
    fun findBySn(sn: Long): Corp?
    fun findByCorpId(corpId: String) : Corp?
    fun findByCorpName(corpName: String): Corp?
    fun findByCorpNameAndCeoName(corpName: String, ceoName: String) : Corp?
//    fun findAll(specification: Specification<Corp>): List<Corp>?
    fun findByCorpNameContaining(corpName: String): List<Corp>?
}