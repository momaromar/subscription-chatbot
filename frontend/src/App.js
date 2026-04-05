import { useCallback, useEffect, useRef, useState } from "react";
import Login from "./Login";
import PaymentGate from "./PaymentGate";
import "./App.css";

function App() {
  const [chatBoxInput, setChatBoxInput] = useState("");
  const [chatHistory, setChatHistory] = useState([]);
  const [response, setResponse] = useState("");
  const bottomOfChatRef = useRef(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem("token"));

  // --- Stripe paywall state (isolated from original chat logic) ---
  const [hasPaid, setHasPaid] = useState(false);
  const [accessLoading, setAccessLoading] = useState(false);
  const [displayUsername, setDisplayUsername] = useState("");

  const refreshPaidAccess = useCallback(async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setHasPaid(false);
      return;
    }
    setAccessLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/stripepay/access-status", {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) {
        setHasPaid(false);
        return;
      }
      const data = await res.json();
      setHasPaid(!!data.paid);
      if (data.username) {
        setDisplayUsername(data.username);
      }
    } catch (e) {
      console.error(e);
      setHasPaid(false);
    } finally {
      setAccessLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!isLoggedIn) {
      return;
    }
    setDisplayUsername(localStorage.getItem("username") || "");
    refreshPaidAccess();
  }, [isLoggedIn, refreshPaidAccess]);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.get("payment") !== "success") {
      return;
    }
    const sessionId = params.get("session_id");
    const token = localStorage.getItem("token");
    if (!sessionId || !token) {
      window.history.replaceState({}, "", window.location.pathname);
      return;
    }
    (async () => {
      try {
        await fetch("http://localhost:8080/api/stripepay/verify-session", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ sessionId }),
        });
      } catch (e) {
        console.error(e);
      } finally {
        window.history.replaceState({}, "", window.location.pathname);
        refreshPaidAccess();
      }
    })();
  }, [refreshPaidAccess]);

  const callApi = async () => {
    try {
      const authToken = localStorage.getItem("token");
      if (isLoading)
        return;

      setChatHistory([...chatHistory,  // Loading text!
        {
          request: chatBoxInput,
          response: "Loading..."
        }
      ]);
      setIsLoading(true);

      // "fetch" is javascript's version of "curl" in cmd
      // It's basically just used to call api endpoints
      const res = await fetch("http://localhost:8080/ai", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${authToken}`
        },
        body: JSON.stringify(
          {postBody: chatBoxInput} 
        )
      });


      // res.text is like res.body except better!
      // It apparently waits until the response is completely done,
      // reads the entire response stream to completion, then 
      // outputs it as a string. res.body apparently spits out
      // responses in chunks as it's arriving as a "ReadableStream"
      // object instead of a string. res.text is clean!
      const text = await res.text();
      setResponse(text);

      if (res.status === 402) {
        setHasPaid(false);
      }

      setChatHistory([...chatHistory, 
        {
          request: chatBoxInput,
          response: text
        }
      ]);

    } catch (err) {
      console.error(err);
      setResponse("Error calling API");
    } finally  {
      setChatBoxInput("");
      setIsLoading(false);
    }
  };

  // Handy tool that makes the bottom of the chat auto-scroll into view whenever chatHistory updates!
  // Learned how to do this through this useful youtube vid: "https://youtu.be/yaIytT_Y0DA?si=nqppce3Xizo6GUUL"
  useEffect(() => { 
    if (bottomOfChatRef.current) {
      bottomOfChatRef.current.scrollIntoView();
    }
  }, [chatHistory]);

  if (isLoggedIn === false) {
    // Basically returns the Login page,
    return (
      <Login
        onLogin={() => setIsLoggedIn(true)} // ALSO gives it a function (onLogin) to work with if needed
      />
    );
  }

  if (accessLoading) {
    return (
      <div className="container">
        <p>Checking subscription access…</p>
      </div>
    );
  }

  if (!hasPaid) {
    return (
      <PaymentGate
        displayUsername={displayUsername}
      />
    );
  }

  return ( // All pretty straightforward stuff
    <div className="container">
      <h1>
        frontend calling backend api test
      </h1>

      <div className="history-box">
        <h3>Chat</h3>
        <div className="history-content">
          {chatHistory.map((item) => (   // map is basically a for loop. Meaning, for each var item in chatHistory, add a <div> containing item
            <div className="history-item">
              <div className="history-request"> {item.request} </div>
              <div className="history-response"> {item.response} </div>
            </div>
          ))}
          <div ref={bottomOfChatRef}></div>
        </div>
      </div>


      <input 
        className="input"
        type="text"
        placeholder="Enter key..."
        value={chatBoxInput}
        onChange={(e) => setChatBoxInput(e.target.value)}
        onKeyDown={(e) => { if (e.key === "Enter") callApi(); }}
      />

      <button 
        className="button"
        onClick={callApi}>
        Call Backend
      </button>

      <p>
        Response: {response}
      </p>
    </div>
  );
}

export default App;