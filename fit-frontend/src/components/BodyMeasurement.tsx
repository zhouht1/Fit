import { useEffect, useState } from 'react';

const METRICS = [
  { key: 'weight', label: 'Weight', unit: 'kg' },
  { key: 'bodyFat', label: 'Body Fat', unit: '%' },
  { key: 'chest', label: 'Chest', unit: 'cm' },
  { key: 'waist', label: 'Waist', unit: 'cm' },
  { key: 'hip', label: 'Hip', unit: 'cm' },
  { key: 'arm', label: 'Arm', unit: 'cm' },
  { key: 'thigh', label: 'Thigh', unit: 'cm' },
];

interface Measurement {
  id: number;
  weight: number;
  bodyFat: number | null;
  chest: number | null;
  waist: number | null;
  hip: number | null;
  arm: number | null;
  thigh: number | null;
  measuredAt: string;
}

export default function BodyMeasurement() {
  const [measurements, setMeasurements] = useState<Measurement[]>([]);
  const [selectedMetric, setSelectedMetric] = useState('weight');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('/api/body-measurements?period=30d', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((json) => setMeasurements(json.data || []))
      .finally(() => setLoading(false));
  }, []);

  const metric = METRICS.find((m) => m.key === selectedMetric)!;

  if (loading) {
    return <div className="h-64 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />;
  }

  return (
    <div className="p-6 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <h3 className="text-lg font-medium mb-4" style={{ color: 'var(--text-primary)' }}>Body Measurements</h3>

      <div className="flex gap-2 mb-4 flex-wrap">
        {METRICS.map((m) => (
          <button
            key={m.key}
            onClick={() => setSelectedMetric(m.key)}
            className="px-3 py-1 rounded-lg text-sm font-medium transition-colors"
            style={{
              backgroundColor: selectedMetric === m.key ? 'var(--accent)' : 'var(--bg-primary)',
              color: selectedMetric === m.key ? '#fff' : 'var(--text-secondary)',
            }}
          >
            {m.label}
          </button>
        ))}
      </div>

      {measurements.length === 0 ? (
        <p className="text-center py-8" style={{ color: 'var(--text-secondary)' }}>No measurements yet</p>
      ) : (
        <div className="flex flex-col gap-2">
          <div className="text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>
            {measurements[measurements.length - 1]?.[metric.key as keyof Measurement] ?? '-'}
            <span className="text-lg ml-1" style={{ color: 'var(--text-secondary)' }}>{metric.unit}</span>
          </div>
          <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>
            Latest: {measurements[measurements.length - 1]?.measuredAt}
          </div>
          <div className="mt-3 flex items-end gap-1 h-32">
            {measurements.map((m, i) => {
              const val = m[metric.key as keyof Measurement] as number;
              const maxVal = Math.max(...measurements.map((x) => (x[metric.key as keyof Measurement] as number) || 0));
              const height = maxVal > 0 ? (val / maxVal) * 100 : 0;
              return (
                <div key={i} className="flex-1 flex flex-col items-center gap-1">
                  <div
                    className="w-full rounded-t-sm transition-all"
                    style={{ height: `${height}%`, backgroundColor: 'var(--accent)', minHeight: val > 0 ? 4 : 0 }}
                  />
                  <span className="text-xs" style={{ color: 'var(--text-secondary)' }}>{val}</span>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}