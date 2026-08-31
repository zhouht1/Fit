import { useState } from 'react';
import WorkoutTimer from './components/WorkoutTimer';
import RestTimer from './components/RestTimer';
import WorkoutComplete from './components/WorkoutComplete';
import WorkoutHistory from './components/WorkoutHistory';
import WorkoutDetail from './components/WorkoutDetail';

function App() {
  const [showRestTimer, setShowRestTimer] = useState(false);
  const [showWorkoutTimer, setShowWorkoutTimer] = useState(false);
  const [showComplete, setShowComplete] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const [selectedWorkoutId, setSelectedWorkoutId] = useState<number | null>(null);

  const mockCompleteData = {
    name: 'Push Day',
    duration: 3120,
    totalSets: 9,
    totalVolume: 8500,
    exerciseCount: 3,
  };

  return (
    <div className="min-h-screen p-6" style={{ backgroundColor: 'var(--bg-primary)' }}>
      <div className="max-w-md mx-auto flex flex-col gap-6">
        <div className="text-center py-4">
          <h1 className="text-5xl font-bold" style={{ color: 'var(--text-primary)' }}>Fit</h1>
          <p className="mt-2 text-lg" style={{ color: 'var(--text-secondary)' }}>Your Personal Fitness Companion</p>
        </div>

        {selectedWorkoutId ? (
          <WorkoutDetail workoutId={selectedWorkoutId} onBack={() => setSelectedWorkoutId(null)} />
        ) : showComplete ? (
          <WorkoutComplete {...mockCompleteData} onClose={() => setShowComplete(false)} />
        ) : showHistory ? (
          <div>
            <button
              onClick={() => setShowHistory(false)}
              className="mb-4 px-4 py-2 rounded-xl text-sm font-medium"
              style={{ color: 'var(--accent)' }}
            >
              ← Back
            </button>
            <WorkoutHistory onSelectWorkout={setSelectedWorkoutId} />
          </div>
        ) : (
          <>
            <div className="flex gap-3 justify-center flex-wrap">
              <button onClick={() => setShowWorkoutTimer(!showWorkoutTimer)} className="px-6 py-3 rounded-xl text-white font-medium" style={{ backgroundColor: 'var(--accent)' }}>
                Workout Timer
              </button>
              <button onClick={() => setShowRestTimer(!showRestTimer)} className="px-6 py-3 rounded-xl text-white font-medium" style={{ backgroundColor: 'var(--accent)' }}>
                Rest Timer
              </button>
              <button onClick={() => setShowComplete(true)} className="px-6 py-3 rounded-xl text-white font-medium" style={{ backgroundColor: 'var(--accent)' }}>
                Complete
              </button>
              <button onClick={() => setShowHistory(true)} className="px-6 py-3 rounded-xl text-white font-medium" style={{ backgroundColor: 'var(--accent)' }}>
                History
              </button>
            </div>

            {showWorkoutTimer && <WorkoutTimer onFinish={() => { setShowWorkoutTimer(false); setShowComplete(true); }} />}
            {showRestTimer && <RestTimer onComplete={() => alert('Rest complete!')} onSkip={() => setShowRestTimer(false)} />}
          </>
        )}
      </div>
    </div>
  );
}

export default App;