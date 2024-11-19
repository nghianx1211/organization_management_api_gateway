package com.example.apigateway.controller;

import com.example.apigateway.model.Employee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("http://3.237.63.137/")
public class RequestForwarderController {

    private final RestTemplate restTemplate;

    private String backendBaseUrl = "http://12.0.3.65:8080";

    public RequestForwarderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/employee")
    public Employee saveEmployee(@RequestBody Employee employee) {
        // Create an HttpEntity with the employee to send in the request body
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        // Use RestTemplate to make the POST request and return the saved Employee
        ResponseEntity<Employee> response = restTemplate.exchange(
                backendBaseUrl + "/employee",  // Assuming backend path for saving employee
                HttpMethod.POST,
                request,
                Employee.class  // The response type
        );

        // Return the saved employee from the response body
        return response.getBody();
    }

    /** Here, we are getting all employee*/
    @GetMapping("/employee")
    public List<Employee> getAllEmployee() {
        // Use the correct type reference for a List<Employee>
        ResponseEntity<List<Employee>> response = restTemplate.exchange(
                backendBaseUrl + "/employee", // Your backend URL
                HttpMethod.GET,
                null, // No request body for GET
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    /**here, we are geting one empployee*/
    /** Here, we are getting one employee */
    @GetMapping("/employee/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        // Make the GET request to the backend service with the given employee ID
        String backendUrl = backendBaseUrl + "/employee/" + id;  // Assuming backend URL for getting employee by ID

        // Use RestTemplate to make the GET request
        ResponseEntity<Employee> response = restTemplate.exchange(
                backendUrl,
                HttpMethod.GET,
                null,  // No request body for GET request
                Employee.class  // The response type is Employee
        );

        // Return the employee from the response body
        return response.getBody();
    }

    /** Here, we are updating an employee */
    @PutMapping("/employee/{id}")
    public Employee updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        // Construct the URL to forward the PUT request to the backend
        String backendUrl = backendBaseUrl + "/employee/" + id;

        // Create the HttpEntity containing the Employee object to be updated
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        // Send the PUT request and receive the response
        ResponseEntity<Employee> response = restTemplate.exchange(
                backendUrl,
                HttpMethod.PUT,
                request,  // Pass the Employee object in the request body
                Employee.class  // Expect an Employee response
        );

        // Return the updated employee from the response body
        return response.getBody();
    }

    /** Here, we are deleting an employee */
    @DeleteMapping("/employee/{id}")
    public void deleteEmployee(@PathVariable int id) {
        // Construct the URL to forward the DELETE request to the backend
        String backendUrl = backendBaseUrl + "/employee/" + id;

        // Create the HttpEntity (no body needed for DELETE request)
        HttpEntity<Void> request = new HttpEntity<>(null);

        // Send the DELETE request
        restTemplate.exchange(
                backendUrl,
                HttpMethod.DELETE,
                request,  // No body for DELETE request
                Void.class  // No response body expected
        );
    }

    @PostMapping("files")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Handle file processing (e.g., save to server or cloud storage)
            String filename = file.getOriginalFilename();
            System.out.println("Uploaded file: " + filename);

            // Prepare to forward the file to another backend service
            String backendUrl = backendBaseUrl + "/files";  // Adjust this URL as needed

            // Create headers for the request (optional)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create a ByteArrayResource to wrap the file's content
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return filename; // Provide the original filename
                }
            };

            // Create a multi-part body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            // Create the HTTP entity containing the file and headers
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Forward the request to the backend using RestTemplate
            ResponseEntity<String> response = restTemplate.exchange(
                    backendUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Return the response from the backend
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            // Handle any errors during file upload or forwarding
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }
}
