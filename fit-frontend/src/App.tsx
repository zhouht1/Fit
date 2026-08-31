import { useState } from 'react';
import WorkoutTimer from './components/WorkoutTimer';
import RestTimer from './components/RestTimer';
import WorkoutComplete from './components/WorkoutComplete';
import WorkoutHistory from './components/WorkoutHistory';
import WorkoutDetail from './components/WorkoutDetail';
import BodyMeasurement from './components/BodyMeasurement';
import ProgressChart from './components/ProgressChart';
import PersonalRecords from './components/PersonalRecords';
import ProgressiveOverload from './components/ProgressiveOverload';
import TodayPage from './components/TodayPage';
import BottomNav from './components/BottomNav';
import { ToastContainer } from './components/Toast';
import { useTheme } from './hooks/useTheme';

function App() {
  const [activeTab, setActiveTab] = useState('today');
  const [selectedWorkoutId, setSelectedWorkoutId] = useState<number | null>(null);
  const { toggle, icon } = useTheme();

  const mockCompleteData = {
    name: 'Push Day', duration: 3120, totalSets: 9, totalVolume: 8500, exerciseCount: 3,
  };

  const renderContent = () => {
    if (selectedWorkoutId) {
      return <WorkoutDetail workoutId={selectedWorkoutId} onBack={() => setSelectedWorkoutId(null)} />;
    }

    switch (activeTab) {
      case 'today':
        return <TodayPage />;
      case 'workout':
        return (
          <div className="flex flex-col gap-4">
            <WorkoutHistory onSelectWorkout={setSelectedWorkoutId} />
            <WorkoutTimer onFinish={() => {}} />
            <RestTimer onComplete={() => {}} onSkip={() => {}} />
          </div>
        );
      case 'progress':
        return (
          <div className="flex flex-col gap-4">
            <ProgressChart />
            <PersonalRecords />
            <ProgressiveOverload />
            <BodyMeasurement />
          </div>
        );
      case 'profile':
        return <WorkoutComplete {...mockCompleteData} onClose={() => setActiveTab('today')} />;
      default:
        return <TodayPage />;
    }
  };

  return (
    <div className="min-h-screen pb-20" style={{ backgroundColor: 'var(--bg-primary)' }}>
      <ToastContainer />

      <div className="max-w-md mx-auto flex flex-col gap-6 p-6">
        <div className="flex items-center justify-between py-2">
          <h1 className="text-3xl font-bold" style={{ color: 'var(--text-primary)' }}>Fit</h1>
          <button
            onClick={toggle}
            className="w-10 h-10 rounded-xl flex items-center justify-center text-lg transition-colors hover:opacity-80 active:scale-95"
            style={{ backgroundColor: 'var(--bg-card)' }}
          >
            {icon}
          </button>
        </div>

        {renderContent()}
      </div>

      {!selectedWorkoutId && (
        <BottomNav active={activeTab} onNavigate={setActiveTab} />
      )}
    </div>
  );
}

export default App;