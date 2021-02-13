package tdd.study.group.supermarket.acceptance

import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class TestAgainstServer {
    protected lateinit var server: Http4kServer
    abstract val application: HttpHandler
    protected lateinit var client: HttpHandler

    @BeforeEach
    fun setup() {
        server = application
            .asServer(Undertow(0))
            .start()
        client = SetBaseUriFrom(Uri.of("http://localhost:${server.port()}"))
            .then(OkHttp())
    }

    @AfterEach
    fun tearDown() {
        server.stop()
    }
}
