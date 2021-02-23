package tdd.study.group.supermarket.changemeplease

import org.http4k.core.Response
import org.http4k.kotest.shouldHaveBody

fun Response.shouldHaveEmptyBody() = shouldHaveBody("")
