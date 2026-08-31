import { useState } from 'react';
import WorkoutTimer from './components/WorkoutTimer';
import RestTimer from './components/RestTimer';

function App() {
  const [showRestTimer, setShowRestTimer] = useState(false);
  const [showWorkoutTimer, setShowWorkoutTimer] = useState(false);

  return (
    <div className="min-h-screen p-6" style={{ backgroundColor: 'var(--bg-primary)' }}>
      <div className="max-w-md mx-auto flex flex-col gap-6">
        <div className="text-center py-4">
          <h1 className="text-5xl font-bold" style={{ color: 'var(--text-primary)' }}>Fit</h1>
          <p className="mt-2 text-lg" style={{ color: 'var(--text-secondary)' }}>Your Personal Fitness Companion</p>
        </div>

        <div className="flex gap-3 justify-center">
          <button
            onClick={() => setShowWorkoutTimer(!showWorkoutTimer)}
            className="px-6 py-3 rounded-xl text-white font-medium"
            style={{ backgroundColor: 'var(--accent)' }}
          >
            {showWorkoutTimer ? 'Hide' : 'Show'} Workout Timer
          </button>
          <button
            onClick={() => setShowRestTimer(!showRestTimer)}
            className="px-6 py-3 rounded-xl text-white font-medium"
            style={{ backgroundColor: 'var(--accent)' }}
          >
            {showRestTimer ? 'Hide' : 'Show'} Rest Timer
          </button>
        </div>

        {showWorkoutTimer && <WorkoutTimer onFinish={() => alert('Workout finished!')} />}
        {showRestTimer && <RestTimer onComplete={() => alert('Rest complete! Next Set')} onSkip={() => setShowRestTimer(false)} />}
      </div>
    </div>
  );
}

export default App;