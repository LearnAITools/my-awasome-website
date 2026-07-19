# BookMyShow Build & Development Guide

## Build System: Maven

The project has been migrated from **Gradle** to **Maven 3.8.0+** for the following reasons:
1. Better Spring Boot integration with Maven plugins
2. Easier dependency management
3. Superior plugin ecosystem for Java projects
4. Consistent with enterprise Java standards

---

## Development Environment Setup

### Required Tools
- **Java 17+** (Tested with Java 25.0.3)
- **Maven 3.8.0+**
- **Node.js v18+**
- **IntelliJ IDEA 2023+** with Lombok Plugin

### IDE Configuration (IntelliJ)

Since Lombok doesn't fully support Java 25 at compile-time, development relies on IntelliJ's built-in Lombok support:

**Step 1: Install Lombok Plugin**
```
IntelliJ IDEA → Preferences → Plugins → Search "Lombok" → Install
```

**Step 2: Enable Annotation Processing**
```
IntelliJ IDEA → Preferences → Build, Execution, Deployment → Compiler → Annotation Processors
→ Check "Enable annotation processing"
→ Check "Obtain processors from project classpath"
```

**Step 3: Rebuild Project**
```
Build → Rebuild Project
```

Now IntelliJ will automatically generate getters, setters, logging, and constructors from Lombok annotations during development.

---

## Build Commands

### Clean Compile
```bash
cd backend
mvn clean compile
```

### Run Tests
```bash
cd backend
mvn test
```

### Run Tests with Coverage (JaCoCo)
```bash
cd backend
mvn clean test jacoco:report
# Coverage report: target/site/jacoco/index.html
```

### Build JAR (Production)
```bash
cd backend
mvn clean package -DskipTests
# Output: target/bookmyshow-1.0.0.jar
```

### Start Application (Development)
```bash
cd backend
mvn spring-boot:run
```

### SonarQube Analysis
```bash
cd backend
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=BookMyShow \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=YOUR_SONAR_TOKEN
```

---

## Maven POM Structure

### Key Configuration in `pom.xml`

**Target Java Version:**
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

**Core Dependencies:**
- `spring-boot-starter-web` - REST API
- `spring-boot-starter-data-jpa` - Database ORM
- `spring-boot-starter-security` - Authentication/Authorization
- `spring-data-jpa-hibernate` - ORM Implementation
- `jjwt` - JWT Token generation/validation
- `razorpay-java` - Payment Gateway SDK
- `lombok` - Code generation (getters, setters, logging)

**Testing Dependencies:**
- `spring-boot-starter-test` - JUnit 5 + Mockito
- `spring-security-test` - Security testing utilities
- `jacoco-maven-plugin` - Code coverage

**Build Plugins:**
- `spring-boot-maven-plugin` - Package as executable JAR
- `maven-compiler-plugin` - Compile Java code (Java 17 target)
- `maven-surefire-plugin` - Run tests
- `jacoco-maven-plugin` - Generate code coverage reports
- `sonar-maven-plugin` - SonarQube integration

---

## Lombok & Java 25 Compatibility Note

### Issue
Lombok annotation processor (version 1.18.32+) has known incompatibilities with Java 25's internal compiler APIs. Specifically, the `com.sun.tools.javac.code.TypeTag` enum is missing in Java 25.

### Solution
1. **Development**: Use IntelliJ IDE's built-in Lombok support
   - IDE generates code from annotations automatically
   - No Maven compiler processing needed
   - Full Lombok features available

2. **Production Build**: Target Java 17
   - Maven compiles targeting Java 17 (stable LTS)
   - Can run on Java 25 runtime (backward compatible)
   - Eliminates Lombok processor incompatibility

### Maven Compiler Configuration
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.12.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <release>17</release>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```

---

## Project Structure (Maven)

```
backend/
├── pom.xml                                    # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/org/website/
│   │   │   ├── domain/                        # Domain-Driven Design
│   │   │   │   ├── booking/                   # Booking Domain
│   │   │   │   ├── payment/                   # Payment Domain
│   │   │   │   ├── movie/                     # Movie Domain
│   │   │   │   └── auth/                      # Auth Domain
│   │   │   ├── config/                        # Spring Configuration
│   │   │   ├── exception/                     # Error Handling
│   │   │   ├── constant/                      # ErrorCode Enum
│   │   │   ├── util/                          # Utility Classes
│   │   │   ├── security/                      # JWT Security
│   │   │   └── controller/                    # Legacy (moving to /domain)
│   │   └── resources/
│   │       └── application.properties         # Spring Configuration
│   └── test/
│       └── java/org/website/                  # Unit/Integration Tests
└── target/                                    # Maven build output
    ├── classes/                               # Compiled bytecode
    ├── test-classes/                          # Compiled test code
    ├── site/                                  # Generated reports (Javadoc, Coverage)
    └── bookmyshow-1.0.0.jar                  # Executable JAR (packaging phase)
```

---

## Continuous Integration

### GitHub Actions (Recommended)

Create `.github/workflows/build.yml`:
```yaml
name: Maven Build & Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: cd backend && mvn clean verify
      - uses: codecov/codecov-action@v3
        with:
          files: ./backend/target/site/jacoco/jacoco.xml
```

---

## Troubleshooting

### Maven Issues

**Problem**: `[ERROR] Cannot find symbol: class Slf4j`
```
Solution:
1. Rebuild project in IntelliJ: Build → Rebuild Project
2. Verify Lombok plugin is installed
3. Enable annotation processing in IDE settings
```

**Problem**: `Port 8080 already in use`
```bash
# macOS/Linux
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Problem**: Maven build timeout
```bash
# Increase Maven heap memory
export MAVEN_OPTS="-Xmx2g -Xms512m"
mvn clean package
```

### Compilation Issues

**Issue**: `ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`
- **Cause**: Java 25 runtime with Lombok processor
- **Solution**: Use IntelliJ IDE for development, Maven targets Java 17

**Issue**: `NoSuchMethodError: findByShowIdAndSeatNumber`
- **Cause**: Repository interface not defined
- **Solution**: Add custom query method or use derived query naming

---

## Code Quality Standards

### Target Metrics
- **Code Coverage**: 85%+ (JUnit 5 + Mockito)
- **SonarQube**: Zero critical/blocker issues
- **Javadoc**: 100% on public APIs
- **Error Codes**: ERRxxx format for all exceptions

### Running Checks

**Test Coverage**
```bash
mvn clean test jacoco:report
# View: backend/target/site/jacoco/index.html
```

**SonarQube Quality**
```bash
mvn clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000
```

**Javadoc Generation**
```bash
mvn javadoc:javadoc
# Output: target/site/apidocs/index.html
```

---

## Deployment

### Build Executable JAR
```bash
cd backend
mvn clean package -DskipTests
# Creates: target/bookmyshow-1.0.0.jar
```

### Run JAR
```bash
# Development (H2 in-memory)
java -jar target/bookmyshow-1.0.0.jar

# Production (external database)
java -jar target/bookmyshow-1.0.0.jar \
  --spring.datasource.url=jdbc:mysql://prod-db:3306/bookmyshow \
  --spring.datasource.username=dbuser \
  --spring.datasource.password=dbpass
```

### Docker Build
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
COPY backend/target/bookmyshow-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
```

```bash
docker build -t bookmyshow:latest .
docker run -p 8080:8080 bookmyshow:latest
```

---

## Frontend Build

### Development
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

### Production Build
```bash
cd frontend
npm run build
npm run preview
# Output: dist/
```

---

## Important Notes

1. **Lombok Plugin Required**: IntelliJ's Lombok plugin MUST be installed for development
2. **Java 17 Target**: Maven always compiles to Java 17 (even if running Java 25)
3. **IDE Configuration**: Annotation processing must be enabled in IDE settings
4. **Test Before Commit**: Run `mvn test` before pushing code
5. **Error Codes**: All exceptions must map to ErrorCode enum (ERRxxx format)

---

## Development Workflow

1. **Clone & Setup**
   ```bash
   git clone <repo>
   cd my-awasome-website
   ```

2. **Open in IntelliJ**
   ```
   File → Open → Select project root
   Configure Lombok plugin when prompted
   ```

3. **Backend Development**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **Frontend Development** (new terminal)
   ```bash
   cd frontend
   npm run dev
   ```

5. **Before Committing**
   ```bash
   cd backend
   mvn clean test       # Verify tests pass
   mvn checkstyle:check # Verify code style
   ```

6. **Build for Deployment**
   ```bash
   mvn clean package -DskipTests
   ```

---

## Support

For build-related issues, check:
- [Maven Documentation](https://maven.apache.org/)
- [Spring Boot Maven Plugin](https://spring.io/guides/gs/maven/)
- [Lombok Setup Guide](https://projectlombok.org/setup/overview)

**Last Updated**: May 30, 2026
