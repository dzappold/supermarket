package tdd.study.group.supermarket.e2e

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tdd.study.group.supermarket.e2e.Matchers.answerShouldBe

object Matchers {
    infix fun Response.answerShouldBe(expected: Int) {
        assertThat(this, hasStatus(OK) and hasBody(expected.toString()))
    }
}

class EndToEndTest {
    private val client = OkHttp()
    private val server = MyMathServer(0)

    @BeforeEach
    fun setup() {
        server.start()
    }

    @AfterEach
    fun teardown() {
        server.stop()
    }

    @Test
    fun `all endpoints are mounted correctly`() {
        assertThat(client(Request(GET, "http://localhost:${server.port()}/ping")), hasStatus(OK))
        client(Request(GET, "http://localhost:${server.port()}/add?value=1&value=2")) answerShouldBe 3
        client(Request(GET, "http://localhost:${server.port()}/multiply?value=1&value=2")) answerShouldBe 2
    }
}

class AddFunctionalTest {
    private val client = MyMathsApp()

    @Test
    fun `adds values together`() {
        client(Request(GET, "/add?value=1&value=2")) answerShouldBe 3
        client(Request(GET, "/add?value=1&value=1&value=1&value=1&value=1&value=1")) answerShouldBe 6
    }

    @Test
    fun `answer is zero when no values`() {
        client(Request(GET, "/add")) answerShouldBe 0
    }

    @Test
    fun `bad request when some values are not numbers`() {
        assertThat(client(Request(GET, "/add?value=1&value=notANumber")), hasStatus(BAD_REQUEST))
    }
}

class MultiplyFunctionalTest {
    private val client = MyMathsApp()

    @Test
    fun `products values together`() {
        client(Request(GET, "/multiply?value=2&value=4")) answerShouldBe 8
    }

    @Test
    fun `answer is zero when no values`() {
        client(Request(GET, "/multiply")) answerShouldBe 0
    }

    @Test
    fun `bad request when some values are not numbers`() {
        assertThat(client(Request(GET, "/multiply?value=1&value=notANumber")), hasStatus(BAD_REQUEST))
    }
}

fun MyMathServer(port: Int): Http4kServer = MyMathsApp().asServer(Undertow(port))

fun MyMathsApp(): HttpHandler = ServerFilters.CatchLensFailure.then(
    routes(
        "/ping" bind GET to { Response(OK) },
        "/add" bind GET to calculate(List<Int>::sum),
        "/multiply" bind GET to calculate { valuesToMultiply ->
            valuesToMultiply.fold(1) { memo, next -> memo * next }
        }
    )
)

private fun calculate(function: (List<Int>) -> Int): HttpHandler = { request: Request ->
    val values = Query.int().multi.defaulted("value", listOf())(request)
    val answer = if (values.isEmpty()) 0 else function(values)
    Response(OK).body(answer.toString())
}

