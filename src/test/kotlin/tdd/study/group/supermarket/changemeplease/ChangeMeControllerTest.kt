package tdd.study.group.supermarket.changemeplease

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import tdd.study.group.supermarket.acceptance.TestAgainstServer

@Tag("integration")
@TestMethodOrder(MethodOrderer.Random::class)
class ChangeMeControllerTest : TestAgainstServer() {

    override val application: HttpHandler = changeMeController()

    @Test
    fun `should not calculate anything if the cart is empty of products`() {
        val response =
            client(
                Request(Method.POST, "/supermarket/checkout")
                    .body(
                        """
                            { 
                                "skus": [] 
                            }
                        """
                    )
            )
        response shouldHaveStatus Status.NO_CONTENT
    }
}
