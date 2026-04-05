import { useState } from "react";
import "./PaymentGate.css";

/**
 * Stripe one-time checkout entry (backend: com.example.backend.stripepay).
 * Intentionally isolated from the original chat UI files.
 */
function PaymentGate({ displayUsername }) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  const startCheckout = async () => {
    setError("");
    const token = localStorage.getItem("token");
    if (!token) {
      setError("You are not logged in.");
      return;
    }
    setBusy(true);
    try {
      const res = await fetch("http://localhost:8080/api/stripepay/checkout-session", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const payloadText = await res.text();
      if (!res.ok) {
        setError(payloadText || "Could not start checkout.");
        return;
      }
      let data;
      try {
        data = JSON.parse(payloadText);
      } catch {
        setError("Unexpected response from server.");
        return;
      }
      if (data.url) {
        window.location.assign(data.url);
        return;
      }
      setError("Checkout URL missing — check Stripe configuration.");
    } catch (e) {
      console.error(e);
      setError("Network error starting checkout.");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="stripepay-shell">
      <div className="stripepay-card">
        <p className="stripepay-kicker">One-time access</p>
        <h2 className="stripepay-title">Unlock the AI chatbot</h2>
        <p className="stripepay-copy">
          Your login is valid, but this account still needs a single Stripe payment before the{" "}
          <code>/ai</code> endpoint will answer. This is a one-time unlock for{" "}
          <span className="stripepay-strong">{displayUsername || "your account"}</span>.
        </p>
        <ul className="stripepay-list">
          <li>Secure payment handled by Stripe Checkout</li>
          <li>No subscription — pay once, chat while this demo app runs</li>
          <li>After paying, you will return here automatically</li>
        </ul>
        <button
          className="stripepay-button"
          type="button"
          onClick={startCheckout}
          disabled={busy}>
          {busy ? "Starting checkout…" : "Pay with Stripe"}
        </button>
        {error ? <p className="stripepay-error">{error}</p> : null}
        <p className="stripepay-footnote">
          Tip: with a placeholder Stripe key, checkout will fail until you add real keys in{" "}
          <code>application-secrets.properties</code>. For local testing you can also mark the account paid in{" "}
          <code>Backend/data/paid-accounts.json</code>.
        </p>
      </div>
    </div>
  );
}

export default PaymentGate;
