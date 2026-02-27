import { useState, useEffect } from 'react';
import { nlpAPI } from '../services/api';
import {
  ChatBubbleLeftRightIcon,
  LightBulbIcon,
  CheckCircleIcon,
  ExclamationCircleIcon,
  PlusIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';

const COMMON_HR_QUESTIONS = [
  "Tell me about yourself",
  "What are your strengths and weaknesses?",
  "Why do you want to join this company?",
  "Where do you see yourself in 5 years?",
  "Why should we hire you?",
  "Tell me about a challenging situation you faced",
  "What are your salary expectations?",
  "Do you have any questions for us?",
  "Tell me about a time you worked in a team",
  "Describe a project you're proud of",
];

const HRAnswers = () => {
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [result, setResult] = useState(null);

  useEffect(() => { fetchAnswers(); }, []);

  const fetchAnswers = async () => {
    setLoading(true);
    try {
      const data = await nlpAPI.getHRAnswers();
      setAnswers(data);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  const submitAnswer = async (e) => {
    e.preventDefault();
    if (!question.trim() || !answer.trim()) return;
    setSubmitting(true);
    try {
      const r = await nlpAPI.analyzeHRAnswer(question, answer);
      setResult(r);
      setShowForm(false);
      fetchAnswers();
      setQuestion('');
      setAnswer('');
    } catch (e) { alert('Failed to analyze'); }
    finally { setSubmitting(false); }
  };

  const scoreColor = (s) => s >= 80 ? 'text-green-600' : s >= 60 ? 'text-yellow-600' : 'text-red-500';
  const scoreBar = (s) => s >= 80 ? 'bg-green-500' : s >= 60 ? 'bg-yellow-500' : 'bg-red-500';

  if (loading) return <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">HR Answers Practice</h1>
          <p className="text-gray-600">Practice HR questions and get AI feedback</p>
        </div>
        <button onClick={() => setShowForm(true)} className="btn btn-primary flex items-center space-x-1">
          <PlusIcon className="w-4 h-4" /><span>Practice Answer</span>
        </button>
      </div>

      {/* Quick Questions */}
      <div className="card">
        <h2 className="font-semibold text-sm mb-3">Common HR Questions</h2>
        <div className="flex flex-wrap gap-2">
          {COMMON_HR_QUESTIONS.slice(0, 6).map((q, i) => (
            <button
              key={i}
              onClick={() => { setQuestion(q); setShowForm(true); }}
              className="text-xs bg-primary-50 text-primary-700 px-3 py-1.5 rounded-full hover:bg-primary-100 transition-colors"
            >
              {q.length > 30 ? q.slice(0, 30) + '...' : q}
            </button>
          ))}
        </div>
      </div>

      {/* New Answer Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg">
            <div className="flex items-center justify-between p-6 border-b">
              <h2 className="text-lg font-semibold">Practice HR Answer</h2>
              <button onClick={() => setShowForm(false)}><XMarkIcon className="w-5 h-5 text-gray-500" /></button>
            </div>
            <form onSubmit={submitAnswer} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Question</label>
                <input
                  type="text"
                  value={question}
                  onChange={e => setQuestion(e.target.value)}
                  className="input"
                  placeholder="Enter HR question..."
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Your Answer</label>
                <textarea
                  value={answer}
                  onChange={e => setAnswer(e.target.value)}
                  className="input h-32 resize-none"
                  placeholder="Type your answer here..."
                  required
                />
                <p className="text-xs text-gray-400 mt-1">{answer.length} characters</p>
              </div>
              <div className="flex space-x-3">
                <button type="button" onClick={() => setShowForm(false)} className="btn btn-secondary flex-1">Cancel</button>
                <button type="submit" disabled={submitting || !answer.trim()} className="btn btn-primary flex-1">
                  {submitting ? 'Analyzing...' : 'Get Feedback'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Result Modal */}
      {result && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg">
            <div className="p-6 border-b">
              <h2 className="text-lg font-semibold">AI Feedback</h2>
            </div>
            <div className="p-6 space-y-4">
              {/* Score */}
              <div className="text-center">
                <p className="text-xs font-semibold text-gray-500 mb-1">OVERALL SCORE</p>
                <p className={`text-5xl font-extrabold ${scoreColor(result.score)}`}>{result.score}<span className="text-xl text-gray-400">/100</span></p>
                <div className="w-full bg-gray-200 rounded-full h-3 mt-3">
                  <div className={`h-3 rounded-full ${scoreBar(result.score)}`} style={{ width: `${result.score}%` }} />
                </div>
              </div>

              {/* Feedback */}
              <div className="bg-blue-50 rounded-lg p-4">
                <div className="flex items-center mb-2">
                  <LightBulbIcon className="w-5 h-5 text-blue-600 mr-2" />
                  <span className="font-semibold text-blue-800">Feedback</span>
                </div>
                <p className="text-sm text-blue-700">{result.feedback}</p>
              </div>

              {/* Suggestions */}
              {result.suggestions?.length > 0 && (
                <div>
                  <p className="text-xs font-semibold text-gray-500 mb-2">SUGGESTIONS</p>
                  <ul className="space-y-2">
                    {result.suggestions.map((s, i) => (
                      <li key={i} className="text-sm text-gray-700 flex items-start">
                        <span className="text-primary-500 mr-2 mt-0.5">→</span>{s}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              <button onClick={() => setResult(null)} className="btn btn-primary w-full">Close</button>
            </div>
          </div>
        </div>
      )}

      {/* History */}
      <div className="space-y-3">
        <h2 className="font-semibold text-gray-900">Your Practice History</h2>
        {answers.length === 0 ? (
          <div className="card text-center py-12 text-gray-500">
            <ChatBubbleLeftRightIcon className="w-12 h-12 mx-auto mb-3 text-gray-300" />
            <p>No practice answers yet. Start practicing!</p>
          </div>
        ) : (
          answers.map(a => (
            <div key={a.id} className="card">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <p className="font-medium text-gray-900">{a.question}</p>
                  <p className="text-sm text-gray-600 mt-2 line-clamp-2">{a.userAnswer}</p>
                  <p className="text-xs text-gray-400 mt-2">
                    {a.submittedAt ? new Date(a.submittedAt).toLocaleDateString() : ''}
                  </p>
                </div>
                <div className="ml-4 text-right">
                  <p className={`text-2xl font-bold ${scoreColor(a.score)}`}>{a.score}</p>
                  <p className="text-xs text-gray-400">Score</p>
                </div>
              </div>
              {a.feedback && (
                <div className="mt-3 pt-3 border-t border-gray-100">
                  <p className="text-xs font-medium text-gray-500 mb-1">Feedback:</p>
                  <p className="text-sm text-gray-700">{a.feedback}</p>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default HRAnswers;
