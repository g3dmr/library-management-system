## Library Management System

## Overview
Library Management is a Spring Boot application designed to manage library operations such as adding, updating, and deleting books from library, as well as managing book operations loans and returns.

## Features
- Add, update, and delete books
- Handle book loans and returns
- Search for books by author, or ISBN
- Manage user authorization

## Technologies Used
- Java 17
- Spring Boot
- Maven
- Spring Security (for authentication and authorization)

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### Clone the repository
```
git clone git@github.com:g3dmr/library-management-system.git
cd library-management-system
```

### Build the project
```
mvn clean install
```

### Test the application
```
mvn test
```

### Run the application
```
mvn spring-boot:run
```

The application will be accessible at `http://localhost:8080`

## Endpoints

#### Login API: POST Request (/auth/login)

- Description : Login service to authenticate user
- Response: `200 OK` with authorization value
```
curl --location 'http://localhost:8080/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{
  "userName": "libraryuser",
  "password": "lib123rary"
  }'
```

#### Add Book: POST Request (/api/v1/book/newbook)
- Description: Add a new book.
- Request Body: Book details in JSON format.
- Response: `200 OK` with a success message, `400 Bad Request` if the book already exists. 

```
curl --location 'http://localhost:8080/api/v1/books/newbook' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: ••••••' \
  --data '{
  "isbn": "ASBC2367WE",
  "title": "Christmas Decor",
  "author": "SpringWals",
  "publicationYear": 1997,
  "availableCopies": 10
  }'
```

#### Find Book :  GET Request (/api/v1/books/find/isbn/{isbn})   
- Description: Retrieve book information by ISBN.
- Response: `200 OK` with book details, `404 Not Found` if the book is not in the library.

**Endpoint**
```
http://localhost:8080/api/v1/books/find/isbn/AA111
```
**Request**

```
curl --location 'http://localhost:8090/api/v1/books/isbn/12' \
--header 'Authorization: ••••••' \
```
**Response**
```
{
    "isbn": "12",
    "title": "Dream Book",
    "author": "Spring",
    "publicationYear": 1997,
    "availableCopies": 1
}
```
#### Find Book : GET Request (/api/v1/books/find/author/{authorName})
- Description: Retrieve books by author name.
- Response: `200 OK` with a list of books, `404 Not Found` if no books are found for the given author.

```
http://localhost:8080/api/v1/books/find/author/Summer
```

#### DELETE /api/v1/books/delete/{isbn}
- Description: Remove a book by ISBN.
- Response: `200 OK` with a success message.

```
http://localhost:8080/api/v1/books/delete/{isbn}
```
#### Borrow Book: /api/v1/books/borrow/{isbn}
- Description: Borrow a book by ISBN only if exists already else error will be thrown.
- Response: `200 OK` with a success message, `400 Bad Request` if no copies are available.
```
http://localhost:8080/api/v1/books/borrow/{isbn}
```
#### Return Book: (/api/v1/books/return/{isbn})
- Description: Return a borrowed book by ISBN.
- Response: `200 OK` with a success message.
```
http://localhost:8080/api/v1/books/return/{isbn}
```

#### GET /api/v1/books/clearcache
- Description: Clear the cache.
- Response: `200 OK` with a success message.
```
http://localhost:8080/api/v1/books/clearcache
```

#### Rate Limiting Fallback
- Description: Handles rate limiting when the request limit is exceeded.
- Response: `429 Too Many Requests` with a retry header.


### Notes :-
- For demonstration purpose, repository includes sample data added (ISBNs: AA111, BB222, CC333) directly within the service class.
- There is no validation for the authentication calls, authentication is implemented using only the username, bypassing validation checks.
- Find a book would return the newly added book details with in the same session if the book is already added.

### Further enhancements:-
- Containerize the application with Docker
- Enhance validation checks
- Set up CI/CD pipeline