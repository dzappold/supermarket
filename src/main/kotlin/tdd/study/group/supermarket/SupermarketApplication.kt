package tdd.study.group.supermarket

import org.http4k.server.Undertow
import org.http4k.server.asServer
import tdd.study.group.supermarket.changemeplease.changeMeController

const val DEFAULT_SERVER_PORT = 8080

fun main(args: Array<String>) {
    changeMeController().asServer(Undertow(DEFAULT_SERVER_PORT)).start()
}
