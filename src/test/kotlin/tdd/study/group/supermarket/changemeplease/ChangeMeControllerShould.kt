package tdd.study.group.supermarket.changemeplease

import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import tdd.study.group.supermarket.acceptance.ChangeMeControllerContract

class ChangeMeControllerShould : ChangeMeControllerContract() {
    override val application: HttpHandler = changeMeController()

    @Test
    internal fun `start here`() {
        TODO("start here with your unit tests ... ")
    }
}
