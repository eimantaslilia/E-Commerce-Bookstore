# [Bookstore - a personal e-commerce project](http://bookstore-eimantas.eu-west-2.elasticbeanstalk.com/)

I worked on this project because I wanted to learn how to implement common functionalities of a lot of e-commerce websites, further improve my skills with technologies listed below and practice making a web application from start to finish.

Feel free to register to the [site](http://bookstore-eimantas.eu-west-2.elasticbeanstalk.com/) and test the functionality yourself, any feedback is welcome.

## Technologies used
* Spring Boot 
* Maven
* Hibernate
* MySQL
* Thymeleaf
* CSS
* Bootstrap 4
* Thymeleaf
* AWS

## Deployment
* The app is deployed on AWS using Elastic Beanstalk.
* I'm using Amazon RDS (Relational Database Services) for the database.
* The book images are being stored in Amazon S3 (Simple Storage Space).

## Project Functionality


### Accessible to everyone:
* **Homepage**  
    * Books are shown from the most recent addition to the oldest and are displayed using bootstrap cards.
    * Pagination is implemented showing 20 books on each page.
    
* **Categories**
    * On this page, people can view books by their genre.
    * People can also find books by using the search bar at the top of the page.
    * Pagination is turned on if the category has enough books in it to warrant extra pages.
    
* **Sign In / Registration**
    * People can make an account on the Sign Up page by entering a username, an e-mail address and a password.
    * Once registered, an account is created and they can immediately login from the Sign In page.
    
* **Forgotten Password**
    * If a user has an account, but they forgot their password, they can retrieve it in this page.
    * An e-mail has to be entered with which the user signed up, if there's a user with this e-mail address, then an email is sent to them.
    * The e-mail contains a link to the website with a token attached in the URL. 
    * The token is usable for 24 hours. Once at the webpage, if the token is correct, a special privilege is granted to change the password.
    * Once a new password is entered and confirmed, the user can log in.
    
### Accessible to users:
* **Shopping Cart**
    * While browsing categories or individual book information pages, users start to see an "Add to basket" icon, which adds the book to their shopping cart.
    * Order information is immediately visible with the total price for the order including taxes and shipping.
    
* **Wish List**
    * While browsing categories or individual book information pages, users start to see an "Add to Wish List" icon, which adds the book to their Wish List so that the users can buy them later.
    * The books can be moved from the Wish List to basket (they are then removed from the Wish List).
    
* **Checkout**
    * If the user wants to complete the order, they can move on to the checkout page from their Shopping Cart.
    * In it, they have to choose their address, their payment method and are asked to review the order information before making a purchase.
        * If they haven't added an address and payment methods, they can do so by following the "Add a new x" link which will point them to the account page.
    * Once they confirm the order, they can see a page with the order information and are sent an e-mail with the same information.
    
* **Account** (this page consists of 4 tabs)
    1. Orders - users can view their order information from the most recent to the oldest order.
    2. Login & Security - users can change their password.
    3. Payment methods  - users can add a payment method, view their added credit cards and choose the default card they would like to use.
    4. Delivery address - users can add a delivery address, view their addresses and choose the default address where the delivery would be sent.

### Accessible to admins:
* An admin has access to two pages:
    * Add book page, from which they can add new books to the bookstore.
    * View all books page, from which they can:
        * View all the books to see their information.
        * Delete them.
        * Edit them.
        

## Extras
[DataTables](https://datatables.net), [tiny](https://www.tiny.cloud/), [FontAwesome](https://fontawesome.com/start), [Google Fonts](https://fonts.google.com/).

## Contact
If you have any questions, feel free to contact me on my [website](https://eimantaslilia.herokuapp.com/), or send an e-mail to eimantas.lilia@gmail.com.
