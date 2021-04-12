#encoding: UTF-8
#language: de
Funktionalität:  Calculate Cart Total
  Some description ...

  Voraussetzungen: existing web shop with a given inventory
  Gegeben sei a web shop
  Und stock of "A" with price of 2.0
  Und stock of "B" with price of 4.0


  Szenario: Checkout an empty cart
  Gegeben sei an empty cart
  Wenn checking out cart
  Dann should response with NO_CONTENT

  Szenarien: Checkout single product
  Angenommen an empty cart
  Und put "<Product>" into cart
  Wenn checking out cart
  Dann total should be "<ExpectedTotal>"
  Beispiele:
  | Product | ExpectedTotal |
  | A       | 2.00          |
  | B       | 4.00          |
  | C       | 3.00          |
