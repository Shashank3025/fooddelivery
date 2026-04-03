import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/api";
import "./RegisterPage.css";

function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      const res = await api.post("/api/users", {
        name,
        email,
        phone,
      });

      if (res.data?.id) {
        localStorage.setItem("userId", res.data.id);
      }

      if (res.data?.name) {
        localStorage.setItem("userName", res.data.name);
      }

      if (res.data?.email) {
        localStorage.setItem("userEmail", res.data.email);
      }

      alert("Registration successful");
      navigate("/restaurants");
    } catch (err) {
      console.error("Registration error:", err);
      alert("Registration failed");
    }
  };

  return (
    <div className="register-page">
      <div className="dark-overlay"></div>

      <div className="floating-shape shape1"></div>
      <div className="floating-shape shape2"></div>
      <div className="floating-shape shape3"></div>

      <div className="register-card">
        <div className="brand">🍔 FoodExpress</div>
        <h2>Create Account</h2>
        <p className="subtitle">
          Join now and get your favorite food delivered fast.
        </p>

        <input
          type="text"
          placeholder="Enter full name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <input
          type="email"
          placeholder="Enter email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          type="text"
          placeholder="Enter phone number"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
        />

        <button onClick={handleRegister}>Create Account</button>

        <p className="bottom-text">
          Already have an account? <Link to="/">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;