# Call out
The module to call Bank is currently mocked, because the simulator can't be launched. 
(likely due to docker is restricted on the company macbook)

## Future improvements
1. Setup checkstyle
2. Use Lombok to simplify model definition. (already partially done)
3. Define constant, like for string "Page not found"
4. Improve the unit test coverage

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

