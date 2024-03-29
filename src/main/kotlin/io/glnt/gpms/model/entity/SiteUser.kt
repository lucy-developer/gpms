package io.glnt.gpms.model.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.glnt.gpms.model.entity.Auditable
import io.glnt.gpms.model.enums.YN
import io.glnt.gpms.model.enums.UserRole
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(schema = "glnt_parking", name="site_user")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SiteUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", unique = true, nullable = false)
    var idx: Long? = null,

    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "user_name", unique = false, nullable = false)
    var userName: String,

    @Column(name = "user_phone", nullable = false)
    var userPhone: String?,

    @Column(name = "user_email", nullable = true)
    var userEmail: String? = null,

    @Column(name = "check_use", nullable = false, columnDefinition = "varchar(1) default 'Y'")
    @Enumerated(value = EnumType.STRING)
    var checkUse: YN? = YN.Y,

    @Column(name = "wrong_count", nullable = true, columnDefinition = "tinyint(1) default 0")
    var wrongCount: Int? = 0,

    @Column(name = "password_date")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    var passwordDate: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", unique = false, nullable = false)
    var role: UserRole? = UserRole.ADMIN,

    @Column(name = "login_date")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    var loginDate: LocalDateTime? = null,

    @Column(name = "corp_sn")
    var corpSn: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn", nullable = false)
    var delYn: YN? = YN.N
): Auditable(), Serializable {

}