# Supermarket checkout micro-service tdd kata

Kata to practice outside-in TDD
with [double loop](http://coding-is-like-cooking.info/2013/04/outside-in-development-with-double-loop-tdd/).

The idea is let tdd to guide us through **architecture and domain design** in a more realistic and day to day context, a
micro-services environment.

Inspired on [supermarket code kata](http://codekata.com/kata/kata01-supermarket-pricing/)

## Kata description

[Slides](https://slides.com/albertllousasortiz-1/supermarket-checkout-kata)

We are going to implement a supermarket checkout, that basically calculates the total price given a list of **products**
a customer have on the **cart**.

To do that we will need and stock of the supermarket, it should have this information, at least:

```bash
SKU     Price   Description 
---------------------------
  A      2.00     Potato 
  B      4.00     Tomato
  ...    ...      ...
```

There are no restrictions on how to implement the stock.

Here are some examples of expected input and output for the app.

```bash
curl --request POST http://localhost:8080/supermarket/checkout \
  --header 'Content-Type: application/json' \
  --data '{ skus: ["A","B","A","B","A","A","A"] }' \
  --include
```

The output should be `201 CREATED` with body:

```json
{
  "total": 24.00
}
```

**Notes**:

* Rule: Write production code in terms of architecture and domain
* SKUs are [Stock keeping units](https://en.wikipedia.org/wiki/Stock_keeping_unit)
* Should respond with `204 NO_CONTENT` for an empty cart request, `{ "skus": [] }`
* Try to collect some potential test ideas upfront
```kotlin
// TODO("ordering one item -> total costs are item price")
// ...
```

Have fun!

## Tips (My rules)

* Let TDD guide/drive you through the design
* Don't Mock What You Don't Own
* Don't mock everything (double testing is ok)
* SoC: Separate orchestration from calculation
* YAGNI

## Where to start

We have provided the exercise with
the [acceptance test](./src/test/kotlin/tdd.study.group/supermarket/acceptance/CalculateCartTotalAcceptanceTest.kt)
already set up, we don't want to waste time designing an API.

So jump [here](./src/test/kotlin/tdd.study.group/supermarket/changemeplease/CheckoutControllerTest.kt) and start coding.

## Bonus: Second part

Let's finish first the previous one 😏 Think about new features / variations like:

* discount system
* out of stock
* ...

## Tech stack

* Kotlin
* http4k
* Testing libraries/frameworks:
    * [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
    * [hamkrest]()
    * [kotest](http://kotest.io)
    * [Mockk](https://mockk.io/)
    * [ArchUnit]()

They are just the most common used, feel free to add/remove/change any dependency that fits to you

## Running the tests

Run all tests:

```bash
./gradlew test
```

Run unit tests:

```bash
./gradlew unitTest
```

Run integration tests:

```bash
./gradlew integrationTest
```

Run acceptance tests:

```bash
./gradlew acceptanceTest
```

## Useful links

- [Outside-in Double Loop TDD](http://coding-is-like-cooking.info/2013/04/outside-in-development-with-double-loop-tdd/)
- [London School](https://www.slideshare.net/pkofler/outsidein-test-driven-development-the-london-school-of-tdd)

