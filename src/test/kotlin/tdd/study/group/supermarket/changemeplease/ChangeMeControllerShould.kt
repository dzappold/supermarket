package tdd.study.group.supermarket.changemeplease

import io.kotest.matchers.shouldBe
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.Test
import java.util.*

class ChangeMeControllerShould : ChangeMeControllerContract() {
    private val inventory = mapOf(
        "A" to 2.0,
        "B" to 4.0
    )

    private val repository = ProductRepository().apply {
        inventory.map { (sku, price) ->
            saveOrUpdate(Product(sku, price))
        }
    }
    override val application: HttpHandler = changeMeController(inventory, repository)

    // no SKU -> empty response (NO_CONTENT)
    // SKU "A" -> single price of "A"
    // SKU "A" twice -> double price of "A"
    // SKU "B" -> inventory -> price of "B"
    // combinations
    // unknown SKU -> ? error ?

    @Test
    internal fun `calculate for a cart with single 'A' item, it's single price as total `() {
        val skus = listOf("A")
        val response = application(
            Request(Method.POST, "/supermarket/checkout")
                .with(checkoutLens of Checkout(skus))
        )
        response shouldHaveStatus Status.CREATED
        totalLens(response) shouldBe resultOf(skus)
    }

    @Test
    internal fun `calculate for a cart with single 'B' item, it's single price as total `() {
        val skus = listOf("B")
        val response = application(
            Request(Method.POST, "/supermarket/checkout")
                .with(checkoutLens of Checkout(skus))
        )
        response shouldHaveStatus Status.CREATED
        totalLens(response) shouldBe resultOf(skus)
    }

    private fun resultOf(skus: List<String>): Result {
        val total =
            skus.groupBy { s: String -> s }.map { entry -> inventory[entry.key]?.times(entry.value.size) ?: 0.0 }.sum()
        return Result(
            "%.2f".format(
                Locale.ENGLISH,
                total
            )
        )

    }

    @Test
    internal fun `calculate for a cart with two item of same SKU, it's double price as total `() {
        val response = application(
            Request(Method.POST, "/supermarket/checkout")
                .with(checkoutLens of Checkout(listOf("A", "A")))
        )
        response shouldHaveStatus Status.CREATED
        totalLens(response) shouldBe Result("4.00")
    }

    @Test
    internal fun `unknown sku`() {
        val response = application(
            Request(Method.POST, "/supermarket/checkout")
                .with(checkoutLens of Checkout(listOf("A", "UNKNOWN")))
        )
        response shouldHaveStatus Status.NOT_FOUND
    }

    @Test
    internal fun `invalid request`() {
        val response = application(
            Request(Method.POST, "/supermarket/checkout").body("this is invalid")
        )
        response shouldHaveStatus Status.BAD_REQUEST
    }
}
