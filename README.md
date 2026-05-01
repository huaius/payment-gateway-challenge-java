## Call out
The module to call Bank is currently mocked, because the simulator can't be launched. (likely due to docker is restricted on the company macbook)

## Development summary
### Initial state
* Query payment endpoint (GET)
* Repository (save and get historical payment, like DB)
* Framework (controller, wiring)

### Added functionalities
* Setup code coverage check and report
* Use lombok
* Create new packages for common modules and clients (So new customers can import and onboard easily)
* Implement the make payment endpoint, request validation (POST)
* Unit tests
* Delete redundant GetPaymentResponse

### Future improvements
1. Setup checkstyle.
2. Define constant, like for string "Page not found".
3. Improve the unit test coverage to 95+% (currently 81%)
4. Retry when Bank returns `Service Unavailable`.
5. cardNumberLastFour is now defined as `int`, not user-friendly if the last 4 digits start with 0 (such as 0012).
6. Add a `Reject reason` attribute in the response, so that requester knows the reason.

## Requirements
- JDK 17
- Docker (Not needed since hardware limitation)

## Requirement analysis and Design

Refer to following diagram
![image](https://github.com/huaius/payment-gateway-challenge-java/blob/main/src/main/resources/payment.png?raw=true)



## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

