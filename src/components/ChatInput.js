// src/components/ChatInput.js
import React, { useState } from 'react';

const ChatInput = () => {
  const [input, setInput] = useState('');

  const handleSend = () => {
    if (input.trim()) {
      // Trigger the chatbot's message sending function
      console.log(input); // Replace with actual send function
      setInput('');
    }
  };

  return (
    <div style={styles.chatInput}>
      <input
        type="text"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        onKeyPress={(e) => e.key === 'Enter' && handleSend()}
        placeholder="Type a message..."
        style={styles.input}
      />
      <button onClick={handleSend} style={styles.button}>Send</button>
    </div>
  );
};

const styles = {
  chatInput: {
    display: 'flex',
    padding: '10px',
    borderTop: '1px solid #ccc',
  },
  input: {
    flex: 1,
    padding: '10px',
    border: '1px solid #ccc',
    borderRadius: '5px',
    marginRight: '10px',
  },
  button: {
    padding: '10px 20px',
    backgroundColor: '#007bff',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
  },
};

export default ChatInput;
