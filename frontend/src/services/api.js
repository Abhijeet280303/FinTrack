import axios from 'axios';

/**
 * Pre-configured Axios instance.
 * Automatically attaches the JWT token from localStorage to every request.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// ─── Request Interceptor: attach Bearer token ─── 
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ─── Response Interceptor: handle 401 globally ───
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Redirect to login only if not already there
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;

/* ─────────────── Service Functions ─────────────── */

// Income
export const incomeApi = {
  getAll:   ()           => api.get('/income'),
  create:   (data)       => api.post('/income', data),
  update:   (id, data)   => api.put(`/income/${id}`, data),
  delete:   (id)         => api.delete(`/income/${id}`),
};

// Expenses
export const expenseApi = {
  getAll:   ()           => api.get('/expenses'),
  create:   (data)       => api.post('/expenses', data),
  update:   (id, data)   => api.put(`/expenses/${id}`, data),
  delete:   (id)         => api.delete(`/expenses/${id}`),
};

// Budget
export const budgetApi = {
  getAll:    ()          => api.get('/budget'),
  getCurrent:()          => api.get('/budget/current'),
  create:    (data)      => api.post('/budget', data),
  update:    (id, data)  => api.put(`/budget/${id}`, data),
};

// Dashboard & Analytics
export const dashboardApi = {
  get: () => api.get('/dashboard'),
};

export const analyticsApi = {
  getMonthly:  (months = 6)        => api.get(`/analytics/monthly?months=${months}`),
  getCategory: (month, year)       => api.get(`/analytics/category?month=${month}&year=${year}`),
  getTrend:    (months = 6)        => api.get(`/analytics/trend?months=${months}`),
};

// Transactions
export const transactionApi = {
  getAll: () => api.get('/transactions'),
};

// User / Profile
export const userApi = {
  getProfile:     ()          => api.get('/user/profile'),
  updateProfile:  (data)      => api.put('/user/profile', data),
  changePassword: (data)      => api.put('/user/password', data),
};
