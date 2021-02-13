package tdd.study.group.supermarket.acceptance

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.matchers.shouldBe
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import tdd.study.group.supermarket.changemeplease.changeMeController

@Tag("acceptance")
@TestMethodOrder(MethodOrderer.Random::class)
class CalculateCartTotalAcceptanceTest : TestAgainstServer() {

    override val application: HttpHandler
        get() = changeMeController()

    @Test
    fun `should calculate the total of the cart given a list of skus`() {
        val response =
            client(
                Request(
                    Method.POST,
                    "/supermarket/checkout"
                ).body(
                    body = """
                            {
                                "skus": ["A","B","A","B","A","A","A"] 
                            }
                            """
                )
            )
        response.status shouldBe Status.CREATED
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
