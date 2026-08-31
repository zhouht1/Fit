import { useEffect, useState } from 'react';
import { workoutApi, type Workout } from '../api/workouts';

interface WorkoutDetailProps {
  workoutId: number;
  onBack: () => void;
}

export default function WorkoutDetail({ workoutId, onBack }: WorkoutDetailProps) {
  const [workout, setWorkout] = useState<Workout | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    workoutApi.get(workoutId)
      .then(setWorkout)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [workoutId]);

  if (loading) {
    return (
      <div className="flex flex-col gap-3">
        <div className="h-40 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />
        <div className="h-20 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />
      </div>
    );
  }

  if (error || !workout) {
    return (
      <div className="text-center p-6" style={{ color: 'var(--text-secondary)' }}>
        <p>Workout not found</p>
        <button onClick={onBack} className="mt-3 px-4 py-2 rounded-xl text-white" style={{ backgroundColor: 'var(--accent)' }}>
          Back
        </button>
      </div>
    );
  }

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  const formatDuration = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    return `${m} min`;
  };

  // Group sets by exercise
  const exerciseGroups = new Map<number, { name: string; sets: typeof workout.sets }>();
  workout.sets.forEach((s) => {
    if (!exerciseGroups.has(s.exerciseId)) {
      exerciseGroups.set(s.exerciseId, { name: s.exerciseName, sets: [] });
    }
    exerciseGroups.get(s.exerciseId)!.sets.push(s);
  });

  return (
    <div className="flex flex-col gap-4">
      <button
        onClick={onBack}
        className="self-start px-4 py-2 rounded-xl text-sm font-medium"
        style={{ color: 'var(--accent)' }}
      >
        ← Back
      </button>

      <div className="p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
        <h2 className="text-2xl font-bold" style={{ color: 'var(--text-primary)' }}>{workout.name}</h2>
        <div className="flex gap-4 mt-2 text-sm" style={{ color: 'var(--text-secondary)' }}>
          {workout.startTime && <span>{formatDate(workout.startTime)}</span>}
          {workout.duration > 0 && <span>{formatDuration(workout.duration)}</span>}
        </div>

        <div className="grid grid-cols-3 gap-3 mt-4">
          <MiniStat label="Total Sets" value={String(workout.totalSets)} />
          <MiniStat label="Volume" value={`${workout.totalVolume.toLocaleString()} kg`} />
          <MiniStat label="Exercises" value={String(workout.exerciseCount)} />
        </div>
      </div>

      {Array.from(exerciseGroups.entries()).map(([exerciseId, group]) => (
        <div key={exerciseId} className="p-4 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
          <h3 className="font-medium text-lg mb-3" style={{ color: 'var(--text-primary)' }}>{group.name}</h3>
          <div className="flex gap-2 text-xs font-medium mb-2" style={{ color: 'var(--text-secondary)' }}>
            <span className="w-8">Set</span>
            <span className="w-16">Weight</span>
            <span className="w-12">Reps</span>
            <span>Volume</span>
          </div>
          {group.sets.map((s) => (
            <div key={s.id} className="flex gap-2 py-2 border-t text-sm" style={{ borderColor: 'var(--bg-primary)', color: 'var(--text-primary)' }}>
              <span className="w-8 font-medium">{s.setNumber}</span>
              <span className="w-16">{s.weight} kg</span>
              <span className="w-12">{s.reps}</span>
              <span className="font-medium">{s.volume.toLocaleString()} kg</span>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
}

function MiniStat({ label, value }: { label: string; value: string }) {
  return (
    <div className="text-center p-2 rounded-xl" style={{ backgroundColor: 'var(--bg-primary)' }}>
      <div className="text-lg font-bold" style={{ color: 'var(--text-primary)' }}>{value}</div>
      <div className="text-xs" style={{ color: 'var(--text-secondary)' }}>{label}</div>
    </div>
  );
}