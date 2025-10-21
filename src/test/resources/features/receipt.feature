Feature: Finalizing a receipt

  Scenario: Customer pays with Mastercard
    Given scanned products:
      | productId                           | name    | unitPrice | discount | priceAfterDiscount | vatRate | unit | quantity | totalPrice |
      | 550e8400-e29b-41d4-a716-446655440000 | Torebka | 159.00    | 0.00     | 159.00              | 23%     | szt  | 1        | 159.00     |
    When I finalize the receipt with payment method "MASTERCARD"
    Then I receive a PDF receipt with total "195.57"
