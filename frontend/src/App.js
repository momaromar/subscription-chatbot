import { useState } from "react";

function App() {
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
          {key: "secret123"}
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
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1>frontend calling backend api test</h1>

      <button onClick={callApi}>
        Call Backend
      </button>

      <p>Response: {response}</p>
    </div>
  );
}

export default App;