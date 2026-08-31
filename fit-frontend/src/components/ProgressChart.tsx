import { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

interface ProgressData {
  date: string;
  weight: number | null;
  trainingVolume: number;
  trainingDuration: number;
}

const PERIODS = ['7D', '30D', '90D', 'ALL'];

export default function ProgressChart() {
  const [data, setData] = useState<ProgressData[]>([]);
  const [period, setPeriod] = useState('30D');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    const token = localStorage.getItem('token');
    fetch(`/api/progress?period=${period.toLowerCase()}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((json) => setData(json.data || []))
      .finally(() => setLoading(false));
  }, [period]);

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return `${d.getMonth() + 1}/${d.getDate()}`;
  };

  if (loading) {
    return <div className="h-80 rounded-2xl animate-pulse" style={{ backgroundColor: 'var(--bg-card)' }} />;
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex gap-2 justify-center">
        {PERIODS.map((p) => (
          <button
            key={p}
            onClick={() => setPeriod(p)}
            className="px-4 py-1 rounded-lg text-sm font-medium"
            style={{
              backgroundColor: period === p ? 'var(--accent)' : 'var(--bg-card)',
              color: period === p ? '#fff' : 'var(--text-secondary)',
            }}
          >
            {p}
          </button>
        ))}
      </div>

      {data.length === 0 ? (
        <div className="text-center p-8 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)', color: 'var(--text-secondary)' }}>
          No data available
        </div>
      ) : (
        <>
          <ChartCard title="Weight" unit="kg" dataKey="weight" data={data} formatDate={formatDate} color="#007AFF" />
          <ChartCard title="Training Volume" unit="kg" dataKey="trainingVolume" data={data} formatDate={formatDate} color="#30D158" />
          <ChartCard title="Training Duration" unit="min" dataKey="trainingDuration" data={data} formatDate={formatDate} color="#FF9500" />
        </>
      )}
    </div>
  );
}

function ChartCard({
  title, unit, dataKey, data, formatDate, color,
}: {
  title: string; unit: string; dataKey: string; data: ProgressData[];
  formatDate: (d: string) => string; color: string;
}) {
  const chartData = data
    .filter((d) => (d as any)[dataKey] != null && (d as any)[dataKey] > 0)
    .map((d) => ({ ...d, date: formatDate(d.date) }));

  if (chartData.length === 0) return null;

  return (
    <div className="p-4 rounded-2xl" style={{ backgroundColor: 'var(--bg-card)' }}>
      <h3 className="text-sm font-medium mb-2" style={{ color: 'var(--text-secondary)' }}>{title}</h3>
      <ResponsiveContainer width="100%" height={160}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="var(--bg-primary)" />
          <XAxis dataKey="date" tick={{ fontSize: 11, fill: 'var(--text-secondary)' }} />
          <YAxis tick={{ fontSize: 11, fill: 'var(--text-secondary)' }} unit={unit} />
          <Tooltip
            contentStyle={{
              backgroundColor: 'var(--bg-card)',
              border: 'none',
              borderRadius: 12,
              color: 'var(--text-primary)',
            }}
          />
          <Line type="monotone" dataKey={dataKey} stroke={color} strokeWidth={2} dot={{ r: 3, fill: color }} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}