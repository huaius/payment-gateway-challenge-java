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
* Implement the make payment endpoint (POST)
* Delete redundant GetPaymentResponse

### Future improvements
1. Setup checkstyle
2. Define constant, like for string "Page not found"
3. Improve the unit test coverage
4. cardNumberLastFour is now defined as `int`, not user-friendly if the last 4 digits start with 0 (such as 0012)

## Requirements
- JDK 17
- Docker (Not needed since hardware limitation)

## High level Design






## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

