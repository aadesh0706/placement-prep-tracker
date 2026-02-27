# 🎯 AI-Powered Placement Preparation & Progress Tracker

A comprehensive, full-stack web application designed to help students prepare effectively for campus placements through personalized learning, performance tracking, and intelligent feedback.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Database-green)
![React](https://img.shields.io/badge/React-18-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Key Modules](#key-modules)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Student Module
- 🔐 **User Authentication** - Secure JWT-based login and registration
- 📚 **Study Session Management** - Track and log study activities
- 🗺️ **Personalized AI Roadmap** - AI-generated study plans based on goals
- 📊 **Topic Tracking** - Track progress in DSA, Aptitude, OS, HR, etc.
- 📝 **Mock Tests & Quizzes** - Take quizzes and track scores
- 📈 **Performance Analytics** - Detailed progress visualization
- 🎯 **Weak Area Detection** - AI-powered analysis of weak areas
- 📄 **Resume Review** - NLP-based resume evaluation
- 💬 **HR Answer Analysis** - Evaluate HR question responses
- 🎤 **AI Mock Interviews** - Simulated interview practice

### Admin / TPO Dashboard
- 👥 **Batch-level Monitoring** - Track entire batch performance
- 📊 **Student Readiness** - Monitor student preparation status
- 📑 **Analytics Reports** - Generate detailed reports

## 🛠 Tech Stack

### Backend
| Technology | Version |
|------------|---------|
| Java | 17+ |
| Spring Boot | 3.2 |
| MongoDB | 6.0+ |
| JWT | 0.12.3 |
| Lombok | Latest |

### Frontend
| Technology | Version |
|------------|---------|
| React | 18 |
| Vite | Latest |
| Tailwind CSS | Latest |
| React Router | 6 |
| Axios | Latest |
| Chart.js | Latest |

## 📁 Project Structure

```
placement-prep-tracker/
├── backend/
│   ├── src/main/java/com/placementprep/
│   │   ├── config/          # Security & App Configuration
│   │   ├── controller/     # REST API Controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # MongoDB Models
│   │   ├── repository/     # MongoDB Repositories
│   │   ├── security/       # JWT & Security
│   │   └── service/        # Business Logic
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/     # React Components
│   │   ├── context/        # React Context (Auth)
│   │   ├── pages/         # Page Components
│   │   ├── services/      # API Services
│   │   ├── hooks/         # Custom Hooks
│   │   └── App.jsx
│   └── package.json
│
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- MongoDB 6.0 or higher (local or Atlas)
- Maven 3.8+
- IntelliJ IDEA or VS Code

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/aadesh0706/placement-prep-tracker.git
   cd placement-prep-tracker
   ```

2. **Configure MongoDB**
   
   Create `backend/src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/placement_prep_db
   spring.data.mongodb.auto-index-creation=true
   
   server.port=8080
   
   # JWT Configuration
   jwt.secret=your-secret-key-min-256-bits-long
   jwt.expiration=86400000
   
   # CORS
   cors.allowed-origins=http://localhost:3000
   ```

3. **Build the backend**
   ```bash
   cd backend
   mvn clean install
   ```

4. **Run the backend**
   ```bash
   mvn spring-boot:run
   ```
   
   The backend will start at `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Create environment file**
   ```bash
   echo "VITE_API_URL=http://localhost:8080/api" > .env
   ```

4. **Run the frontend**
   ```bash
   npm run dev
   ```
   
   The frontend will start at `http://localhost:5173`

### Using Docker (Optional)

1. **Start MongoDB with Docker**
   ```bash
   docker run -d -p 27017:27017 --name mongodb mongo:6.0
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## ⚙️ Configuration

### Environment Variables

#### Backend (.env or application.properties)
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATA_MONGODB_URI` | MongoDB Connection URI | `mongodb://localhost:27017/placement_prep_db` |
| `JWT_SECRET` | JWT Signing Secret | Required |
| `JWT_EXPIRATION` | Token Expiration (ms) | 86400000 (24 hours) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS Origins | `http://localhost:3000` |

#### Frontend (.env)
| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_URL` | Backend API URL | `http://localhost:8080/api` |

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/auth/me` | Get current user |
| PUT | `/api/auth/profile` | Update profile |

### Progress
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/progress` | Get user progress |
| GET | `/api/progress/weekly` | Get weekly analytics |
| POST | `/api/progress/sessions` | Log study session |
| GET | `/api/progress/sessions` | Get all sessions |

### Quizzes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/quizzes` | Get all quizzes |
| GET | `/api/quizzes/category/{category}` | Get by category |
| POST | `/api/quizzes/{id}/start` | Start a quiz |
| POST | `/api/quizzes/attempt/{id}/submit` | Submit quiz |
| GET | `/api/quizzes/attempts` | Get user attempts |

### Roadmaps
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/roadmap/generate` | Generate AI roadmap |
| GET | `/api/roadmap` | Get user roadmap |
| PUT | `/api/roadmap/phases/{id}` | Update phase progress |

### Mock Interviews
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/interviews` | Create interview |
| GET | `/api/interviews` | Get all interviews |
| POST | `/api/interviews/{id}/start` | Start interview |
| POST | `/api/interviews/{id}/respond` | Submit response |
| POST | `/api/interviews/{id}/complete` | Complete & evaluate |

### NLP/AI
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/nlp/resume/analyze` | Analyze resume |
| GET | `/api/nlp/resume` | Get user resumes |
| POST | `/api/nlp/hr/analyze` | Analyze HR answer |
| GET | `/api/nlp/hr` | Get HR answers |

## 🔑 Key Modules

### 1. Authentication Module
- JWT-based authentication
- Role-based access (STUDENT, TPO, ADMIN)
- Password encryption with BCrypt

### 2. Quiz Module
- Multiple quiz categories (DSA, APTITUDE, OS, DBMS, HR)
- Timed quizzes with automatic submission
- Detailed results with category-wise analysis

### 3. Progress Tracking
- Study session logging
- Streak tracking
- Weekly/monthly analytics
- Weak area identification

### 4. AI Roadmap Generator
- Personalized study plans
- Company-specific preparation
- Time-based scheduling

### 5. NLP Services
- Resume parsing and evaluation
- ATS scoring
- HR answer analysis
- Feedback generation

## 📊 Database Schema

### Collections
- `users` - User profiles and progress
- `quizzes` - Quiz questions and metadata
- `quiz_attempts` - User quiz attempts and results
- `study_sessions` - Logged study sessions
- `study_roadmaps` - AI-generated roadmaps
- `mock_interviews` - Interview practice sessions
- `resumes` - Uploaded resumes and analysis
- `hr_answers` - HR question responses
- `topics` - Topic catalog

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

---

Made with ❤️ for students, by students
