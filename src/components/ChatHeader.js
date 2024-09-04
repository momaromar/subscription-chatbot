// src/components/ChatHeader.js
import React from 'react';

const ChatHeader = () => {
  return (
    <div style={styles.chatHeader}>
      <h2>Chatbot</h2>
    </div>
  );
};

const styles = {
  chatHeader: {
    padding: '10px',
    borderBottom: '1px solid #ccc',
    backgroundColor: '#007bff',
    color: '#fff',
    textAlign: 'center',
    borderTopLeftRadius: '10px',
    borderTopRightRadius: '10px',
  },
};

export default ChatHeader;
