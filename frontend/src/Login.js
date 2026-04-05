import { useState } from "react";

function Login({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      const res = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          username,
          password
        })
      });

      if (res.ok) {
        const data = await res.json();
        localStorage.setItem("token", data.token);
        if (data.username) {
          localStorage.setItem("username", data.username);
        }
        console.log("logged in");
        onLogin(); // Call that onLogin function we were graciously given by App.js
      } else {
        const text = await res.text();
        alert("Login failed: " + text);
      }

    } catch (err) {
      console.error(err);
      alert("Error logging in");
    }
  };

  return (
    <div className="container">
      <h2>login to frontend</h2>

      <input
        className="input"
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        className="input"
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button className="button" onClick={handleLogin}>
        Login
      </button>
    </div>
  );
}

export default Login;