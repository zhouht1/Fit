const API_BASE = '/api';

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  const res = await fetch(`${API_BASE}${url}`, { ...options, headers });
  const json = await res.json();

  if (json.code !== 200) {
    throw new Error(json.message || 'Request failed');
  }
  return json.data;
}

export interface Workout {
  id: number;
  userId: number;
  planId: number | null;
  name: string;
  startTime: string;
  endTime: string | null;
  duration: number;
  totalVolume: number;
  totalSets: number;
  exerciseCount: number;
  status: string;
  sets: WorkoutSet[];
}

export interface WorkoutSet {
  id: number;
  workoutId: number;
  exerciseId: number;
  exerciseName: string;
  setNumber: number;
  weight: number;
  reps: number;
  volume: number;
  completed: boolean;
}

export const workoutApi = {
  list: () => request<Workout[]>('/workouts'),
  get: (id: number) => request<Workout>(`/workouts/${id}`),
  start: (data: { name: string; planId?: number | null }) =>
    request<Workout>('/workouts', { method: 'POST', body: JSON.stringify(data) }),
  addSet: (id: number, data: { exerciseId: number; weight: number; reps: number }) =>
    request<WorkoutSet>(`/workouts/${id}/sets`, { method: 'POST', body: JSON.stringify(data) }),
  finish: (id: number) =>
    request<Workout>(`/workouts/${id}/finish`, { method: 'POST' }),
};