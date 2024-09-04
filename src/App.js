// src/App.js
import React from 'react';
import ChatWindow from './components/ChatWindow';

function App() {
  return (
    <div style={styles.app}>
      <ChatWindow />
    </div>
  );
}

const styles = {
  app: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh',
    backgroundColor: '#f5f5f5',
  },
};

export default App;
