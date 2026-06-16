package com.chaltteok.user.auth.email

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils

@Service
class UserEmailServiceImpl(private val mailSender: JavaMailSender) : UserEmailService {

    override fun sendPasswordReset(email: String, tempPassword: String) {
        val safe = sanitize(email)
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(safe)
            setSubject("[찰떡딜] 임시 비밀번호가 발급되었습니다")
            setText(buildResetHtml(tempPassword), true)
        }
        mailSender.send(message)
    }

    private fun sanitize(v: String) = v.replace("[\r\n]".toRegex(), "")

    private fun buildResetHtml(temp: String): String {
        val safeTemp = HtmlUtils.htmlEscape(temp)
        return """
            <div style="font-family:sans-serif;max-width:480px;margin:0 auto">
              <h2 style="color:#e11d48">찰떡딜 임시 비밀번호 안내</h2>
              <p>임시 비밀번호로 로그인 후 반드시 비밀번호를 변경해 주세요.</p>
              <div style="background:#f9fafb;border-radius:8px;padding:16px;font-size:20px;font-family:monospace;letter-spacing:2px;text-align:center;">
                <strong>$safeTemp</strong>
              </div>
              <p style="color:#6b7280;font-size:12px;margin-top:16px">
                이 메일은 자동 발송된 메일입니다. 본인이 요청하지 않은 경우 고객센터에 문의하세요.
              </p>
            </div>
        """.trimIndent()
    }
}
