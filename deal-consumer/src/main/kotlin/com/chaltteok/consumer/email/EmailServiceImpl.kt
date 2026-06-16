package com.chaltteok.consumer.email

import com.chaltteok.core.common.OrderEventFormatters
import com.chaltteok.core.event.OrderCancelledEvent
import com.chaltteok.core.event.OrderCompletedEvent
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils

@Service
class EmailServiceImpl(private val mailSender: JavaMailSender) : EmailService {

    override fun sendOrderConfirmation(event: OrderCompletedEvent, userEmail: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(sanitize(userEmail))
            setSubject("[찰떡딜] 주문이 완료되었습니다 - ${sanitize(event.orderNumber)}")
            setText(buildConfirmHtml(event), true)
        }
        mailSender.send(message)
    }

    override fun sendOrderCancellation(event: OrderCancelledEvent, userEmail: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(sanitize(userEmail))
            setSubject("[찰떡딜] 주문이 취소되었습니다 - ${sanitize(event.orderNumber)}")
            setText(buildCancellationHtml(event), true)
        }
        mailSender.send(message)
    }

    override fun sendPasswordReset(email: String, tempPassword: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(sanitize(email))
            setSubject("[찰떡딜] 임시 비밀번호가 발급되었습니다")
            setText(buildPasswordResetHtml(tempPassword), true)
        }
        mailSender.send(message)
    }

    // 이메일 헤더 인젝션 방지: CRLF 제거
    private fun sanitize(value: String): String = value.replace("[\r\n]".toRegex(), "")

    private fun buildConfirmHtml(event: OrderCompletedEvent): String {
        // XSS 방지: 사용자 입력 유래 값 HTML 이스케이프
        val safeName = HtmlUtils.htmlEscape(event.userName)
        val safeProduct = HtmlUtils.htmlEscape(event.productName)
        val safeOrderNumber = HtmlUtils.htmlEscape(event.orderNumber)
        val safeAmount = "%,d".format(event.totalAmount)
        val safeOrderedAt = HtmlUtils.htmlEscape(event.orderedAt.format(OrderEventFormatters.DISPLAY))

        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:sans-serif;max-width:600px;margin:0 auto;padding:20px;color:#333">
              <div style="border-bottom:3px solid #e11d48;padding-bottom:16px;margin-bottom:24px">
                <h2 style="color:#e11d48;margin:0">찰떡딜 주문 확인</h2>
              </div>
              <p style="font-size:16px">${safeName}님, 주문이 완료되었습니다.</p>
              <table style="width:100%;border-collapse:collapse;margin:20px 0">
                <tr style="background:#f9f9f9">
                  <td style="padding:12px;border-bottom:1px solid #eee;color:#666;width:120px">주문번호</td>
                  <td style="padding:12px;border-bottom:1px solid #eee;font-weight:bold">${safeOrderNumber}</td>
                </tr>
                <tr>
                  <td style="padding:12px;border-bottom:1px solid #eee;color:#666">상품명</td>
                  <td style="padding:12px;border-bottom:1px solid #eee">${safeProduct}</td>
                </tr>
                <tr style="background:#f9f9f9">
                  <td style="padding:12px;border-bottom:1px solid #eee;color:#666">결제금액</td>
                  <td style="padding:12px;border-bottom:1px solid #eee;color:#e11d48;font-weight:bold">${safeAmount}원</td>
                </tr>
                <tr>
                  <td style="padding:12px;color:#666">주문일시</td>
                  <td style="padding:12px">${safeOrderedAt}</td>
                </tr>
              </table>
              <p style="margin-top:24px;font-size:12px;color:#999">
                궁금한 점이 있으시면 support@chaltteok.com 으로 문의해 주세요.
              </p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun buildCancellationHtml(event: OrderCancelledEvent): String {
        val safeName = HtmlUtils.htmlEscape(event.userName)
        val safeOrderNumber = HtmlUtils.htmlEscape(event.orderNumber)
        val safeAmount = "%,d".format(event.totalAmount)
        val safeCancelledAt = HtmlUtils.htmlEscape(event.cancelledAt.format(OrderEventFormatters.DISPLAY))

        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:sans-serif;max-width:600px;margin:0 auto;padding:20px;color:#333">
              <div style="border-bottom:3px solid #6b7280;padding-bottom:16px;margin-bottom:24px">
                <h2 style="color:#6b7280;margin:0">찰떡딜 주문 취소 안내</h2>
              </div>
              <p style="font-size:16px">${safeName}님, 주문이 취소되었습니다.</p>
              <table style="width:100%;border-collapse:collapse;margin:20px 0">
                <tr style="background:#f9f9f9">
                  <td style="padding:12px;border-bottom:1px solid #eee;color:#666;width:120px">주문번호</td>
                  <td style="padding:12px;border-bottom:1px solid #eee;font-weight:bold">${safeOrderNumber}</td>
                </tr>
                <tr>
                  <td style="padding:12px;border-bottom:1px solid #eee;color:#666">취소금액</td>
                  <td style="padding:12px;border-bottom:1px solid #eee">${safeAmount}원</td>
                </tr>
                <tr style="background:#f9f9f9">
                  <td style="padding:12px;color:#666">취소일시</td>
                  <td style="padding:12px">${safeCancelledAt}</td>
                </tr>
              </table>
              <p style="margin-top:24px;font-size:12px;color:#999">
                궁금한 점이 있으시면 support@chaltteok.com 으로 문의해 주세요.
              </p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun buildPasswordResetHtml(tempPassword: String): String {
        val safeTemp = HtmlUtils.htmlEscape(tempPassword)

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
