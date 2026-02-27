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
};

export const progressAPI = {
  getProgress: () => api.get('/progress'),
  getWeeklyAnalytics: () => api.get('/progress/weekly'),
  logSession: (data) => api.post('/progress/sessions', data),
  getSessions: () => api.get('/progress/sessions'),
};

export const quizAPI = {
  getAllQuizzes: () => api.get('/quizzes'),
  getByCategory: (category) => api.get(`/quizzes/category/${category}`),
  getByDifficulty: (difficulty) => api.get(`/quizzes/difficulty/${difficulty}`),
  startQuiz: (id) => api.post(`/quizzes/${id}/start`),
  submitQuiz: (attemptId, answers) => api.post(`/quizzes/attempt/${attemptId}/submit`, answers),
  getAttempts: () => api.get('/quizzes/attempts'),
};

export const roadmapAPI = {
  generate: (data) => api.post('/roadmap/generate', data),
  getRoadmap: () => api.get('/roadmap'),
  updatePhase: (phaseId, completed) => api.put(`/roadmap/phases/${phaseId}?completed=${completed}`),
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
