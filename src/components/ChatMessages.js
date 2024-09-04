// src/components/ChatMessages.js
import React from 'react';

const ChatMessages = () => {
  return (
    <div style={styles.chatMessages}>
      {/* Messages will be dynamically added here */}
    </div>
  );
};

const styles = {
  chatMessages: {
    flex: 1,
    padding: '10px',
    overflowY: 'auto',
    display: 'flex',
    flexDirection: 'column',
  },
};

export default ChatMessages;
