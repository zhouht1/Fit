import { useTimer } from '../hooks/useTimer';

interface WorkoutTimerProps {
  onFinish?: () => void;
}

export default function WorkoutTimer({ onFinish }: WorkoutTimerProps) {
  const { seconds, isRunning, start, pause, resume, reset, formatTime } = useTimer();

  const handleFinish = () => {
    reset();
    onFinish?.();
  };

  return (
    <div className="flex flex-col items-center gap-4 p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <h3 className="text-lg font-medium" style={{ color: 'var(--text-secondary)' }}>Workout Timer</h3>
      <div className="text-6xl font-bold tabular-nums tracking-tight" style={{ color: 'var(--text-primary)' }}>
        {formatTime(seconds)}
      </div>
      <div className="flex gap-3">
        {!isRunning && seconds === 0 && (
          <button
            onClick={start}
            className="px-8 py-3 rounded-xl text-white font-medium text-lg"
            style={{ backgroundColor: 'var(--accent)' }}
          >
            Start
          </button>
        )}
        {isRunning && (
          <button
            onClick={pause}
            className="px-8 py-3 rounded-xl text-white font-medium text-lg"
            style={{ backgroundColor: '#FF9500' }}
          >
            Pause
          </button>
        )}
        {!isRunning && seconds > 0 && (
          <button
            onClick={resume}
            className="px-8 py-3 rounded-xl text-white font-medium text-lg"
            style={{ backgroundColor: 'var(--accent)' }}
          >
            Resume
          </button>
        )}
        {seconds > 0 && (
          <button
            onClick={handleFinish}
            className="px-8 py-3 rounded-xl text-white font-medium text-lg"
            style={{ backgroundColor: 'var(--positive)' }}
          >
            Finish
          </button>
        )}
      </div>
    </div>
  );
}