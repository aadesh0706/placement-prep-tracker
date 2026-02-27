import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Layout from './components/Layout';

function PrivateRoute({ children }) {
  const { user, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    );
  }
  
  return user ? children : <Navigate to="/login" />;
}

function PublicRoute({ children }) {
  const { user, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    );
  }
  
  return user ? <Navigate to="/dashboard" /> : children;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
      <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
      <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
        <Route index element={<Navigate to="/dashboard" />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="quizzes" element={<div className="p-6"><h1 className="text-2xl font-bold">Quizzes</h1><p className="text-gray-600 mt-2">Quiz functionality coming soon...</p></div>} />
        <Route path="interviews" element={<div className="p-6"><h1 className="text-2xl font-bold">Mock Interviews</h1><p className="text-gray-600 mt-2">Mock interview functionality coming soon...</p></div>} />
        <Route path="roadmap" element={<div className="p-6"><h1 className="text-2xl font-bold">Study Roadmap</h1><p className="text-gray-600 mt-2">Roadmap functionality coming soon...</p></div>} />
        <Route path="resume" element={<div className="p-6"><h1 className="text-2xl font-bold">Resume Analysis</h1><p className="text-gray-600 mt-2">Resume analysis coming soon...</p></div>} />
        <Route path="analytics" element={<div className="p-6"><h1 className="text-2xl font-bold">Analytics</h1><p className="text-gray-600 mt-2">Analytics coming soon...</p></div>} />
      </Route>
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
