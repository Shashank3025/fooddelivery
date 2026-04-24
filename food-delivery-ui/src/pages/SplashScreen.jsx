import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import logo from "../assets/logo.png";
import "./SplashScreen.css";

function SplashScreen() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/login");
    }, 3500);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="splash-screen">
      <img src={logo} alt="Rushank Logo" className="splash-logo" />
      <p className="splash-tagline">Food. Care. Delivered.</p>
    </div>
  );
}

export default SplashScreen;