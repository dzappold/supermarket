package tdd.study.group.supermarket.changemeplease

import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters.CatchAll
import org.http4k.filter.ServerFilters.CatchLensFailure
import org.http4k.format.Moshi.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.Locale.ENGLISH


typealias Sku = String
typealias Price = Double

data class Product(val sku: Sku, val price: Price)

class ProductRepository {

    private val products = mutableMapOf<Sku, Product>()

    fun priceOf(sku: Sku): Price {
        return products[sku]?.price ?: Double.NaN
    }

    fun productsSortedBySku() = products.values.sortedBy(Product::sku)

    fun saveOrUpdate(product: Product) {
        products.putIfAbsent(product.sku, product)
        products.replace(product.sku, product)
    }
}

object CatchInvalidSkus {
    operator fun invoke(errorStatus: Status = Status.NOT_FOUND): Filter = Filter { next: HttpHandler ->
        { request: Request ->
            try {
                next(request)
            } catch (e: NoSuchElementException) {
                Response(errorStatus)
            }
        }
    }
}

fun changeMeController(
    inventory: Map<String, Double>,
    repository: ProductRepository
): HttpHandler {
    return CatchAll().then(
        CatchLensFailure.then(
            CatchInvalidSkus().then(
                routes(
                    "/supermarket" bind routes(
                        "/checkout" bind Method.POST to { request: Request ->
                            val skus = checkoutLens(request).skus
                            if (skus.hasEmptyCart()) {
                                Response(Status.NO_CONTENT)
                            } else {
                                Response(Status.CREATED)
                                    .with(totalLens of calculateCartTotal(inventory, skus, repository))
                            }
                        }
                    )
                )
            )
        )
    )
}

private fun calculateCartTotal(inventory: Map<String, Double>, skus: List<String>, repository: ProductRepository): Result {

    val total = skus.map { sku -> inventory[sku] ?: throw NoSuchElementException("element does not exist") }.sum()
    return Result("%.2f".format(ENGLISH, total))
}

private fun List<String>.hasEmptyCart() = isEmpty()

data class Result(val total: String)

val totalLens = Body.auto<Result>().toLens()

data class Checkout(val skus: List<String>)

val checkoutLens = Body.auto<Checkout>().toLens()
