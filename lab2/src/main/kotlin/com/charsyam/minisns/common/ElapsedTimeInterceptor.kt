package com.charsyam.minisns.common

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 컨트롤러 진입부터 응답 직전까지의 서버 전체 처리 시간을 측정한다.
 * 요청 파싱, 서비스 로직, 트랜잭션 커밋 시점의 flush 비용까지 포함한다.
 * 측정값은 서버 로그로 남긴다.
 */
@Component
class ElapsedTimeInterceptor : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        request.setAttribute(START_NANOS, System.nanoTime())
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val startNanos = request.getAttribute(START_NANOS) as? Long ?: return
        val elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000

        val query = request.queryString?.let { "?$it" } ?: ""
        log.info("{} {}{} -> {}ms", request.method, request.requestURI, query, elapsedMillis)
    }

    companion object {
        private const val START_NANOS = "elapsedTime.startNanos"
    }
}
