package tdd.study.group.supermarket.acceptance

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.matchers.shouldBe
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import tdd.study.group.supermarket.changemeplease.Checkout
import tdd.study.group.supermarket.changemeplease.Result
import tdd.study.group.supermarket.changemeplease.changeMeController
import tdd.study.group.supermarket.changemeplease.checkoutLens
import tdd.study.group.supermarket.changemeplease.totalLens

@Tag("acceptance")
@TestMethodOrder(MethodOrderer.Random::class)
class CalculateCartTotalAcceptanceTest : ChangeMeControllerContract() {
    private val server: Http4kServer = changeMeController().asServer(Undertow(0))
    override val application: HttpHandler
        get() = SetBaseUriFrom(Uri.of("http://localhost:${server.port()}")).then(OkHttp())

    @BeforeEach
    fun startServer() {
        server.start()
    }

    @AfterEach
    fun stopServer() {
        server.stop()
    }

    @Test
    fun `should calculate the total of the cart given a list of skus`() {
        val response =
            application(
                Request(Method.POST, "/supermarket/checkout").with(
                    checkoutLens of Checkout(listOf("A", "B", "A", "B", "A", "A", "A"))
                )
            )

        response.status shouldBe Status.CREATED

        totalLens(response) shouldBe Result("24.00")

        response.bodyString() shouldEqualJson """
                {
                  "total": 24.00
                }
                """
        response.bodyString() shouldMatchJson """
                {
                  "total": 24.00
                }
                """
    }

}
