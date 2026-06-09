package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCompletedEvent
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(private val mailSender: JavaMailSender) : EmailService {

    override fun sendOrderConfirmation(event: OrderCompletedEvent) {
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(event.userEmail)
            setSubject("[찰떡딜] 주문이 완료되었습니다 - ${event.orderNumber}")
            setText(buildHtml(event), true)
        }
        mailSender.send(message)
    }

    private fun buildHtml(event: OrderCompletedEvent): String = """
        <!DOCTYPE html>
        <html>
        <body style="font-family:sans-serif;max-width:600px;margin:0 auto;padding:20px;color:#333">
          <div style="border-bottom:3px solid #e11d48;padding-bottom:16px;margin-bottom:24px">
            <h2 style="color:#e11d48;margin:0">찰떡딜 주문 확인</h2>
          </div>
          <p style="font-size:16px">${event.userName}님, 주문이 완료되었습니다.</p>
          <table style="width:100%;border-collapse:collapse;margin:20px 0">
            <tr style="background:#f9f9f9">
              <td style="padding:12px;border-bottom:1px solid #eee;color:#666;width:120px">주문번호</td>
              <td style="padding:12px;border-bottom:1px solid #eee;font-weight:bold">${event.orderNumber}</td>
            </tr>
            <tr>
              <td style="padding:12px;border-bottom:1px solid #eee;color:#666">상품명</td>
              <td style="padding:12px;border-bottom:1px solid #eee">${event.productName}</td>
            </tr>
            <tr style="background:#f9f9f9">
              <td style="padding:12px;border-bottom:1px solid #eee;color:#666">결제금액</td>
              <td style="padding:12px;border-bottom:1px solid #eee;color:#e11d48;font-weight:bold">${"%,d".format(event.totalAmount)}원</td>
            </tr>
            <tr>
              <td style="padding:12px;color:#666">주문일시</td>
              <td style="padding:12px">${event.orderedAt}</td>
            </tr>
          </table>
          <p style="margin-top:24px;font-size:12px;color:#999">
            궁금한 점이 있으시면 support@chaltteok.com 으로 문의해 주세요.
          </p>
        </body>
        </html>
    """.trimIndent()
}
