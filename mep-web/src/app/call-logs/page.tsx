"use client";

import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../lib/api';

interface CallLog {
    id: string;
    staffName: string;
    phoneNumber: string;
    callType: string;
    duration: string;
    timestamp: string;
}

const CallLogsPage = () => {
    const [logs, setLogs] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

    const fetchLogs = () => {
        setLoading(true);
        apiFetch('/api/call-logs')
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) {
                    setLogs(data);
                }
                setLoading(false);
                setLastUpdated(new Date());
            })
            .catch((err) => {
                setLoading(false);
                console.error("Error fetching logs:", err);
            });
    };

    useEffect(() => {
        fetchLogs();
    }, []);

    return (
        <div className="animate-enter">
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
                <div>
                    <h2 style={{ fontSize: '2rem', fontWeight: 700, marginBottom: '4px' }}>Call Logs</h2>
                    <p style={{ color: '#94a3b8' }}>Monitor all field staff communication.</p>
                </div>
                <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
                    {lastUpdated && <span style={{ fontSize: '0.8rem', color: '#475569' }}>Last updated: {lastUpdated.toLocaleTimeString()}</span>}
                    <button onClick={fetchLogs} disabled={loading} className="btn-primary">
                        {loading ? 'Updating...' : 'Refresh Logs'}
                    </button>
                </div>
            </header>

            <div className="glass-card" style={{ overflow: 'hidden' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                    <thead>
                        <tr style={{ background: 'rgba(255, 255, 255, 0.02)', borderBottom: '1px solid var(--card-border)' }}>
                            <th style={{ padding: '16px 24px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Staff</th>
                            <th style={{ padding: '16px 24px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Phone Number</th>
                            <th style={{ padding: '16px 24px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Type</th>
                            <th style={{ padding: '16px 24px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Duration</th>
                            <th style={{ padding: '16px 24px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading && logs.length === 0 ? (
                            <tr><td colSpan={5} style={{ padding: '48px', textAlign: 'center', color: '#475569' }}>Fetching call history...</td></tr>
                        ) : logs.length === 0 ? (
                            <tr><td colSpan={5} style={{ padding: '48px', textAlign: 'center', color: '#475569' }}>No call logs available.</td></tr>
                        ) : (
                            logs.map((log) => (
                                <tr key={log.id} style={{ borderBottom: '1px solid var(--card-border)', transition: 'background 0.2s' }}>
                                    <td style={{ padding: '16px 24px', fontWeight: 500 }}>{log.staffName || 'Admin'}</td>
                                    <td style={{ padding: '16px 24px', color: '#38bdf8' }}>{log.phoneNumber}</td>
                                    <td style={{ padding: '16px 24px' }}>
                                        <span style={{
                                            padding: '4px 8px',
                                            borderRadius: '4px',
                                            fontSize: '0.7rem',
                                            background: log.callType === 'OUTGOING' ? 'rgba(56, 189, 248, 0.1)' : 'rgba(34, 197, 94, 0.1)',
                                            color: log.callType === 'OUTGOING' ? '#38bdf8' : '#22c55e'
                                        }}>
                                            {log.callType}
                                        </span>
                                    </td>
                                    <td style={{ padding: '16px 24px', color: '#94a3b8' }}>{log.duration}</td>
                                    <td style={{ padding: '16px 24px', color: '#94a3b8', fontSize: '0.875rem' }}>
                                        {new Date(log.timestamp).toLocaleString()}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default CallLogsPage;
