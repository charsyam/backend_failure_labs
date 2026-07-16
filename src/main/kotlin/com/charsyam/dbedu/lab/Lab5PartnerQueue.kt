package com.charsyam.dbedu.lab

import com.charsyam.dbedu.repository.OrderRepository
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

data class PartnerOrderMessage(
    val orderId: Long,
    val publishedAt: LocalDateTime = LocalDateTime.now(),
)

data class PartnerFetchAttempt(
    val orderId: Long,
    val fetched: Boolean,
    val httpStatus: Int,
    val attemptedAt: LocalDateTime,
    val detail: String,
)

data class OrderApiResponse(
    val id: Long,
    val userId: Long,
    val status: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
)

@Component
class PartnerOrderQueue(
    restClientBuilder: RestClient.Builder,
    @param:Value("\${lab.our-api-base-url:http://localhost:8080}")
    private val ourApiBaseUrl: String,
) {
    private val queue = LinkedBlockingQueue<PartnerOrderMessage>()
    private val attempts = ConcurrentLinkedDeque<PartnerFetchAttempt>()
    private val executor = Executors.newSingleThreadExecutor()
    private val restClient = restClientBuilder.build()
    @Volatile
    private var running = true

    fun publish(message: PartnerOrderMessage) {
        queue.put(message)
    }

    fun attempts(): List<PartnerFetchAttempt> = attempts.toList().sortedByDescending { it.attemptedAt }

    @PostConstruct
    fun start() {
        executor.submit {
            while (running) {
                val message = queue.poll(200, TimeUnit.MILLISECONDS) ?: continue
                attempts.addFirst(fetchOrderFromOurApi(message.orderId))
                while (attempts.size > 100) {
                    attempts.removeLast()
                }
            }
        }
    }

    @PreDestroy
    fun stop() {
        running = false
        executor.shutdownNow()
    }

    private fun fetchOrderFromOurApi(orderId: Long): PartnerFetchAttempt =
        try {
            restClient.get()
                .uri("$ourApiBaseUrl/api/orders/{orderId}", orderId)
                .retrieve()
                .body(OrderApiResponse::class.java)
            PartnerFetchAttempt(
                orderId = orderId,
                fetched = true,
                httpStatus = HttpStatus.OK.value(),
                attemptedAt = LocalDateTime.now(),
                detail = "상대 서비스가 큐 메시지를 받고 우리 주문 API 조회에 성공했다.",
            )
        } catch (e: HttpClientErrorException.NotFound) {
            PartnerFetchAttempt(
                orderId = orderId,
                fetched = false,
                httpStatus = HttpStatus.NOT_FOUND.value(),
                attemptedAt = LocalDateTime.now(),
                detail = "상대 서비스가 큐 메시지를 받았지만 주문 트랜잭션 커밋 전이라 우리 주문 API에서 404를 받았다.",
            )
        }
}

@RestController
@RequestMapping("/api/orders")
class OrderApiController(
    private val orderRepository: OrderRepository,
) {
    @GetMapping("/{orderId}")
    fun get(@PathVariable orderId: Long): OrderApiResponse {
        val order = orderRepository.findById(orderId).orElseThrow { OrderNotFoundException() }
        return OrderApiResponse(
            id = requireNotNull(order.id),
            userId = order.userId,
            status = order.status,
            amount = order.amount,
            createdAt = order.createdAt,
        )
    }
}

class OrderNotFoundException : RuntimeException()

@RestControllerAdvice
class OrderApiExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(OrderNotFoundException::class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(): Map<String, String> = mapOf("message" to "order not found")
}
