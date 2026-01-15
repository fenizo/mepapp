import React from 'react';

export default function Dashboard() {
    const metrics = [
        { label: 'Total Calls Today', value: '24', color: 'var(--primary)' },
        { label: 'Completed Jobs', value: '18', color: 'var(--success)' },
        { label: 'Pending Jobs', value: '6', color: 'var(--warning)' },
        { label: 'Revenue Today', value: '₹42,500', color: 'var(--accent)' },
    ];

    return (
        <div>
            <header style={{ marginBottom: '32px' }}>
                <h2 style={{ fontSize: '2rem', fontWeight: 700 }}>Admin Dashboard</h2>
                <p style={{ color: '#94a3b8' }}>Welcome back, here is what is happening today.</p>
            </header>

            <div className="metric-grid">
                {metrics.map((m) => (
                    <div key={m.label} className="glass-card metric-card">
                        <span className="metric-label">{m.label}</span>
                        <span className="metric-value" style={{ color: m.color }}>{m.value}</span>
                    </div>
                ))}
            </div>

            <section className="glass-card" style={{ padding: '24px' }}>
                <h3 style={{ marginBottom: '20px', fontSize: '1.25rem' }}>Recent Active Jobs</h3>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ textAlign: 'left', borderBottom: '1px solid var(--card-border)', color: '#94a3b8' }}>
                            <th style={{ padding: '12px' }}>Customer</th>
                            <th style={{ padding: '12px' }}>Service</th>
                            <th style={{ padding: '12px' }}>Staff</th>
                            <th style={{ padding: '12px' }}>Status</th>
                            <th style={{ padding: '12px' }}>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {[
                            { customer: 'John Doe', service: 'Electrical Repair', staff: 'Siva', status: 'In Progress' },
                            { customer: 'Amit Sharma', service: 'Plumbing', staff: 'Rahul', status: 'New' },
                            { customer: 'Sarah Khan', service: 'AC Maintenance', staff: 'Vijay', status: 'In Progress' },
                        ].map((job, i) => (
                            <tr key={i} style={{ borderBottom: '1px solid var(--card-border)' }}>
                                <td style={{ padding: '12px' }}>{job.customer}</td>
                                <td style={{ padding: '12px' }}>{job.service}</td>
                                <td style={{ padding: '12px' }}>{job.staff}</td>
                                <td style={{ padding: '12px' }}>
                                    <span style={{
                                        padding: '4px 10px',
                                        borderRadius: '20px',
                                        fontSize: '0.75rem',
                                        background: job.status === 'In Progress' ? 'rgba(56, 189, 248, 0.1)' : 'rgba(245, 158, 11, 0.1)',
                                        color: job.status === 'In Progress' ? 'var(--primary)' : 'var(--warning)'
                                    }}>
                                        {job.status}
                                    </span>
                                </td>
                                <td style={{ padding: '12px' }}>
                                    <button className="btn-primary" style={{ padding: '6px 12px', fontSize: '0.8rem' }}>View</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </section>
        </div>
    );
}
