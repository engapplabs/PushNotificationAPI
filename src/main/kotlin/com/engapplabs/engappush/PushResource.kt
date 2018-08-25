package com.engapplabs.engappush

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import org.springframework.web.bind.annotation.GetMapping
import java.util.concurrent.CopyOnWriteArrayList
import org.springframework.beans.factory.annotation.Autowired

@RestController
@RequestMapping("/v1/push")
class PushResource {

    @Autowired
    var service: NotificationService? = null

    val emitters: List<SseEmitter> = CopyOnWriteArrayList()

    @GetMapping("/notification")
    @Throws(InterruptedException::class, IOException::class)
    fun doNotify(): ResponseEntity<SseEmitter> {
        val emitter = SseEmitter()
        service!!.addEmitter(emitter)
        service!!.doNotify()
        emitter.onCompletion { service!!.removeEmitter(emitter) }
        emitter.onTimeout { service!!.removeEmitter(emitter) }

        return ResponseEntity(emitter, HttpStatus.OK)
    }

}
