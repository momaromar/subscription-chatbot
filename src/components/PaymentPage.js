// src/components/PaymentPage.js
import React, { useState } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, useStripe, useElements, CardElement } from '@stripe/react-stripe-js';
import axios from 'axios';

const stripePromise = loadStripe('pk_live_51Pv3PWFIKAACs2oXZzDgh0DeO9Gb0MFt5W4gTG8HXzrl06P0HJR3UKjnHBlaibHqfQIRrg0kbIMBgmD7Cai1oCig000ljx3nvu');

const PaymentPage = () => {
  return (
    <Elements stripe={stripePromise}>
      <CheckoutForm />
    </Elements>
  );
};

const CheckoutForm = () => {
  const stripe = useStripe();
  const elements = useElements();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const cardElement = elements.getElement(CardElement);

    try {
      // Create a payment method and subscription
      const { error: paymentError, paymentMethod } = await stripe.createPaymentMethod({
        type: 'card',
        card: cardElement,
      });

      if (paymentError) {
        setError(paymentError.message);
        setLoading(false);
        return;
      }

      const { data: { sessionId } } = await axios.post('/api/checkout-session', {
        paymentMethodId: paymentMethod.id,
      });

      const { error: confirmError } = await stripe.confirmCardPayment(sessionId);

      if (confirmError) {
        setError(confirmError.message);
        setLoading(false);
        return;
      }

      setSuccess(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <h2>Subscribe to Our Chatbot</h2>
      <form onSubmit={handleSubmit} style={styles.form}>
        <CardElement style={styles.cardElement} />
        <button type="submit" disabled={!stripe || loading} style={styles.button}>
          {loading ? 'Processing...' : 'Subscribe'}
        </button>
      </form>
      {error && <div style={styles.error}>{error}</div>}
      {success && <div style={styles.success}>Subscription successful!</div>}
    </div>
  );
};

const styles = {
  container: {
    maxWidth: '400px',
    margin: '0 auto',
    padding: '20px',
    border: '1px solid #ccc',
    borderRadius: '10px',
    textAlign: 'center',
  },
  form: {
    marginTop: '20px',
  },
  cardElement: {
    marginBottom: '20px',
    padding: '10px',
    border: '1px solid #ccc',
    borderRadius: '5px',
  },
  button: {
    width: '100%',
    padding: '10px',
    backgroundColor: '#007bff',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
  },
  error: {
    marginTop: '20px',
    color: 'red',
  },
  success: {
    marginTop: '20px',
    color: 'green',
  },
};

export default PaymentPage;
