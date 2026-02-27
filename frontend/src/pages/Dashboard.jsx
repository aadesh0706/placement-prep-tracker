import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { progressAPI } from '../services/api';
import { 
  AcademicCapIcon, 
  ChartBarIcon, 
  BookOpenIcon,
  VideoCameraIcon,
  FireIcon,
  ClockIcon,
  ArrowTrendingUpIcon
} from '@heroicons/react/24/outline';

const Dashboard = () => {
  const { user } = useAuth();
  const [progress, setProgress] = useState(null);
  const [weeklyData, setWeeklyData] = useState(null);

  useEffect(() => {
    fetchProgress();
    fetchWeeklyAnalytics();
  }, []);

  const fetchProgress = async () => {
    try {
      const data = await progressAPI.getProgress();
      setProgress(data);
    } catch (error) {
      console.error('Error fetching progress:', error);
    }
  };

  const fetchWeeklyAnalytics = async () => {
    try {
      const data = await progressAPI.getWeeklyAnalytics();
      setWeeklyData(data);
    } catch (error) {
      console.error('Error fetching analytics:', error);
    }
  };

  const stats = [
    { 
      name: 'Overall Progress', 
      value: progress?.overallProgress || 0, 
      icon: ArrowTrendingUpIcon,
      color: 'bg-blue-500' 
    },
    { 
      name: 'Study Hours', 
      value: progress?.totalStudyHours || 0, 
      icon: ClockIcon,
      color: 'bg-green-500' 
    },
    { 
      name: 'Quizzes Completed', 
      value: progress?.quizzesCompleted || 0, 
      icon: BookOpenIcon,
      color: 'bg-purple-500' 
    },
    { 
      name: 'Current Streak', 
      value: progress?.currentStreak || 0, 
      icon: FireIcon,
      color: 'bg-orange-500' 
    },
  ];

  return (
    <div className="space-y-6">
      {/* Welcome Header */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 rounded-2xl p-8 text-white">
        <h1 className="text-3xl font-bold">
          Welcome back, {user?.firstName}! 👋
        </h1>
        <p className="mt-2 text-primary-100">
          Ready to continue your placement preparation journey?
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => (
          <div key={stat.name} className="card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500">{stat.name}</p>
                <p className="text-3xl font-bold mt-1">{stat.value}</p>
              </div>
              <div className={`p-3 rounded-lg ${stat.color}`}>
                <stat.icon className="w-6 h-6 text-white" />
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Link to="/quizzes" className="card hover:shadow-lg transition-shadow">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-blue-100 rounded-lg">
              <BookOpenIcon className="w-8 h-8 text-blue-600" />
            </div>
            <div>
              <h3 className="font-semibold text-lg">Take a Quiz</h3>
              <p className="text-gray-500 text-sm">Test your knowledge</p>
            </div>
          </div>
        </Link>

        <Link to="/interviews" className="card hover:shadow-lg transition-shadow">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-purple-100 rounded-lg">
              <VideoCameraIcon className="w-8 h-8 text-purple-600" />
            </div>
            <div>
              <h3 className="font-semibold text-lg">Mock Interview</h3>
              <p className="text-gray-500 text-sm">Practice with AI</p>
            </div>
          </div>
        </Link>

        <Link to="/roadmap" className="card hover:shadow-lg transition-shadow">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-green-100 rounded-lg">
              <AcademicCapIcon className="w-8 h-8 text-green-600" />
            </div>
            <div>
              <h3 className="font-semibold text-lg">Study Roadmap</h3>
              <p className="text-gray-500 text-sm">Your personalized plan</p>
            </div>
          </div>
        </Link>
      </div>

      {/* Progress Overview */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Weak Areas */}
        <div className="card">
          <h3 className="font-semibold text-lg mb-4">Areas to Improve</h3>
          {progress?.weakAreas?.length > 0 ? (
            <div className="space-y-2">
              {progress.weakAreas.map((area) => (
                <div key={area} className="flex items-center justify-between p-3 bg-red-50 rounded-lg">
                  <span className="text-red-700">{area}</span>
                  <Link to={`/quizzes?category=${area}`} className="text-sm text-red-600 hover:underline">
                    Practice →
                  </Link>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500">Complete some quizzes to see your weak areas</p>
          )}
        </div>

        {/* Strong Areas */}
        <div className="card">
          <h3 className="font-semibold text-lg mb-4">Your Strengths</h3>
          {progress?.strongAreas?.length > 0 ? (
            <div className="space-y-2">
              {progress.strongAreas.map((area) => (
                <div key={area} className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                  <span className="text-green-700">{area}</span>
                  <span className="text-sm text-green-600">✓ Strong</span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500">Complete some quizzes to see your strengths</p>
          )}
        </div>
      </div>

      {/* Weekly Activity */}
      {weeklyData && (
        <div className="card">
          <h3 className="font-semibold text-lg mb-4">This Week's Activity</h3>
          <div className="grid grid-cols-7 gap-2">
            {Object.entries(weeklyData.dailyStudyTime || {}).map(([day, minutes]) => (
              <div key={day} className="text-center">
                <div 
                  className="h-24 bg-primary-100 rounded-lg flex items-end justify-center"
                  style={{ height: `${Math.min(100, (minutes / 120) * 100)}%` }}
                >
                  <span className="text-xs text-primary-600 mb-2">{minutes}m</span>
                </div>
                <p className="text-xs text-gray-500 mt-2">{day}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
