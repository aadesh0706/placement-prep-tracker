import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (data) => api.post('/auth/register', data),
  getMe: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/profile', data),
};

export const progressAPI = {
  getProgress: () => api.get('/progress'),
  getWeeklyAnalytics: () => api.get('/progress/weekly'),
  logSession: (data) => api.post('/progress/sessions', data),
  getSessions: () => api.get('/progress/sessions'),
};

export const quizAPI = {
  getAllQuizzes: () => api.get('/quizzes'),
  getQuizById: (id) => api.get(`/quizzes/${id}`),
  getByCategory: (category) => api.get(`/quizzes/category/${category}`),
  getByDifficulty: (difficulty) => api.get(`/quizzes/difficulty/${difficulty}`),
  startQuiz: (id) => api.post(`/quizzes/${id}/start`),
  submitQuiz: (attemptId, answers) => api.post(`/quizzes/attempt/${attemptId}/submit`, answers),
  getAttempts: () => api.get('/quizzes/attempts'),
};

export const roadmapAPI = {
  generate: (data) => api.post('/roadmap/generate', data),
  getRoadmap: () => api.get('/roadmap'),
  updatePhase: (roadmapId, phaseId, data) => api.put(`/roadmap/${roadmapId}/phases/${phaseId}`, data),
};

export const adminAPI = {
  getStats: () => api.get('/admin/stats'),
  getUsers: () => api.get('/admin/users'),
  updateUserRole: (id, role) => api.put(`/admin/users/${id}/role`, { role }),
  toggleUserActive: (id) => api.put(`/admin/users/${id}/toggle-active`),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  getQuizzes: () => api.get('/admin/quizzes'),
  createQuiz: (data) => api.post('/admin/quizzes', data),
  updateQuiz: (id, data) => api.put(`/admin/quizzes/${id}`, data),
  deleteQuiz: (id) => api.delete(`/admin/quizzes/${id}`),
  toggleQuizActive: (id) => api.put(`/admin/quizzes/${id}/toggle-active`),
  getLeaderboard: () => api.get('/admin/leaderboard'),
};

export const interviewAPI = {
  create: (data) => api.post('/interviews', data),
  getAll: () => api.get('/interviews'),
  getById: (id) => api.get(`/interviews/${id}`),
  start: (id) => api.post(`/interviews/${id}/start`),
  respond: (id, questionId, text) => api.post(`/interviews/${id}/respond`, { questionId, text }),
  complete: (id, evaluation) => api.post(`/interviews/${id}/complete`, evaluation),
};

export const nlpAPI = {
  analyzeResume: (fileName, text) => api.post('/nlp/resume/analyze', { fileName, text }),
  getResumes: () => api.get('/nlp/resume'),
  analyzeHRAnswer: (question, answer) => api.post('/nlp/hr/analyze', { question, answer }),
  getHRAnswers: () => api.get('/nlp/hr'),
};

export default api;
