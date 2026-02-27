import { useState, useEffect } from 'react';
import { quizAPI } from '../services/api';
import {
  BookOpenIcon,
  ClockIcon,
  StarIcon,
  PlayIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowLeftIcon
} from '@heroicons/react/24/outline';

const CATEGORIES = ['ALL', 'DSA', 'APTITUDE', 'OS', 'DBMS', 'CN', 'TECHNICAL', 'HR'];
const DIFFICULTIES = ['ALL', 'EASY', 'MEDIUM', 'HARD'];

const difficultyColor = {
  EASY: 'bg-green-100 text-green-700',
  MEDIUM: 'bg-yellow-100 text-yellow-700',
  HARD: 'bg-red-100 text-red-700',
};

const Quizzes = () => {
  const [quizzes, setQuizzes] = useState([]);
  const [attempts, setAttempts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [category, setCategory] = useState('ALL');
  const [difficulty, setDifficulty] = useState('ALL');
  const [activeQuiz, setActiveQuiz] = useState(null);       // full quiz during attempt
  const [attemptId, setAttemptId] = useState(null);
  const [answers, setAnswers] = useState({});
  const [result, setResult] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [tab, setTab] = useState('quizzes'); // 'quizzes' | 'history'
  const [timeLeft, setTimeLeft] = useState(null);

  useEffect(() => { fetchData(); }, []);

  useEffect(() => {
    if (timeLeft === null || timeLeft <= 0) return;
    const t = setInterval(() => setTimeLeft(p => p - 1), 1000);
    return () => clearInterval(t);
  }, [timeLeft]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [q, a] = await Promise.all([quizAPI.getAllQuizzes(), quizAPI.getAttempts()]);
      setQuizzes(q);
      setAttempts(a);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  const filtered = quizzes.filter(q =>
    (category === 'ALL' || q.category === category) &&
    (difficulty === 'ALL' || q.difficulty === difficulty)
  );

  const startQuiz = async (quiz) => {
    try {
      const [attempt, fullQuiz] = await Promise.all([
        quizAPI.startQuiz(quiz.id),
        quizAPI.getQuizById(quiz.id),
      ]);
      setAttemptId(attempt.id);
      setActiveQuiz(fullQuiz);
      setAnswers({});
      setResult(null);
      if (fullQuiz.timeLimit) setTimeLeft(fullQuiz.timeLimit * 60);
    } catch (e) { alert('Failed to start quiz'); }
  };

  const submitQuiz = async () => {
    setSubmitting(true);
    try {
      const answerList = Object.entries(answers).map(([qi, selectedOptionIndex]) => ({
        questionId: qi,
        selectedOptionIndex
      }));
      const res = await quizAPI.submitQuiz(attemptId, answerList);
      setResult(res);
      setActiveQuiz(null);
      setTimeLeft(null);
      fetchData();
    } catch (e) { alert('Failed to submit'); }
    finally { setSubmitting(false); }
  };

  const formatTime = (s) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;

  if (loading) return <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>;

  // Active quiz taking screen
  if (activeQuiz) {
    const qs = activeQuiz.questions || [];
    const answered = Object.keys(answers).length;
    return (
      <div className="max-w-3xl mx-auto space-y-6">
        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-xl font-bold">{activeQuiz.title}</h2>
              <p className="text-sm text-gray-500">{answered} / {qs.length} answered</p>
            </div>
            {timeLeft !== null && (
              <div className={`text-lg font-mono font-bold px-4 py-2 rounded-lg ${timeLeft < 60 ? 'bg-red-100 text-red-700' : 'bg-gray-100 text-gray-700'}`}>
                {formatTime(timeLeft)}
              </div>
            )}
          </div>
          {/* Progress bar */}
          <div className="w-full bg-gray-200 rounded-full h-2 mb-6">
            <div className="bg-primary-600 h-2 rounded-full transition-all" style={{ width: `${qs.length ? (answered / qs.length) * 100 : 0}%` }} />
          </div>
        </div>

        {qs.map((q, qi) => (
          <div key={qi} className="card">
            <p className="font-medium mb-4"><span className="text-primary-600 font-bold mr-2">Q{qi + 1}.</span>{q.text}</p>
            <div className="space-y-2">
              {(q.options || []).map((opt, oi) => (
                <button
                  key={oi}
                  onClick={() => setAnswers({ ...answers, [qi]: oi })}
                  className={`w-full text-left px-4 py-3 rounded-lg border-2 transition-colors ${
                    answers[qi] === oi
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <span className="font-medium mr-2">{String.fromCharCode(65 + oi)}.</span>{opt}
                </button>
              ))}
            </div>
          </div>
        ))}

        <div className="flex space-x-3">
          <button onClick={() => { setActiveQuiz(null); setTimeLeft(null); }} className="btn btn-secondary flex-1">Cancel</button>
          <button onClick={submitQuiz} disabled={submitting} className="btn btn-primary flex-1">
            {submitting ? 'Submitting…' : `Submit (${answered}/${qs.length} answered)`}
          </button>
        </div>
      </div>
    );
  }

  // Result screen
  if (result) {
    const pct = result.percentage || 0;
    return (
      <div className="max-w-lg mx-auto space-y-6">
        <div className={`card text-center p-10 ${pct >= 50 ? 'border-green-300' : 'border-red-300'} border-2`}>
          {pct >= 50
            ? <CheckCircleIcon className="w-16 h-16 text-green-500 mx-auto mb-4" />
            : <XCircleIcon className="w-16 h-16 text-red-500 mx-auto mb-4" />}
          <h2 className="text-2xl font-bold mb-2">{pct >= 50 ? 'Well Done!' : 'Keep Practicing!'}</h2>
          <p className="text-5xl font-extrabold text-primary-600">{pct.toFixed(1)}%</p>
          <p className="text-gray-500 mt-2">{result.correctAnswers} / {result.totalQuestions} correct</p>
          <p className="text-gray-500">{result.marksObtained} / {result.totalMarks} marks</p>
        </div>
        <button onClick={() => setResult(null)} className="btn btn-primary w-full">Back to Quizzes</button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Quizzes</h1>
        <div className="flex space-x-2">
          <button onClick={() => setTab('quizzes')} className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${tab === 'quizzes' ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>Take Quiz</button>
          <button onClick={() => setTab('history')} className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${tab === 'history' ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>History ({attempts.length})</button>
        </div>
      </div>

      {tab === 'quizzes' && (
        <>
          {/* Filters */}
          <div className="card flex flex-wrap gap-4">
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1">Category</label>
              <div className="flex flex-wrap gap-2">
                {CATEGORIES.map(c => (
                  <button key={c} onClick={() => setCategory(c)}
                    className={`px-3 py-1 rounded-full text-xs font-medium ${category === c ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>{c}</button>
                ))}
              </div>
            </div>
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1">Difficulty</label>
              <div className="flex gap-2">
                {DIFFICULTIES.map(d => (
                  <button key={d} onClick={() => setDifficulty(d)}
                    className={`px-3 py-1 rounded-full text-xs font-medium ${difficulty === d ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>{d}</button>
                ))}
              </div>
            </div>
          </div>

          {filtered.length === 0 ? (
            <div className="card text-center py-12 text-gray-500">
              <BookOpenIcon className="w-12 h-12 mx-auto mb-3 text-gray-300" />
              <p>No quizzes found. Ask your TPO/admin to add some.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filtered.map(quiz => (
                <div key={quiz.id} className="card hover:shadow-lg transition-shadow">
                  <div className="flex items-start justify-between mb-3">
                    <h3 className="font-semibold text-gray-900 flex-1">{quiz.title}</h3>
                    <span className={`ml-2 text-xs font-semibold px-2 py-1 rounded-full ${difficultyColor[quiz.difficulty] || 'bg-gray-100 text-gray-600'}`}>{quiz.difficulty}</span>
                  </div>
                  <p className="text-sm text-gray-500 mb-4">{quiz.description}</p>
                  <div className="flex items-center space-x-4 text-xs text-gray-500 mb-4">
                    <span className="flex items-center"><BookOpenIcon className="w-3.5 h-3.5 mr-1" />{quiz.questionCount || '?'} questions</span>
                    <span className="flex items-center"><ClockIcon className="w-3.5 h-3.5 mr-1" />{quiz.timeLimit || '?'} min</span>
                    <span className="flex items-center"><StarIcon className="w-3.5 h-3.5 mr-1" />{quiz.totalMarks} marks</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-xs bg-primary-50 text-primary-600 px-2 py-1 rounded">{quiz.category}</span>
                    <button onClick={() => startQuiz(quiz)} className="flex items-center space-x-1 bg-primary-600 text-white px-3 py-1.5 rounded-lg text-sm hover:bg-primary-700">
                      <PlayIcon className="w-4 h-4" /><span>Start</span>
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {tab === 'history' && (
        <div className="space-y-3">
          {attempts.length === 0 ? (
            <div className="card text-center py-12 text-gray-500">No quiz attempts yet.</div>
          ) : (
            attempts.map(a => (
              <div key={a.id} className="card flex items-center justify-between">
                <div>
                  <p className="font-medium text-gray-900">{a.quizTitle || 'Quiz'}</p>
                  <p className="text-sm text-gray-500">{a.correctAnswers}/{a.totalQuestions} correct · {a.marksObtained}/{a.totalMarks} marks</p>
                </div>
                <div className="text-right">
                  <p className={`text-xl font-bold ${(a.percentage || 0) >= 50 ? 'text-green-600' : 'text-red-500'}`}>{(a.percentage || 0).toFixed(0)}%</p>
                  <p className="text-xs text-gray-400">{a.submittedAt ? new Date(a.submittedAt).toLocaleDateString() : ''}</p>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default Quizzes;
