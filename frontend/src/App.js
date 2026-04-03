import { useEffect, useRef, useState } from "react";
import "./App.css";

function App() {
  const [chatBoxInput, setChatBoxInput] = useState("");
  const [chatHistory, setChatHistory] = useState([]);
  const [response, setResponse] = useState("");
  const bottomOfChatRef = useRef(null);
  const [isLoading, setIsLoading] = useState(false);

  const callApi = async () => {
    try {
      if (isLoading)
        return;

      // "fetch" is javascript's version of "curl" in cmd
      // It's basically just used to call api endpoints
      const res = await fetch("http://localhost:8080/ai", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(
          {postBody: chatBoxInput} 
        )
      });

      setChatHistory([...chatHistory,  // Loading text!
        {
          request: chatBoxInput,
          response: "Loading..."
        }
      ]);
      setIsLoading(true);


      // res.text is like res.body except better!
      // It apparently waits until the response is completely done,
      // reads the entire response stream to completion, then 
      // outputs it as a string. res.body apparently spits out
      // responses in chunks as it's arriving as a "ReadableStream"
      // object instead of a string. res.text is clean!
      const text = await res.text();
      setResponse(text);

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