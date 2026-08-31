interface BottomNavProps {
  active: string;
  onNavigate: (tab: string) => void;
}

const TABS = [
  { key: 'today', label: 'Today', icon: '📋' },
  { key: 'workout', label: 'Workout', icon: '🏋️' },
  { key: 'progress', label: 'Progress', icon: '📈' },
  { key: 'profile', label: 'Profile', icon: '👤' },
];

export default function BottomNav({ active, onNavigate }: BottomNavProps) {
  return (
    <nav
      className="fixed bottom-0 left-0 right-0 flex justify-around py-3 px-2 z-50 border-t"
      style={{
        backgroundColor: 'var(--bg-card)',
        borderColor: 'var(--bg-primary)',
      }}
    >
      {TABS.map((tab) => (
        <button
          key={tab.key}
          onClick={() => onNavigate(tab.key)}
          className="flex flex-col items-center gap-1 px-3 py-1 rounded-xl transition-colors min-w-0 flex-1"
          style={{
            color: active === tab.key ? 'var(--accent)' : 'var(--text-secondary)',
          }}
        >
          <span className="text-xl">{tab.icon}</span>
          <span className="text-xs font-medium">{tab.label}</span>
        </button>
      ))}
    </nav>
  );
}