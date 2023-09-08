NOTE: All test cases rely on the contents of the submitted .txt files remaining the same.

__Test 1: Seller Test Case (login seller, create customer)__
This test case includes two users. User 1 is a seller, User 2 is a customer. This serves to test concurrency.
1. User 1 starts Server.java.
2. User 1 starts Client.java.
3. User 1 selects “Log in”.
4. User 1 selects “Seller”.
5. User 1 selects the username textbox and enters “kobe24@purdue.edu” for their username. This is an existing account and the username is valid.
6. User 1 selects the password textbox and enters “wrong password” for their password. This is the incorrect password for the account.
7. An error message, reading “Either your username or password is incorrect!”, is displayed
8. User 1 is prompted for a username again and enters “kobe24@purdue.edu”.
9. User 1 selects the password textbox and enters “blackMamba8” for their password. This is the correct password. The user should be presented with the seller menu. 
10. User 2 starts a new instance of Client.java.
11. User 2 selects “Create an account”.
12. User 2 selects “Customer”.
13. User 1 selects “Edit account”.
14. User 1 selects the username textbox and changes his username to “kobe25@purdue.edu”.
15. User 1 selects the password textbox and changes his password to “blackMamba9”.
16. User 2 selects the username textbox and enters “purduepete18@purdue.edu”.
17. User 2 selects the password textbox and enters “BoilerUp!”.
18. User 2 selects “Browse all products”.
19. User 2 selects “9. Toys R Us: Harry Potter Lego - $45”.
20. User 2 presses “Ok” on the description and availability menus.
21. User 2 selects “Purchase” and selects “Ok” on the “Purchase successful!” menu.
22. User 1 selects “Add store”.
23. User 1 selects the store name textbox and enters “Bryant Merch”
24. User 1 selects “Create”.
25. User 1 selects “Yes” 
26. User 1 selects the product name textbox and enters “Jersey”.
27. User 1 selects the product description textbox and enters “Kobe Bryant Lakers jersey”.
28. User 1 selects the product price textbox and enters “50”.
29. User 1 selects the product availability textbox and enters “24”.
30. User 1 selects “Yes”.
31. User 1 selects the product name textbox and enters “Shoes”.
32. User 1 selects the product description textbox and enters “Kobe Bryant game worn shoes.”.
33. User 1 selects the product price textbox and enters “100”.
34. User 1 selects the product availability textbox and enters “2”.
35. User 1 selects “No”.
36. User 2 selects “Browse all products”.
37. User 2 selects “14. Bryant Merch: Shoes - $100”.
38. User 2 selects “Ok” on the description and availability windows.
39. User 2 selects “Continue browsing” and “Ok” on the purchase window.
40. User 2 selects “13. Kobes merch store: Jersey - $50”.
41. User 2 selects “Ok” on the description and availability windows.
42. User 2 selects “Purchase” and “Ok” on the purchase window.
43. User 2 selects “Ok” on the “Purchase successful!” window.
44. User 2 selects “Log out”.
45. User 2 clicks the “x” on the “Goodbye!” window.
46. User 1 selects “Check statistics”.
47. User 1 selects “By product sales”.
48. User 1 sees\
        1. Harry Potter Lego - Purchases: 2\
        2. Jersey - Purchases: 1\
        3. Star Wars Lego - Purchases: 1
50. User 1 selects “Ok”
51. User 1 selects “Edit store”
52. User 1 selects the edit store dropdown and selects “Bryant Merch”
53. User 1 selects “Yes”
54. User 1 selects the change name textbox and enters “Kobe Merch”
55. User 1 selects “No”
56. User 1 selects “No”
57. User 1 selects “Delete store”
58. User 1 selects the delete store dropdown and selects “Kobe Merch”
59. User 1 selects “Log out” and hits “Ok” on the “Goodbye” message

Expected Results: 
1. A new line “purduepete18@purdue.edu,BoilerUp!” is added to customer.txt
2. Two new lines\
      Toys R Us;purduepete18@purdue.edu;Harry Potter Lego;45\
      Kobe Merch;purduepete18@purdue.edu;Jersey;50\
   are added to customerPurchases.txt
3. The last line of seller.txt is changed from “kobe24@purdue.edu,blackMamba8” to “kobe25@purdue.edu,blackMamba9”
4. Two new lines\
      Toys R Us;Harry Potter Lego;45\
      Bryant Merch;Jersey;50\
   are added to a new file called purduepete18.txt


STATUS: passed

__Test 2: Customer Test Case (Login Customer, Create Seller)__
This test case includes two users. User 1 is a customer, User 2 is a seller. This serves to test concurrency.
1. User 1 launches application
2. User 1 selects “Log in” 
3. User 1 selects “Customer” 
4. User 1 selects the username textbox and enters “cary”
5. User 1 receives error message
6. User 1 selects the username textbox and enters “cary@purdue.edu”
7. User 1 selects the password textbox and enters “password”
8. User 1 receives error message
9. User 1 selects the username textbox and enters “cary@purdue.edu”
10. User 1 selects the password textbox and enters “knightLife”
11. User 1 selects “Browse all Products”
12. User 1 selects “6. Michaels Restuarant: Cheeseburger - $8”
13. User 1 receives description message
14. User 1 receives availability message
15. User 1 selects “Purchase”
16. User 1 receives success message
17. User 1 selects “Search for a product or store”
18. User 1 selects the search product textbox and enters “McDonalds”
19. User 1 receives error message
20. User 1 selects the search product textbox and searches “Cookies”
21. User 1 selects “1. Carls Cookies: Chocolate Chip Cookie - $5”
22. User 1 receives description message
23. User 1 receives availability message
24. User 1 selects “Continue Browsing”
25. User 1 selects “3. Carls Cookies: Pumpkin Cookie - $7”
26. User 1 receives description message
27. User 1 receives availability message
28. User 1 selects “Purchase”
29. User 1 receives success message
30. User 1 selects “Review Purchase History”
31. User 1 selects “No”
32. User 1 selects “Review Purchase History”
33. User 1 selects “Yes”
34. User 1 receives success message
35. User 1 selects “Check statistics”
36. User 1 selects “Alphabetical Order”
37. User 1 receives sorted statistics message: “Carls Coffee - Total Purchases: 2 - Your Purchases: 1 Carls Cookies - Total Purchases: 4 - Your Purchases: 4 - Johns CarShop - Total Purchases: 1 - Your Purchases: 1 Michaels Restuarant - Total Purchases: 1 - Your Purchases: 1 Toys R Us - Total Purchases: 2 - Your Purchases: 1”
38. User 1 selects “Check statistics”
39. User 1 selects “By total sold product”
40. User 1 receives sorted statistics message: “Carls Cookies - Total Purchases: 4 - Your Purchases: 4  Carls Coffee - Total Purchases: 2 - Your Purchases: 1 Toys R Us - Total Purchases: 2 - Your Purchases: 1 Michaels Restuarant - Total Purchases: 1 - Your Purchases: 1 Johns CarShop - Total Purchases: 1 - Your Purchases: 1”
41. User 1 selects “Check statistics”
42. User 1 selects “By products you bought”
43. User 1 receives sorted statistics message: “Carls Cookies - Total Purchases: 4 - Your Purchases: 4 Michaels Restuarant - Total Purchases: 1 - Your Purchases: 1 Johns CarShop - Total Purchases: 1 - Your Purchases: 1 Carls Coffee - Total Purchases: 2 - Your Purchases: 1 Toys R Us - Total Purchases: 2 - Your Purchases: 1"
44. User 2 launches application
45. User 2 selects “Create an account”
46. User 2 selects “Customer”
47. User 2 selects the Cancel button
48. User 2 relaunches application
49. User 2 selects “Create an account”
50. User 2 selects “Seller”
51. User 2 selects username creation textbox and enters “user2”
52. User 2 receives an error message
53. User 2 selects username creation textbox and enters “user2@purdue.edu”
54. User 2 selects password creation textbox and enters “password” 
55. User 2 selects “Add store”
56. User 2 selects store name textbox and types in "New Store"
57. User 2 selects "Create Store”
58. User 2 selects “Yes”
59. User 2 selects add product name textbox and enters “New Product”
60. User 2 selects add product description textbox and enters “awesome”
61. User 2 selects add product price textbox and enters “seven”
62. User 2 receives an error message
63. User 2 selects add product price textbox and enters “7”
64. User 2 selects add product availability textbox and enters “-100”
65. User 2 receives an error message
66. User 2 selects add product availability textbox and enters “100”
67. User 2 selects “No”
68. User 1 selects “Browse all products”
69. User 1 selects “13. New Store: New Product - $7”
70. User 1 receives description message
71. User 1 receives availability message
72. User 1 selects “Purchase”
73. User 1 receives success message
74. User 2 selects “Check statistics”
75. User 2 selects “By customer purchases”
76. User 2 receives sorted statistics message: “1.cary@purdue.edu - Purchases: 1”
77. User 2 selects “Log Out”
78. User 2 receives logout  message. User 2 program ends
79. User 1 selects cancel button. User 1 programs end

EXPECTED OUTPUT: 
1. New file created called “cary@purdue.eduPurchases.txt”
2. “New Store;New Product;7;user2@purdue.edu” added to Marketplace.txt 
3. “Michaels Restuarant;Cheeseburger;8” added to cary@purdue.edu.txt
4. “Carls Cookies;Pumpkin Cookie;7” added to cary@purdue.edu.txt
5. “New Store;New Product;7” added to cary@purdue.edu.txt
6. New file created called “user2@purdue.edu.txt”
7. “Michaels Restuarant;cary@purdue.edu;Cheeseburger;8” added to customerPurchases.txt
8. “Carls Cookies:cary@purdue.edu;Pumpkin Cookie;7” added to customerPurchases.txt
9. “New Store;cary@purdue.edu;New Product;7” added to customerPurchases.txt

STATUS: passed
STATUS: passed
