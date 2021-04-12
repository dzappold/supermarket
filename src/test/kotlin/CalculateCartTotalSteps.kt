import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.kotest.matchers.and
import io.kotest.matchers.be
import io.kotest.matchers.should
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.kotest.haveBody
import org.http4k.kotest.haveStatus
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import tdd.study.group.supermarket.changemeplease.Checkout
import tdd.study.group.supermarket.changemeplease.ProductRepository
import tdd.study.group.supermarket.changemeplease.Result
import tdd.study.group.supermarket.changemeplease.changeMeController
import tdd.study.group.supermarket.changemeplease.checkoutLens
import tdd.study.group.supermarket.changemeplease.totalLens


@Suppress("Unused")
class CalculateCartTotalSteps : En {
    init {
        val inventory = mutableMapOf<String, Double>()
        val repository = ProductRepository()
        lateinit var server: Http4kServer
        val cart = mutableListOf<String>()
        fun application() = ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:${server.port()}")).then(OkHttp())

        Given("a web shop") {
        }
        And("stock of {string} with price of {double}") { item: String, price: Double ->
            inventory[item] = price
        }
        Given("an empty cart") {
            server = changeMeController(
                inventory,
                repository
            ).asServer(Undertow(0)).start()
        }
        When("checking out cart") {
        }
        Then("should response with NO_CONTENT") {
            val response =
                application()(
                    Request(Method.POST, "/supermarket/checkout").with(
                        checkoutLens of Checkout(cart)
                    )
                )
            server.stop()
            response should haveStatus(Status.NO_CONTENT)
        }
        And("put {string} into cart") { product: String ->
            println("add $product to cart")
            cart.add(product)
            println(cart)
        }
        Then("total should be {string}") { expectedTotal: String ->
            val response =
                application()(
                    Request(Method.POST, "/supermarket/checkout").with(
                        checkoutLens of Checkout(cart)
                    )
                )
            server.stop()
            response should (haveStatus(Status.CREATED) and haveBody(totalLens, be(Result(expectedTotal))))
        }
        Given("the following products exist:") { table: DataTable ->
            table.asLists()
                .drop(1)
                .forEach { (product, price) ->
                    inventory[product] = price.toDouble()
                }
            server = changeMeController(
                inventory,
                repository
            ).asServer(Undertow(0)).start()
        }
    }
}
