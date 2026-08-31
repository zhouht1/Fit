import { useEffect, useState } from 'react';

interface Streak {
  currentStreak: number;
  longestStreak: number;
  lastWorkoutDate: string | null;
}

export default function StreakBanner() {
  const [streak, setStreak] = useState<Streak | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('/api/stats/streak', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((json) => setStreak(json.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="h-20 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />;
  }

  if (!streak || streak.currentStreak === 0) return null;

  return (
    <div className="flex items-center justify-between p-4 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <div>
        <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>Current Streak</div>
        <div className="text-2xl font-bold" style={{ color: 'var(--text-primary)' }}>
          {streak.currentStreak} {streak.currentStreak === 1 ? 'day' : 'days'}
        </div>
      </div>
      <div className="text-right">
        <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>Best Streak</div>
        <div className="text-xl font-bold" style={{ color: 'var(--accent)' }}>
          {streak.longestStreak} {streak.longestStreak === 1 ? 'day' : 'days'}
        </div>
      </div>
    </div>
  );
}