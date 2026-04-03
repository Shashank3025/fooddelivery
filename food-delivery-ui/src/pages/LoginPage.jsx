import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./LoginPage.css";

function LoginPage() {
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const handleLogin = () => {
    if (!email.trim()) {
      alert("Please enter your email");
      return;
    }

    localStorage.setItem("userEmail", email);
    navigate("/restaurants");
  };

  return (
    <div className="login-page">
      <div className="dark-overlay"></div>

      <div className="floating-shape shape1"></div>
      <div className="floating-shape shape2"></div>
      <div className="floating-shape shape3"></div>

      <div className="login-card">
        <div className="brand">🍔 FoodExpress</div>
        <h2>Welcome Back</h2>
        <p className="subtitle">Hot meals. Fast delivery. Anytime.</p>

        <input
          type="email"
          placeholder="Enter email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <button onClick={handleLogin}>Continue</button>

        <p className="bottom-text">
          New here? <Link to="/register">Create account</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;