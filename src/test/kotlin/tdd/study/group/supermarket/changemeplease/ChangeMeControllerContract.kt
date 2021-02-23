package tdd.study.group.supermarket.acceptance

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.MethodOrderer.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import tdd.study.group.supermarket.changemeplease.Checkout
import tdd.study.group.supermarket.changemeplease.checkoutLens
import tdd.study.group.supermarket.changemeplease.shouldHaveEmptyBody

@TestMethodOrder(Random::class)
abstract class ChangeMeControllerContract {
    abstract val application: HttpHandler

    @Test
    fun `should not calculate anything if the cart is empty of products`() {
        val response =
            application(
                Request(Method.POST, "/supermarket/checkout").with(
                    checkoutLens of Checkout(emptyList())
                )
            )
        response shouldHaveStatus Status.NO_CONTENT
        response.shouldHaveEmptyBody()
    }

//     TODO("move all tests to this spot, which should be executed as unit and acceptance test (be careful and compare runtime)")
}

