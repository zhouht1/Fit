interface WorkoutCompleteProps {
  name: string;
  duration: number;
  totalSets: number;
  totalVolume: number;
  exerciseCount: number;
  onClose: () => void;
}

function formatDuration(totalSeconds: number): string {
  const h = Math.floor(totalSeconds / 3600);
  const m = Math.floor((totalSeconds % 3600) / 60);
  if (h > 0) return `${h}h ${m}m`;
  return `${m} min`;
}

export default function WorkoutComplete({
  name, duration, totalSets, totalVolume, exerciseCount, onClose,
}: WorkoutCompleteProps) {
  return (
    <div className="flex flex-col items-center gap-6 p-8 rounded-3xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <div className="text-center">
        <div className="text-4xl mb-2">✓</div>
        <h2 className="text-3xl font-bold" style={{ color: 'var(--text-primary)' }}>Workout Complete</h2>
        <p className="text-lg mt-1" style={{ color: 'var(--text-secondary)' }}>{name}</p>
      </div>

      <div className="grid grid-cols-2 gap-4 w-full">
        <StatCard label="Duration" value={formatDuration(duration)} />
        <StatCard label="Total Sets" value={String(totalSets)} />
        <StatCard label="Total Volume" value={`${totalVolume.toLocaleString()} kg`} />
        <StatCard label="Exercises" value={String(exerciseCount)} />
      </div>

      <button
        onClick={onClose}
        className="w-full py-4 rounded-xl text-white font-medium text-lg"
        style={{ backgroundColor: 'var(--accent)' }}
      >
        Done
      </button>
    </div>
  );
}

function StatCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="text-center p-4 rounded-xl" style={{ backgroundColor: 'var(--bg-primary)' }}>
      <div className="text-2xl font-bold" style={{ color: 'var(--text-primary)' }}>{value}</div>
      <div className="text-sm mt-1" style={{ color: 'var(--text-secondary)' }}>{label}</div>
    </div>
  );
}