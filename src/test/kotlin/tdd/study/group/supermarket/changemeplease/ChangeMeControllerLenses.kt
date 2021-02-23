package tdd.study.group.supermarket.changemeplease

import org.http4k.core.Body
import org.http4k.format.Moshi.auto

val checkoutLens = Body.auto<Checkout>().toLens()
val totalLens = Body.auto<Result>().toLens()
