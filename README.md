# Fit - Personal Fitness Management System

A minimal, elegant personal fitness tracking and progress management web application.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.16, Spring Security, JWT, MyBatis-Plus 3.5.12, Maven
- **Frontend**: React 19, TypeScript 6, Vite 8, Tailwind CSS 4
- **Database**: MySQL 8.4 LTS

## Project Structure

```
fit/
├── fit-backend/    # Spring Boot backend
├── fit-frontend/   # React + TypeScript frontend
└── docs/           # Documentation
```

## Getting Started

### Prerequisites

- Java 17
- Maven 3.9+
- Node.js 20+
- MySQL 8.4

### Backend

```bash
cd fit-backend
# Configure MySQL in src/main/resources/application.yml
mvn spring-boot:run
```

### Frontend

```bash
cd fit-frontend
npm install
npm run dev
```

## Development Status

- Phase 0: Project Initialization