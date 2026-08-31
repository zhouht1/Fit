import { useEffect, useState } from 'react';

interface Overload {
  exerciseName: string;
  previousWeight: number;
  previousReps: number;
  currentWeight: number;
  currentReps: number;
  suggestion: string;
}

export default function ProgressiveOverload() {
  const [data, setData] = useState<Overload[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('/api/stats/progressive-overload', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((json) => setData(json.data || []))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="h-40 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />;
  }

  if (data.length === 0) return null;

  return (
    <div className="p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <h3 className="text-lg font-medium mb-4" style={{ color: 'var(--text-primary)' }}>Progress</h3>
      {data.map((item, i) => (
        <div key={i} className="py-3 border-t" style={{ borderColor: 'var(--bg-primary)' }}>
          <div className="font-medium" style={{ color: 'var(--text-primary)' }}>{item.exerciseName}</div>
          <div className="flex items-center gap-2 mt-1 text-sm">
            <span style={{ color: 'var(--text-secondary)' }}>
              {item.previousWeight}kg × {item.previousReps}
            </span>
            <span style={{ color: 'var(--text-secondary)' }}>→</span>
            <span className="font-medium" style={{ color: 'var(--text-primary)' }}>
              {item.currentWeight}kg × {item.currentReps}
            </span>
          </div>
          <div className="mt-1 text-sm font-medium" style={{ color: 'var(--positive)' }}>{item.suggestion}</div>
        </div>
      ))}
    </div>
  );
}