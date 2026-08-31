import { useState, useEffect, useRef } from 'react';

interface RestTimerProps {
  onComplete?: () => void;
  onSkip?: () => void;
}

const PRESETS = [60, 90, 120];

export default function RestTimer({ onComplete, onSkip }: RestTimerProps) {
  const [totalSeconds, setTotalSeconds] = useState(90);
  const [remaining, setRemaining] = useState(90);
  const [isRunning, setIsRunning] = useState(false);
  const [customInput, setCustomInput] = useState('');
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    if (isRunning && remaining > 0) {
      intervalRef.current = setInterval(() => {
        setRemaining((prev) => {
          if (prev <= 1) {
            setIsRunning(false);
            onComplete?.();
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [isRunning, remaining, onComplete]);

  const startTimer = (seconds: number) => {
    setTotalSeconds(seconds);
    setRemaining(seconds);
    setIsRunning(true);
  };

  const add30Seconds = () => {
    setRemaining((prev) => prev + 30);
    setTotalSeconds((prev) => prev + 30);
  };

  const handleSkip = () => {
    setIsRunning(false);
    onSkip?.();
  };

  const handleCustomStart = () => {
    const s = parseInt(customInput);
    if (s > 0) {
      startTimer(s);
      setCustomInput('');
    }
  };

  const formatTime = (s: number) => {
    const m = Math.floor(s / 60);
    const sec = s % 60;
    return `${m}:${String(sec).padStart(2, '0')}`;
  };

  const progress = totalSeconds > 0 ? (remaining / totalSeconds) * 100 : 0;

  return (
    <div className="flex flex-col items-center gap-4 p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <h3 className="text-lg font-medium" style={{ color: 'var(--text-secondary)' }}>Rest Timer</h3>

      {!isRunning ? (
        <>
          <div className="flex gap-2">
            {PRESETS.map((sec) => (
              <button
                key={sec}
                onClick={() => startTimer(sec)}
                className="px-5 py-2 rounded-xl font-medium"
                style={{ backgroundColor: 'var(--accent)', color: '#fff' }}
              >
                {sec}s
              </button>
            ))}
          </div>
          <div className="flex gap-2 items-center">
            <input
              type="number"
              placeholder="Custom (s)"
              value={customInput}
              onChange={(e) => setCustomInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleCustomStart()}
              className="px-4 py-2 rounded-xl border text-center w-32"
              style={{ borderColor: 'var(--text-secondary)', color: 'var(--text-primary)', backgroundColor: 'var(--bg-primary)' }}
              min={1}
            />
            <button
              onClick={handleCustomStart}
              className="px-4 py-2 rounded-xl text-white font-medium"
              style={{ backgroundColor: 'var(--accent)' }}
            >
              Set
            </button>
          </div>
        </>
      ) : (
        <>
          <div className="relative w-48 h-48 flex items-center justify-center">
            <svg className="absolute w-full h-full -rotate-90">
              <circle
                cx="96" cy="96" r="88"
                fill="none"
                stroke="var(--bg-primary)"
                strokeWidth="8"
              />
              <circle
                cx="96" cy="96" r="88"
                fill="none"
                stroke="var(--accent)"
                strokeWidth="8"
                strokeDasharray={`${2 * Math.PI * 88}`}
                strokeDashoffset={`${2 * Math.PI * 88 * (1 - progress / 100)}`}
                strokeLinecap="round"
                style={{ transition: 'stroke-dashoffset 0.5s linear' }}
              />
            </svg>
            <div className="text-5xl font-bold tabular-nums" style={{ color: 'var(--text-primary)' }}>
              {formatTime(remaining)}
            </div>
          </div>
          <div className="flex gap-3">
            <button
              onClick={add30Seconds}
              className="px-5 py-2 rounded-xl font-medium"
              style={{ backgroundColor: 'var(--accent)', color: '#fff' }}
            >
              +30s
            </button>
            <button
              onClick={handleSkip}
              className="px-5 py-2 rounded-xl font-medium"
              style={{ borderColor: 'var(--text-secondary)', borderWidth: 1, color: 'var(--text-secondary)' }}
            >
              Skip
            </button>
          </div>
        </>
      )}
    </div>
  );
}