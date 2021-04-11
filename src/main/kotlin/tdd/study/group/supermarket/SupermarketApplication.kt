package tdd.study.group.supermarket

import org.http4k.server.Undertow
import org.http4k.server.asServer
import tdd.study.group.supermarket.changemeplease.Product
import tdd.study.group.supermarket.changemeplease.ProductRepository
import tdd.study.group.supermarket.changemeplease.changeMeController

const val DEFAULT_SERVER_PORT = 8080

@Suppress("MagicNumber")
fun main(args: Array<String>) {
    val inventory = mapOf(
        "A" to 2.0,
        "B" to 4.0
    )

    val repository = ProductRepository().apply {
        inventory.map { (sku, price) ->
            saveOrUpdate(Product(sku, price))
        }
    }

    changeMeController(inventory, repository).asServer(Undertow(DEFAULT_SERVER_PORT)).start()
}
