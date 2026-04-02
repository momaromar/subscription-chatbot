import { useState } from "react";
import "./App.css";

function App() {
  const [chatBoxInput, setChatBoxInput] = useState("");
  const [response, setResponse] = useState("");

  const callApi = async () => {
    try {
      // "fetch" is javascript's version of "curl" in cmd
      // It's basically just used to call api endpoints
      const res = await fetch("http://localhost:8080/hello", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(
          {key: chatBoxInput} 
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
    } catch (err) {
      console.error(err);
      setResponse("Error calling API");
    }
  };

  return ( // All pretty straightforward stuff
    <div className="container">
      <h1>
        frontend calling backend api test
      </h1>

      <input 
        className="input"
        type="text"
        placeholder="Enter key..."
        value={chatBoxInput}
        onChange={(e) => setChatBoxInput(e.target.value)}
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