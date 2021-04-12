#encoding: UTF-8
#language: en
Business Need: Calculate Cart Total
  Some description ...

  Background: existing web shop with a given inventory
    Given a web shop
    And stock of "A" with price of 2.0
    And stock of "B" with price of 4.0


  Scenario: Checkout an empty cart
    Given an empty cart
    When checking out cart
    Then should response with NO_CONTENT

  Scenario Outline: Checkout single product
    Given an empty cart
    And put "<Product>" into cart
    When checking out cart
    Then total should be "<ExpectedTotal>"
    Examples:
      | Product | ExpectedTotal |
      | A       | 2.00          |
      | B       | 4.00          |

  Scenario: Another way ...
    Given the following products exist:
      | product | price |
      | A       | 2.00  |
      | B       | 4.00  |
      | C       | 3.00  |
    And put "C" into cart
    When checking out cart
    Then total should be "3.00"
