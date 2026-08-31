import { useEffect, useState } from 'react';
import { workoutApi, type Workout } from '../api/workouts';

interface WorkoutHistoryProps {
  onSelectWorkout: (id: number) => void;
}

export default function WorkoutHistory({ onSelectWorkout }: WorkoutHistoryProps) {
  const [workouts, setWorkouts] = useState<Workout[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    workoutApi.list()
      .then(setWorkouts)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col gap-3">
        {[1, 2, 3].map((i) => (
          <div key={i} className="h-20 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center p-6" style={{ color: 'var(--text-secondary)' }}>
        <p>Something went wrong.</p>
        <p className="text-sm mt-1">Please try again.</p>
        <button
          onClick={() => window.location.reload()}
          className="mt-3 px-4 py-2 rounded-xl text-white"
          style={{ backgroundColor: 'var(--accent)' }}
        >
          Retry
        </button>
      </div>
    );
  }

  if (workouts.length === 0) {
    return (
      <div className="text-center p-6" style={{ color: 'var(--text-secondary)' }}>
        <p className="text-lg">No workouts yet</p>
        <p className="text-sm mt-1">Start your first workout!</p>
      </div>
    );
  }

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  const formatDuration = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    return `${m} min`;
  };

  return (
    <div className="flex flex-col gap-3">
      {workouts.map((w) => (
        <button
          key={w.id}
          onClick={() => onSelectWorkout(w.id)}
          className="flex items-center justify-between p-4 rounded-2xl text-left"
          style={{ backgroundColor: 'var(--bg-card)' }}
        >
          <div>
            <div className="font-medium text-lg" style={{ color: 'var(--text-primary)' }}>{w.name}</div>
            <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>
              {w.startTime ? formatDate(w.startTime) : ''} · {w.totalSets} sets · {w.totalVolume.toLocaleString()} kg
            </div>
          </div>
          <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>
            {w.duration ? formatDuration(w.duration) : ''}
          </div>
        </button>
      ))}
    </div>
  );
}