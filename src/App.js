// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ChatWindow from './components/ChatWindow';
import PaymentPage from './components/PaymentPage';

function App() {
  return (
    
    /*<div style={styles.app}>
      <ChatWindow />
    </div>*/
    <Router>
      <Routes>
        <Route path="/" element={<ChatWindow />} />
        <Route path="/subscribe" element={<PaymentPage />} />
      </Routes>
    </Router>
  ); /* It seems that when I uncomment the div at the start and get rid of the entire Router block it works fine... */
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
