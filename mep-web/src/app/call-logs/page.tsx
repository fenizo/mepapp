"use client";

import React, { useEffect, useState } from 'react';

interface CallLog {
    id: string;
    phoneNumber: string;
    duration: number;
    callType: string;
    timestamp: string;
    staff: {
        name: string;
    };
}

export default function CallLogsPage() {
    const [logs, setLogs] = useState<CallLog[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('/api/call-logs')
            .then(res => res.json())
            .then(data => {
                setLogs(data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, []);

    return (
        <div>
            <header style={{ marginBottom: '32px' }}>
                <h2 style={{ fontSize: '2rem', fontWeight: 700 }}>Call Logs</h2>
                <p style={{ color: '#94a3b8' }}>Monitor all field staff communication.</p>
            </header>

            <section className="glass-card" style={{ padding: '24px' }}>
                {loading ? (
                    <p>Loading logs...</p>
                ) : (
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                            <tr style={{ textAlign: 'left', borderBottom: '1px solid var(--card-border)', color: '#94a3b8' }}>
                                <th style={{ padding: '12px' }}>Staff</th>
                                <th style={{ padding: '12px' }}>Phone Number</th>
                                <th style={{ padding: '12px' }}>Type</th>
                                <th style={{ padding: '12px' }}>Duration</th>
                                <th style={{ padding: '12px' }}>Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            {logs.map((log) => (
                                <tr key={log.id} style={{ borderBottom: '1px solid var(--card-border)' }}>
                                    <td style={{ padding: '12px' }}>{log.staff?.name || "Unknown"}</td>
                                    <td style={{ padding: '12px' }}>{log.phoneNumber}</td>
                                    <td style={{ padding: '12px' }}>
                                        <span style={{
                                            padding: '4px 10px',
                                            borderRadius: '20px',
                                            fontSize: '0.75rem',
                                            background: log.callType === 'OUTGOING' ? 'rgba(56, 189, 248, 0.1)' :
                                                log.callType === 'INCOMING' ? 'rgba(34, 197, 94, 0.1)' :
                                                    'rgba(244, 63, 94, 0.1)',
                                            color: log.callType === 'OUTGOING' ? 'var(--primary)' :
                                                log.callType === 'INCOMING' ? 'var(--success)' :
                                                    '#f43f5e'
                                        }}>
                                            {log.callType}
                                        </span>
                                    </td>
                                    <td style={{ padding: '12px' }}>{Math.floor(log.duration / 60)}m {log.duration % 60}s</td>
                                    <td style={{ padding: '12px' }}>{new Date(log.timestamp).toLocaleString()}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </section>
        </div>
    );
}
