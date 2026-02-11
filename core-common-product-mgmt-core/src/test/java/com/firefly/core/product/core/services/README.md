# Product Management Service Tests

## Introduction

This directory contains the test suite for the service layer of the Common Platform Product Management microservice. The tests are designed to verify the correct behavior of the service implementations, ensuring that they handle both success and error scenarios appropriately.

## Test Package Structure

The test package structure mirrors the main application structure:

```
com.firefly.core.product.core.services
├── bundle.v1
│   ├── ProductBundleItemServiceImplTest.java
│   └── ProductBundleServiceImplTest.java
├── category.v1
│   ├── ProductCategoryServiceImplTest.java
│   └── ProductCategorySubtypeServiceImplTest.java
├── core.v1
│   └── ProductServiceImplTest.java
├── documentantion.v1
│   └── ProductDocumentationServiceImplTest.java
├── fee.v1
│   └── ProductFeeStructureServiceImplTest.java
├── feature.v1
│   └── ProductFeatureServiceImplTest.java
├── lifecycle.v1
│   ├── ProductLifecycleServiceImplTest.java
│   └── ProductLimitServiceImplTest.java
├── localization.v1
│   └── ProductLocalizationServiceImplTest.java
├── pricing.v1
│   ├── ProductPricingLocalizationServiceImplTest.java
│   └── ProductPricingServiceImplTest.java
├── relationship.v1
│   └── ProductRelationshipServiceImplTest.java
└── version.v1
    └── ProductVersionServiceImplTest.java
```

## Testing Approach

The test suite follows a consistent approach across all service implementations:

1. **Test Setup**: Each test class uses JUnit 5 with Mockito for mocking dependencies. The `@BeforeEach` method sets up common test data.

2. **Mocking Strategy**: Dependencies are mocked using `@Mock` annotations, and the service under test is injected with these mocks using `@InjectMocks`.

3. **Reactive Testing**: Since the services return Reactor types (Mono/Flux), tests use StepVerifier to verify the reactive streams.

4. **Test Structure**: Tests follow the Arrange-Act-Assert pattern:
   - Arrange: Set up test data and mock behavior
   - Act: Call the service method
   - Assert: Verify the result using StepVerifier and verify interactions with mocks

5. **Test Coverage**: Each service method is tested for both success and error scenarios, including:
   - Successful operations
   - Not found scenarios
   - Validation errors
   - Database errors
   - Business rule violations

6. **Verification**: Tests verify both the returned values and the interactions with dependencies using Mockito's `verify()` method.

## Service Implementations Tested

The following service implementations are tested:

1. **Core Services**
   - ProductServiceImpl

2. **Bundle Services**
   - ProductBundleServiceImpl
   - ProductBundleItemServiceImpl

3. **Category Services**
   - ProductCategoryServiceImpl
   - ProductCategorySubtypeServiceImpl

4. **Documentation Services**
   - ProductDocumentationServiceImpl

5. **Fee Services**
   - ProductFeeStructureServiceImpl

6. **Feature Services**
   - ProductFeatureServiceImpl

7. **Lifecycle Services**
   - ProductLifecycleServiceImpl
   - ProductLimitServiceImpl

8. **Localization Services**
   - ProductLocalizationServiceImpl

9. **Pricing Services**
   - ProductPricingServiceImpl
   - ProductPricingLocalizationServiceImpl

10. **Relationship Services**
    - ProductRelationshipServiceImpl

11. **Version Services**
    - ProductVersionServiceImpl

## Running the Tests

To run all the service tests:

```bash
mvn test -Dtest="com.firefly.core.product.core.services.**.*Test"
```

To run tests for a specific service:

```bash
mvn test -Dtest="com.firefly.core.product.core.services.bundle.v1.ProductBundleItemServiceImplTest"
```

## Guidelines for Maintaining and Extending Tests

When adding new tests or modifying existing ones, follow these guidelines:

1. **Naming Convention**: Test methods should be named according to the pattern `methodName_scenario_expectedResult()`.

2. **Test Independence**: Each test should be independent and not rely on the state from other tests.

3. **Mock Verification**: Verify all interactions with mocks to ensure the service is using its dependencies correctly.

4. **Error Handling**: Test both success and error scenarios, including edge cases.

5. **Spies vs Mocks**: Use spies when you need to verify method calls on objects that are created by the service. Use mocks for dependencies that are injected into the service.

6. **Test Data**: Keep test data simple and focused on the specific test case.

7. **Comments**: Add comments to explain complex test setups or assertions.

## Dependencies

The test suite relies on the following dependencies:

- JUnit 5 (Jupiter) for test execution
- Mockito for mocking dependencies
- Reactor Test for testing reactive streams
- Spring Test for Spring-specific testing utilities

These dependencies are managed through Maven and should be available when running the tests.