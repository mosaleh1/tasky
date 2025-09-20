# Tasky - Task Management REST API

A Spring Boot REST API for user authentication and task management system.

## Features

- User registration and authentication with JWT
- Task management (CRUD operations)
- Secure endpoints with token-based authentication
- Password hashing with BCrypt
- H2 in-memory database
- Input validation and error handling
- Unit tests

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security 6**
- **Spring Data JPA**
- **JWT (JSON Web Tokens)**
- **H2 Database**
- **Maven**
- **JUnit 5** for testing

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation & Running

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd tasky
   ```

2. **Build the project:**
   ```bash
   ./mvnw clean compile
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application:**
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: `password`

## API Endpoints

### Authentication Endpoints

#### 1. User Registration
- **POST** `/auth/register`
- **Request Body:**
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Success Response (200):**
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "name": "John Doe",
    "email": "john@example.com"
  }
  ```

#### 2. User Login
- **POST** `/auth/login`
- **Request Body:**
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Success Response (200):**
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "name": "John Doe",
    "email": "john@example.com"
  }
  ```

#### 3. User Logout
- **POST** `/auth/logout`
- **Headers:** `Authorization: Bearer <token>`
- **Success Response (200):**
  ```json
  {
    "message": "Logged out successfully"
  }
  ```

### Task Management Endpoints

**Note:** All task endpoints require authentication. Include the JWT token in the Authorization header.

#### 4. Create Task
- **POST** `/tasks`
- **Headers:** `Authorization: Bearer <token>`
- **Request Body:**
  ```json
  {
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation with examples",
    "status": "OPEN"
  }
  ```
- **Success Response (201):**
  ```json
  {
    "id": 1,
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation with examples",
    "status": "OPEN"
  }
  ```

#### 5. Get All Tasks
- **GET** `/tasks`
- **Headers:** `Authorization: Bearer <token>`
- **Success Response (200):**
  ```json
  [
    {
      "id": 1,
      "title": "Complete API documentation",
      "description": "Write comprehensive API documentation with examples",
      "status": "OPEN"
    },
    {
      "id": 2,
      "title": "Fix login bug",
      "description": "Fix the authentication issue in login endpoint",
      "status": "IN_PROGRESS"
    }
  ]
  ```

#### 6. Update Task
- **PUT** `/tasks/{id}`
- **Headers:** `Authorization: Bearer <token>`
- **Request Body:**
  ```json
  {
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation with examples",
    "status": "DONE"
  }
  ```
- **Success Response (200):**
  ```json
  {
    "id": 1,
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation with examples",
    "status": "DONE"
  }
  ```

#### 7. Delete Task
- **DELETE** `/tasks/{id}`
- **Headers:** `Authorization: Bearer <token>`
- **Success Response (200):**
  ```json
  {
    "message": "Task deleted successfully"
  }
  ```

## Task Status Values

- `OPEN` - Task is created but not started
- `IN_PROGRESS` - Task is currently being worked on
- `DONE` - Task is completed

## Error Responses

### Validation Errors (400)
```json
{
  "email": "Email should be valid",
  "password": "Password must be at least 6 characters"
}
```

### Authentication Errors (400)
```json
{
  "error": "Bad Request",
  "message": "Invalid email or password",
  "status": 400
}
```

### Unauthorized Access (401)
When accessing protected endpoints without a valid token, you'll receive a 401 Unauthorized response.

### Forbidden Access (403)
When trying to access another user's tasks, you'll receive a 403 Forbidden response.

## Example cURL Commands

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Create a task
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "title": "Complete project",
    "description": "Finish the task management API",
    "status": "OPEN"
  }'
```

### 4. Get all tasks
```bash
curl -X GET http://localhost:8080/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 5. Update a task
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "title": "Complete project",
    "description": "Finish the task management API",
    "status": "DONE"
  }'
```

### 6. Delete a task
```bash
curl -X DELETE http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Testing

Run the unit tests:
```bash
./mvnw test
```

## Project Structure

```
src/
├── main/
│   ├── java/ar/meetus/tasky/
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Global exception handling
│   │   ├── model/               # JPA entities
│   │   ├── repository/          # JPA repositories
│   │   ├── security/            # Security configuration & JWT
│   │   ├── service/             # Business logic services
│   │   └── TaskyApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/ar/meetus/tasky/
        └── TaskyApplicationTests.java
```

## Configuration

Key application properties in `application.properties`:

```properties
# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
```

## Security Features

- JWT-based authentication
- Password hashing with BCrypt
- Protected endpoints requiring valid tokens
- User isolation (users can only access their own tasks)
- CORS configuration for frontend integration

## Future Enhancements

- JWT refresh token mechanism
- User roles and permissions
- Task categories and tags
- Task due dates and reminders
- File attachments for tasks
- PostgreSQL/MySQL database support