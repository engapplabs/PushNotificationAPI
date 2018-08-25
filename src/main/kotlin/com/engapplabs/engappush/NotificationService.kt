package com.engapplabs.engappush

import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
@EnableScheduling
class NotificationService {

    internal val DATE_FORMATTER: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")

    internal val emitters: MutableList<SseEmitter> = CopyOnWriteArrayList()

    fun addEmitter(emitter: SseEmitter) {
        emitters.add(emitter)
    }

    fun removeEmitter(emitter: SseEmitter) {
        emitters.remove(emitter)
    }

    @Async
    @Scheduled(fixedRate = 5000)
    @Throws(IOException::class)
    fun doNotify() {
        val deadEmitters = ArrayList<Any>()
        emitters.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event()
                        .data(DATE_FORMATTER.format(Date()) + " : " + UUID.randomUUID().toString()))
            } catch (e: Exception) {
                deadEmitters.add(emitter)
            }
        }
        emitters.removeAll(deadEmitters)
    }

}

