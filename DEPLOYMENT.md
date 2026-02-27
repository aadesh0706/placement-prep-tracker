# 🚀 Deployment Guide

## Frontend → Vercel (React)

1. Go to [vercel.com](https://vercel.com) → Sign in with GitHub
2. Click "Add New..." → Project
3. Select your repo `testerhundread/placement-prep-tracker`
4. Settings:
   - Framework Preset: **Vite**
   - Build Command: `npm run build`
   - Output Directory: `dist`
   - Install Command: `npm install`
5. Add Environment Variable:
   - `VITE_API_URL` = your-backend-url (after deploying backend)
6. Deploy! ✅

## Backend → Render.com (Java Spring Boot)

### Option 1: Auto-Deploy from GitHub

1. Go to [render.com](https://render.com) → Sign in with GitHub
2. Click "New" → Web Service
3. Connect your GitHub repo
4. Settings:
   - Name: `placement-prep-backend`
   - Environment: **Java**
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/placement-prep-0.0.1-SNAPSHOT.jar`
5. Add Environment Variables:
   - `SPRING_DATA_MONGODB_URI` = your MongoDB Atlas connection string
   - `JWT_SECRET` = any random 256-bit string
6. Deploy! ✅

### Option 2: MongoDB (Database)

Use MongoDB Atlas (free tier):
1. Go to [atlas.mongodb.com](https://atlas.mongodb.com)
2. Create free cluster
3. Get connection string: `mongodb+srv://<username>:<password>@cluster.mongodb.net/placement_prep_db`

---

## 🔧 After Deploy

1. Deploy backend on Render → get URL like `https://placement-prep-backend.onrender.com`
2. Deploy frontend on Vercel
3. Update Vercel env var: `VITE_API_URL` = `https://placement-prep-backend.onrender.com/api`
4. Rebuild frontend

---

## 📝 Important Notes

- Vercel serves frontend only
- Render runs Java backend
- MongoDB Atlas for database (free)
- Both have free tiers suitable for testing
