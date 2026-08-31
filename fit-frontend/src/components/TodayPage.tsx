import { useEffect, useState } from 'react';

interface TodayData {
  greeting: string;
  userName: string;
  date: string;
  workout: {
    planId: number | null;
    name: string;
    muscleGroups: string;
    estimatedDuration: number | null;
    hasWorkout: boolean;
  };
  weeklyActivity: boolean[];
  weight: {
    current: number | null;
    change: number | null;
  };
  recovery: {
    status: string;
    suggestion: string;
  };
  streak: {
    currentStreak: number;
    longestStreak: number;
  } | null;
}

export default function TodayPage() {
  const [data, setData] = useState<TodayData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('/api/today', { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => res.json())
      .then((json) => setData(json.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col gap-4">
        <div className="h-8 w-48 rounded animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />
        <div className="h-40 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />
        <div className="h-24 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />
      </div>
    );
  }

  if (!data) return null;

  return (
    <div className="flex flex-col gap-4">
      {/* Greeting */}
      <div>
        <h2 className="text-3xl font-bold" style={{ color: 'var(--text-primary)' }}>
          {data.greeting}, {data.userName}
        </h2>
        <p className="text-sm mt-1" style={{ color: 'var(--text-secondary)' }}>{data.date}</p>
      </div>

      {/* Streak */}
      {data.streak && data.streak.currentStreak > 0 && (
        <div className="flex items-center gap-2 px-4 py-2 rounded-xl" style={{ backgroundColor: 'var(--bg-card)' }}>
          <span className="text-lg">🔥</span>
          <span className="font-medium" style={{ color: 'var(--text-primary)' }}>
            {data.streak.currentStreak} day streak
          </span>
        </div>
      )}

      {/* Today's Workout */}
      <div className="p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
        {data.workout.hasWorkout ? (
          <>
            <h3 className="text-sm font-medium" style={{ color: 'var(--text-secondary)' }}>Today's Workout</h3>
            <div className="text-2xl font-bold mt-1" style={{ color: 'var(--text-primary)' }}>{data.workout.name}</div>
            {data.workout.muscleGroups && (
              <div className="text-sm mt-1" style={{ color: 'var(--text-secondary)' }}>{data.workout.muscleGroups}</div>
            )}
            {data.workout.estimatedDuration && (
              <div className="text-sm mt-1" style={{ color: 'var(--text-secondary)' }}>{data.workout.estimatedDuration} min</div>
            )}
            <button className="w-full mt-4 py-4 rounded-xl text-white font-medium text-lg" style={{ backgroundColor: 'var(--accent)', height: 56 }}>
              START WORKOUT
            </button>
          </>
        ) : (
          <>
            <h3 className="text-sm font-medium" style={{ color: 'var(--text-secondary)' }}>Today</h3>
            <div className="text-2xl font-bold mt-1" style={{ color: 'var(--text-primary)' }}>Recovery Day</div>
            <div className="text-sm mt-1" style={{ color: 'var(--text-secondary)' }}>{data.recovery.suggestion}</div>
          </>
        )}
      </div>

      {/* Weekly Activity */}
      <div className="p-4 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
        <div className="text-sm font-medium mb-2" style={{ color: 'var(--text-secondary)' }}>This Week</div>
        <div className="flex gap-2 justify-center">
          {data.weeklyActivity.map((active, i) => (
            <div
              key={i}
              className="w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium"
              style={{
                backgroundColor: active ? 'var(--accent)' : 'var(--bg-primary)',
                color: active ? '#fff' : 'var(--text-secondary)',
              }}
            >
              {active ? '●' : '○'}
            </div>
          ))}
        </div>
      </div>

      {/* Weight + Recovery */}
      <div className="grid grid-cols-2 gap-3">
        <div className="p-4 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
          <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>Weight</div>
          {data.weight.current ? (
            <>
              <div className="text-2xl font-bold mt-1" style={{ color: 'var(--text-primary)' }}>
                {data.weight.current} kg
              </div>
              {data.weight.change != null && (
                <div className="text-sm mt-1" style={{ color: data.weight.change < 0 ? 'var(--positive)' : 'var(--text-secondary)' }}>
                  {data.weight.change > 0 ? '+' : ''}{data.weight.change} kg
                </div>
              )}
            </>
          ) : (
            <div className="text-sm mt-2" style={{ color: 'var(--text-secondary)' }}>No data</div>
          )}
        </div>

        <div className="p-4 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
          <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>Recovery</div>
          <div className="text-2xl mt-1">
            {data.recovery.status === 'Recovery Day' ? '🙂' : '💪'}
          </div>
          <div className="text-sm font-medium mt-1" style={{ color: 'var(--text-primary)' }}>{data.recovery.status}</div>
        </div>
      </div>
    </div>
  );
}