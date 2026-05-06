/*import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // API Gateway
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;*/
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.DEV
    ? "http://localhost:8080/api"
    : "/api",
});

export default api;