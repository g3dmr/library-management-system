package com.librarymanagement;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;
import com.librarymanagement.model.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibrarymanagementApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private URL base;
	private static String baseUrl;
	private String authToken;

	HttpEntity<String> httpEntity;
	HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	public void setUp() throws Exception {
		baseUrl = "http://localhost:" + port  + "/";
		this.base = new URL(baseUrl);

		HttpHeaders loginHeaders = new HttpHeaders();
		HttpEntity<LoginRequest> loginHttpEntity = new HttpEntity<>(loginData(), loginHeaders);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl+"auth/login",loginHttpEntity, String.class);
		authToken = loginResponse.getBody();
		headers.setBearerAuth(authToken);
		httpEntity = new HttpEntity<>(headers);
	}

	private LoginRequest loginData() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserName("librarytestuser");
		loginRequest.setPassword("lib123rary");
		return loginRequest;
	}

	@Test
	public void testRequestWithAuthentication() {
		String url = "http://localhost:" + port + "/auth/login";
		HttpHeaders loginHeaders = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginRequest> loginHttpEntity = new HttpEntity<>(loginData(), loginHeaders);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity(url,loginHttpEntity, String.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testGetBook() {
		String url = baseUrl + "api/v1/books/find/isbn/AA111";
		ResponseEntity<Book> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Book.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getAvailableCopies()).isEqualTo(15);
	}

	@Test
	public void testGetBookNotFound() {
		String url = baseUrl + "api/v1/books/find/isbn/AA11112";
		ResponseEntity<Book> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Book.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testGetBookByAuthor() {
		String url = baseUrl + "api/v1/books/find/author/Season";
		ResponseEntity<BooksList> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, BooksList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getBookList().size()).isEqualTo(1);
	}

	@Test
	public void testGetBookByAuthorNotFound() {
		String url = baseUrl + "api/v1/books/find/author/Templerun";
		ResponseEntity<BooksList> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, BooksList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testAddNewBook() {
		Book newBook = new Book("CC123", "New Book", "New Season", 2024,50);
		String url = baseUrl + "api/v1/books/newbook";
		HttpEntity<Book> request = new HttpEntity<>(newBook,headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url,request, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Book Added Successfully");
	}

	@Test
	public void testRemoveBook() {
		Book delBook = new Book("RR123", "Delete Book", "New Season", 2024,50);
		String addUrl = baseUrl + "api/v1/books/newbook";
		restTemplate.postForEntity(addUrl,new HttpEntity<>(delBook, headers), String.class);

		String delUrl = baseUrl + "api/v1/books/delete/RR123";
		restTemplate.exchange(delUrl,HttpMethod.DELETE, httpEntity, String.class);

		String url = baseUrl + "api/v1/books/find/isbn/RR123";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testBorrowBook() {
		Book newBook1 = new Book("BB123", "Borrow Book", "New Season", 2024,1);
		String addUrl = baseUrl + "api/v1/books/newbook";
		restTemplate.postForEntity(addUrl,new HttpEntity<>(newBook1, headers), String.class);

		String borrowUrl = baseUrl + "api/v1/books/borrow/BB123";
		ResponseEntity<String> borrowResponse = restTemplate.exchange(borrowUrl, HttpMethod.PUT, httpEntity,String.class);

		assertThat(borrowResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(borrowResponse.getBody()).isEqualTo("Book borrowed successfully");

		ResponseEntity<String> retryResponse = restTemplate.postForEntity(borrowUrl, new HttpEntity<>(null, headers), String.class);
		assertThat(borrowResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testReturnBook() {
		Book newBook1 = new Book("RR345", "Borrow Book", "New Season", 2024,1);
		String addUrl = baseUrl + "api/v1/books/newbook";
		restTemplate.postForEntity(addUrl,new HttpEntity<>(newBook1, headers), String.class);

		String returnUrl = baseUrl + "api/v1/books/return/RR345";
		ResponseEntity<String> borrowResponse = restTemplate.exchange(returnUrl, HttpMethod.PUT, httpEntity, String.class);

		assertThat(borrowResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(borrowResponse.getBody()).isEqualTo("Book returned successfully");

		String url = baseUrl + "api/v1/books/find/isbn/RR345";
		ResponseEntity<Book> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Book.class);
		assertThat(borrowResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getAvailableCopies()).isEqualTo(2);
	}

	@Test
	public void testRateLimitRequestValidation() {
		String url = baseUrl + "api/v1/books/find/isbn/AA111";
		ResponseEntity<Book> response = null;
		for(int i=0; i <= 60; i++) {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Book.class);
			if((response.getStatusCode()).equals(HttpStatus.TOO_MANY_REQUESTS)) {
				break;
			}
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody().getAvailableCopies()).isEqualTo(15);
		}
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

	}

	@Test
	public void testCacheClear() {
		String url = "http://localhost:" + port + "/api/v1/books/clearcache";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
