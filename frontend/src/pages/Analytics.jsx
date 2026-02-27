import { useState, useEffect } from 'react';
import { progressAPI } from '../services/api';
import {
  FireIcon,
  ClockIcon,
  TrophyIcon,
  BookOpenIcon,
  ChartBarIcon,
} from '@heroicons/react/24/outline';

const BAR_MAX_HEIGHT = 120; // px

const BarChart = ({ data, label }) => {
  const max = Math.max(...data.map(d => d.value), 1);
  return (
    <div className="flex items-end space-x-2 h-36 pt-4">
      {data.map((d, i) => (
        <div key={i} className="flex-1 flex flex-col items-center">
          <span className="text-xs text-gray-500 mb-1">{d.value > 0 ? d.value : ''}</span>
          <div
            className="w-full bg-primary-500 rounded-t-md transition-all hover:bg-primary-600"
            style={{ height: `${Math.max((d.value / max) * BAR_MAX_HEIGHT, d.value > 0 ? 4 : 0)}px` }}
            title={`${d.label}: ${d.value} ${label}`}
          />
          <span className="text-xs text-gray-400 mt-1 truncate w-full text-center">{d.label}</span>
        </div>
      ))}
    </div>
  );
};

const Analytics = () => {
  const [progress, setProgress] = useState(null);
  const [weekly, setWeekly] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [p, w] = await Promise.all([progressAPI.getProgress(), progressAPI.getWeeklyAnalytics()]);
      setProgress(p);
      setWeekly(w);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  if (loading) return <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>;

  const weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  const studyHoursData = weekDays.map((day, i) => ({
    label: day,
    value: weekly?.dailyStudyHours?.[i] ?? 0,
  }));

  const categoryData = Object.entries(progress?.categoryProgress || {}).map(([k, v]) => ({
    label: k.length > 5 ? k.slice(0, 5) : k,
    value: Math.round(v),
  }));

  const quizTrend = (weekly?.quizScores || []).map((s, i) => ({
    label: `Q${i + 1}`,
    value: Math.round(s),
  }));

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Analytics</h1>

      {/* KPI cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <StatCard icon={FireIcon} color="text-orange-500 bg-orange-50" label="Current Streak" value={`${progress?.currentStreak ?? 0} days`} />
        <StatCard icon={TrophyIcon} color="text-yellow-500 bg-yellow-50" label="Longest Streak" value={`${progress?.longestStreak ?? 0} days`} />
        <StatCard icon={ClockIcon} color="text-blue-500 bg-blue-50" label="Total Study Hours" value={`${Math.round(progress?.totalStudyHours ?? 0)}h`} />
        <StatCard icon={BookOpenIcon} color="text-green-500 bg-green-50" label="Quizzes Taken" value={progress?.totalQuizzesTaken ?? 0} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Weekly hours */}
        <div className="card">
          <div className="flex items-center justify-between mb-2">
            <h2 className="font-semibold text-gray-900">Weekly Study Hours</h2>
            <span className="text-xs text-gray-500">this week</span>
          </div>
          <BarChart data={studyHoursData} label="hrs" />
        </div>

        {/* Category progress */}
        <div className="card">
          <div className="flex items-center justify-between mb-2">
            <h2 className="font-semibold text-gray-900">Category Progress (%)</h2>
          </div>
          {categoryData.length === 0 ? (
            <p className="text-sm text-gray-400 py-8 text-center">No category data yet.</p>
          ) : (
            <div className="space-y-3 mt-2">
              {categoryData.map((c, i) => (
                <div key={i}>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-700">{c.label}</span>
                    <span className="font-medium text-primary-600">{c.value}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-primary-500 h-2 rounded-full" style={{ width: `${c.value}%` }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Quiz score trend */}
        <div className="card">
          <h2 className="font-semibold text-gray-900 mb-2">Recent Quiz Scores (%)</h2>
          {quizTrend.length === 0 ? (
            <p className="text-sm text-gray-400 py-8 text-center">No quiz scores yet.</p>
          ) : (
            <BarChart data={quizTrend} label="%" />
          )}
        </div>

        {/* Overall progress */}
        <div className="card flex flex-col justify-between">
          <h2 className="font-semibold text-gray-900 mb-4">Overall Progress</h2>
          <div className="flex items-center justify-center flex-1">
            <div className="relative w-36 h-36">
              <svg viewBox="0 0 36 36" className="w-36 h-36 -rotate-90">
                <circle cx="18" cy="18" r="15.9" fill="none" stroke="#e5e7eb" strokeWidth="3" />
                <circle cx="18" cy="18" r="15.9" fill="none" stroke="#6366f1" strokeWidth="3"
                  strokeDasharray={`${(progress?.overallProgress ?? 0).toFixed(1)}, 100`}
                  strokeLinecap="round" />
              </svg>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <p className="text-2xl font-extrabold text-primary-600">{Math.round(progress?.overallProgress ?? 0)}%</p>
                <p className="text-xs text-gray-400">Overall</p>
              </div>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3 mt-4">
            <div className="bg-gray-50 rounded-lg p-3 text-center">
              <p className="text-lg font-bold text-gray-900">{progress?.totalSessionsCompleted ?? 0}</p>
              <p className="text-xs text-gray-500">Sessions</p>
            </div>
            <div className="bg-gray-50 rounded-lg p-3 text-center">
              <p className="text-lg font-bold text-gray-900">{Math.round(progress?.averageQuizScore ?? 0)}%</p>
              <p className="text-xs text-gray-500">Avg Quiz Score</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const StatCard = ({ icon: Icon, color, label, value }) => (
  <div className="card flex items-center space-x-3">
    <div className={`p-2 rounded-lg ${color}`}><Icon className="w-5 h-5" /></div>
    <div>
      <p className="text-lg font-bold text-gray-900">{value}</p>
      <p className="text-xs text-gray-500">{label}</p>
    </div>
  </div>
);

export default Analytics;
