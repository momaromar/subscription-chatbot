// src/components/ChatWindow.js
import React from 'react';
import ChatHeader from './ChatHeader';
import ChatMessages from './ChatMessages';
import ChatInput from './ChatInput';

const ChatWindow = () => {
  return (
    <div style={styles.chatWindow}>
      <ChatHeader />
      <ChatMessages />
      <ChatInput />
    </div>
  );
};

const styles = {
  chatWindow: {
    width: '400px',
    height: '600px',
    border: '1px solid #ccc',
    borderRadius: '10px',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    boxShadow: '0px 0px 15px rgba(0, 0, 0, 0.2)',
  },
};

export default ChatWindow;
