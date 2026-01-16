"use client";

import React, { useEffect, useState } from 'react';
import { apiFetch } from '../lib/api';

interface JobItem {
    description: string;
    quantity: number;
    price: number;
}

interface Job {
    id: string;
    customer: {
        name: string;
    };
    staff: {
        username: string;
    };
    serviceType: string;
    status: string;
    items: JobItem[];
    createdAt: string;
}

export default function Dashboard() {
    const [jobs, setJobs] = useState<Job[]>([]);
    const [loading, setLoading] = useState(true);

    const [callLogs, setCallLogs] = useState<any[]>([]);

    useEffect(() => {
        apiFetch('/api/jobs')
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) {
                    setJobs(data);
                }
                setLoading(false);
            })
            .catch(err => {
                console.error("Failed to fetch jobs:", err);
                setLoading(false);
            });

        apiFetch('/api/call-logs') // Using standard path now
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) {
                    setCallLogs(data);
                }
            })
            .catch(() => { });
    }, []);

    const totalCalls = jobs.length;
    const completedJobs = jobs.filter(j => j.status === 'COMPLETED').length;
    const pendingJobs = jobs.filter(j => j.status === 'NEW' || j.status === 'IN_PROGRESS').length;

    const revenue = jobs
        .filter(j => j.status === 'COMPLETED') // Assuming revenue is only realized on completion
        .reduce((sum, job) => sum + (job.items?.reduce((itemSum: number, item: any) => itemSum + (item.price * item.quantity), 0) || 0), 0);

    return (
        <div>
            <header style={{ marginBottom: '32px' }}>
                <h2 style={{ fontSize: '2rem', fontWeight: 700, marginBottom: '4px' }}>Admin Dashboard</h2>
                <p style={{ color: '#94a3b8' }}>Welcome back, here is what is happening today.</p>
            </header>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '24px', marginBottom: '32px' }}>
                <div className="glass-card" style={{ padding: '24px' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.875rem', marginBottom: '8px' }}>Total Jobs</div>
                    <div style={{ fontSize: '1.875rem', fontWeight: 700, color: 'var(--primary)' }}>{totalCalls}</div>
                </div>
                <div className="glass-card" style={{ padding: '24px' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.875rem', marginBottom: '8px' }}>Completed Jobs</div>
                    <div style={{ fontSize: '1.875rem', fontWeight: 700, color: '#22c55e' }}>{completedJobs}</div>
                </div>
                <div className="glass-card" style={{ padding: '24px' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.875rem', marginBottom: '8px' }}>Pending Jobs</div>
                    <div style={{ fontSize: '1.875rem', fontWeight: 700, color: 'var(--warning)' }}>{pendingJobs}</div>
                </div>
                <div className="glass-card" style={{ padding: '24px' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.875rem', marginBottom: '8px' }}>Synced Logs</div>
                    <div style={{ fontSize: '1.875rem', fontWeight: 700, color: '#38bdf8' }}>{callLogs.length}</div>
                </div>
            </div>

            <div className="glass-card" style={{ padding: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                    <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>Recent Active Jobs</h3>
                </div>
                <div className="table-container" style={{ marginTop: 0, border: 'none', borderRadius: 0 }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                        <thead>
                            <tr style={{ borderBottom: '1px solid var(--card-border)' }}>
                                <th style={{ padding: '12px 16px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Customer</th>
                                <th style={{ padding: '12px 16px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Service</th>
                                <th style={{ padding: '12px 16px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Staff</th>
                                <th style={{ padding: '12px 16px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Status</th>
                                <th style={{ padding: '12px 16px', color: '#94a3b8', fontSize: '0.875rem', fontWeight: 500 }}>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {jobs.length === 0 ? (
                                <tr><td colSpan={5} style={{ padding: '40px', textAlign: 'center', color: '#475569' }}>No active jobs found.</td></tr>
                            ) : (
                                jobs.map((job) => (
                                    <tr key={job.id} style={{ borderBottom: '1px solid var(--card-border)' }}>
                                        <td style={{ padding: '16px', fontWeight: 500 }}>{job.customer?.name || 'Unknown'}</td>
                                        <td style={{ padding: '16px' }}>{job.serviceType}</td>
                                        <td style={{ padding: '16px', color: '#94a3b8' }}>{job.staff?.username || 'Unassigned'}</td>
                                        <td style={{ padding: '16px' }}>
                                            <span style={{
                                                padding: '4px 10px',
                                                borderRadius: '20px',
                                                fontSize: '0.75rem',
                                                background: job.status === 'COMPLETED' ? 'rgba(34, 197, 94, 0.1)' : 'rgba(56, 189, 248, 0.1)',
                                                color: job.status === 'COMPLETED' ? '#22c55e' : 'var(--primary)'
                                            }}>
                                                {job.status}
                                            </span>
                                        </td>
                                        <td style={{ padding: '16px' }}><button className="btn-primary" style={{ padding: '6px 12px', fontSize: '0.8rem' }}>View</button></td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
