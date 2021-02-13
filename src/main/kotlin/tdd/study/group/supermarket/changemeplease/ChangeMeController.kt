package tdd.study.group.supermarket.changemeplease

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes

fun changeMeController(): HttpHandler {
    return routes(
        "/supermarket" bind routes(
            "/checkout" bind Method.POST to { request: Request ->
                Response(Status.NO_CONTENT)
            }
        )
    )
}
