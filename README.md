# [Bookstore - a personal e-commerce project](http://bookstore-eimantas.eu-west-2.elasticbeanstalk.com/)

Feel free to register to the [site](http://bookstore-eimantas.eu-west-2.elasticbeanstalk.com/) and test the functionality such as:
* Registration
* Search by category, title or author
* Shopping cart
* Wish list
* Forgotten password retrieval
* Setting up your addresses and payments
* Ordering

## Technologies used
* **Backend**: Spring Boot, Maven, Hibernate, MySQL
* **Frontend**: HTML, CSS, Thymeleaf, Bootstrap
* **Other**: AWS, JUnit 5, Mockito, Git

![alt text](https://i.imgur.com/HmxH5hz.jpg)

## Deployment
* AWS Elastic Beanstalk is being used to deploy the application.
* Amazon RDS (Relational Database Service) is being used for the database.
* Amazon S3 (Simple Storage Space) is being used to save book covers.

## Additional Info
 
* **There are 3 different access levels in this web application:**  
    * **Anonymous visitors** can browse the main page and categories page, search, as well as register or retrieve a password.
    * **Users** can manage their payments, addresses, use the shopping cart and wish list, use the checkout, change their password and view their past orders.
     * **Admins** can add, edit, delete and view books.
     
 *  **Unit Tests**
      * Tests are currently written only for the controller layer mainly using JUnit 5, Mockito and Spring Test.

## Extras
[DataTables](https://datatables.net), [tiny](https://www.tiny.cloud/), [FontAwesome](https://fontawesome.com/start), [Google Fonts](https://fonts.google.com/).

## Goals
   * I worked on this project because I wanted to learn how to develop a web application from start to finish. I chose to create an e-commerce website so that I would learn how to implement functionalities common to such websites and further improve my skills with technologies listed.
   * After finishing the development and deploying the project, I thought a logical next step is to learn unit testing, so I added unit tests for the controller layer first.
