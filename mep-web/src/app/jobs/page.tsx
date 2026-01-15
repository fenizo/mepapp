import React from 'react';

export default function JobsPage() {
    return (
        <div>
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
                <div>
                    <h2 style={{ fontSize: '2rem', fontWeight: 700 }}>Job Management</h2>
                    <p style={{ color: '#94a3b8' }}>Create and assign jobs to field staff.</p>
                </div>
                <button className="btn-primary">+ Create New Job</button>
            </header>

            <div style={{ display: 'grid', gap: '20px' }}>
                {[
                    { id: '1', customer: 'John Doe', staff: 'Siva', type: 'Electrical', status: 'In Progress', date: 'Oct 24, 2023' },
                    { id: '2', customer: 'Jane Smith', staff: 'Unassigned', type: 'Plumbing', status: 'New', date: 'Oct 25, 2023' },
                ].map((job) => (
                    <div key={job.id} className="glass-card" style={{ padding: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div style={{ display: 'flex', gap: '24px' }}>
                            <div>
                                <div style={{ fontSize: '0.875rem', color: '#94a3b8' }}>Customer</div>
                                <div style={{ fontWeight: 600 }}>{job.customer}</div>
                            </div>
                            <div>
                                <div style={{ fontSize: '0.875rem', color: '#94a3b8' }}>Type</div>
                                <div style={{ fontWeight: 600 }}>{job.type}</div>
                            </div>
                            <div>
                                <div style={{ fontSize: '0.875rem', color: '#94a3b8' }}>Staff</div>
                                <div style={{ fontWeight: 600, color: job.staff === 'Unassigned' ? 'var(--warning)' : 'inherit' }}>{job.staff}</div>
                            </div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                            <span style={{
                                padding: '4px 10px',
                                borderRadius: '20px',
                                fontSize: '0.75rem',
                                background: job.status === 'In Progress' ? 'rgba(56, 189, 248, 0.1)' : 'rgba(245, 158, 11, 0.1)',
                                color: job.status === 'In Progress' ? 'var(--primary)' : 'var(--warning)'
                            }}>
                                {job.status}
                            </span>
                            <button className="btn-primary" style={{ padding: '6px 12px', fontSize: '0.8rem', background: 'transparent', border: '1px solid var(--primary)', color: 'var(--primary)' }}>Edit Details</button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
