import { useEffect, useState } from 'react';

interface PR {
  exerciseId: number;
  exerciseName: string;
  maxWeight: number;
  maxReps: number;
  maxVolume: number;
}

export default function PersonalRecords() {
  const [records, setRecords] = useState<PR[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('/api/stats/personal-records', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((json) => setRecords(json.data || []))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="h-40 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />;
  }

  return (
    <div className="p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <h3 className="text-lg font-medium mb-4" style={{ color: 'var(--text-primary)' }}>Personal Records</h3>
      {records.length === 0 ? (
        <p className="text-center py-4" style={{ color: 'var(--text-secondary)' }}>Complete a workout to see your PRs</p>
      ) : (
        <div className="flex flex-col gap-2">
          {records.map((pr) => (
            <div key={pr.exerciseId} className="flex items-center justify-between py-2 border-t" style={{ borderColor: 'var(--bg-primary)' }}>
              <span className="font-medium" style={{ color: 'var(--text-primary)' }}>{pr.exerciseName}</span>
              <div className="flex gap-4 text-sm">
                <span style={{ color: 'var(--text-secondary)' }}>{pr.maxWeight} kg</span>
                <span style={{ color: 'var(--text-secondary)' }}>{pr.maxReps} reps</span>
                <span style={{ color: 'var(--text-secondary)' }}>{pr.maxVolume.toLocaleString()} kg</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}