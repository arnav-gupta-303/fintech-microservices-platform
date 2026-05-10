import React, { useState, useEffect } from 'react';
import { getPayments, getEmail } from '../api/api';

function PaymentHistory() {

  const [payments, setPayments] = useState([]);

  const [loading, setLoading] = useState(true);

  const currentUser = getEmail();

  useEffect(() => {
    getPayments()
      .then(d => {
        setPayments(d);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  return (
    <div>

      <div className="page-title">Payment History</div>

      <div className="page-sub">
        All your sent and received transactions
      </div>

      <div className="table-wrap">

        {loading ? (

          <div className="loading">Loading...</div>

        ) : payments.length === 0 ? (

          <div className="empty">
            <div className="empty-icon">◈</div>
            No payments yet
          </div>

        ) : (

          <table>

            <thead>
              <tr>
                <th>Type</th>
                <th>User</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Failure Reason</th>
                <th>Date</th>
              </tr>
            </thead>

            <tbody>

              {payments.map((p, i) => {

                const isSender = p.senderEmail === currentUser;

                return (

                  <tr key={i}>

                    <td>
                      <span className={`badge ${isSender ? 'debit' : 'credit'}`}>
                        {isSender ? 'SENT' : 'RECEIVED'}
                      </span>
                    </td>

                    <td>
                      {isSender ? p.receiverEmail : p.senderEmail}
                    </td>

                    <td>
                      ${parseFloat(p.amount).toFixed(2)}
                    </td>

                    <td>
                      <span className={`badge ${p.status.toLowerCase()}`}>
                        {p.status}
                      </span>
                    </td>

                    <td
                      style={{
                        color: 'var(--muted)',
                        fontSize: 12,
                        maxWidth: 220,
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap'
                      }}
                    >
                      {p.failureReason || '—'}
                    </td>

                    <td
                      style={{
                        color: 'var(--muted)',
                        fontSize: 13
                      }}
                    >
                      {new Date(p.createdAt).toLocaleString()}
                    </td>

                  </tr>
                );
              })}

            </tbody>

          </table>
        )}
      </div>
    </div>
  );
}

export default PaymentHistory;