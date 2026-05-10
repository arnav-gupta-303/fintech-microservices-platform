const BASE_URL = process.env.REACT_APP_API_URL;

export const getToken = () => localStorage.getItem('token');

export const getEmail = () => localStorage.getItem('email');

export const saveAuth = (token, email) => {
  localStorage.setItem('token', token);
  localStorage.setItem('email', email);
};

export const clearAuth = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('email');
};

export const genKey = () =>
  'pay-' + Date.now() + '-' + Math.random().toString(36).substr(2, 6);

const request = async (path, options = {}) => {
  const token = getToken();

  const headers = {
    'Content-Type': 'application/json',
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(BASE_URL + path, {
    headers,
    ...options,
  });

  const data = await res.json().catch(() => ({}));

  if (!res.ok) {
    throw new Error(data.error || 'Request failed');
  }

  return data;
};

// Auth
export const signup = (body) =>
  request('/api/auth/signup', {
    method: 'POST',
    body: JSON.stringify(body),
  });

export const login = (body) =>
  request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(body),
  });

// User
export const getProfile = () => request('/api/users/profile');

export const saveProfile = (body) =>
  request('/api/users/profile', {
    method: 'POST',
    body: JSON.stringify(body),
  });

// Wallet
export const getWallet = () => request('/api/wallet');

export const createWallet = () =>
  request('/api/wallet/create', {
    method: 'POST',
  });

export const topUp = (amount) =>
  request('/api/wallet/topup', {
    method: 'POST',
    body: JSON.stringify({ amount }),
  });

// Payments
export const sendPayment = (body) =>
  request('/api/payments/send', {
    method: 'POST',
    body: JSON.stringify(body),
  });

export const getPayments = () =>
  request('/api/payments/history');

// Ledger
export const getLedger = () =>
  request('/api/ledger/history');

// Fraud
export const fraudCheck = (body) =>
  request('/api/fraud/check', {
    method: 'POST',
    body: JSON.stringify(body),
  });