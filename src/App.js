// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ChatWindow from './components/ChatWindow';
import PaymentPage from './components/PaymentPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<ChatWindow />} />
        <Route path="/subscribe" element={<PaymentPage />} />
      </Routes>
    </Router>
  );
}

export default App;
